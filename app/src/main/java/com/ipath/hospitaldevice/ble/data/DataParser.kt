package com.ipath.hospitaldevice.ble.data

import android.util.Log
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by ZXX on 2016/1/8.
 */
class DataParser     //Constructor
    (private val mPackageReceivedListener: onPackageReceivedListener) {
    //Const
    var TAG = this.javaClass.simpleName

    //Buffer queue
    private val bufferQueue = LinkedBlockingQueue<Int>(256)

    //Parse Runnable
    private var mParseRunnable: ParseRunnable? = null
    private var isStop = true
    private val mOxiParams = OxiParams()

    /**
     * interface for parameters changed.
     */
    interface onPackageReceivedListener {
        fun onOxiParamsChanged(params: OxiParams?)
        fun onPlethWaveReceived(amp: Int)
    }

    fun start() {
        mParseRunnable = ParseRunnable()
        Thread(mParseRunnable).start()
    }

    fun stop() {
        isStop = true
    }

    /**
     * ParseRunnable
     */
    internal inner class ParseRunnable : Runnable {
        var dat = 0
        lateinit var packageData: IntArray
        override fun run() {
            while (isStop) {
                dat = data
                packageData = IntArray(10)
                if (dat and 0x76 > 0) //search package head
                {
                    packageData[0] = dat
                    for (i in 1 until packageData.size) {
                        dat = data
                        if (dat and 0x80 == 0) {
                            packageData[i] = dat
                        } else {
                            continue
                        }
                    }
                    val spo2 = packageData[4]
                    val pulseRate = packageData[3] or (packageData[2] and 0x40 shl 1)
                    val pi = packageData[0] and 0x0f
                    if (spo2 <= 100 && pulseRate <= 220) {
                       if (spo2 != mOxiParams.spo2 || pulseRate != mOxiParams.pulseRate || pi != mOxiParams.pi) {
                           mOxiParams.update(spo2, pulseRate, pi)
                           mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                       }
                    }
                    mPackageReceivedListener.onPlethWaveReceived(packageData[1])
                }
            }
        }
    }

    /**
     * Add the data received from USB or Bluetooth
     * @param dat
     */
    fun add(dat: ByteArray) {
        for (b in dat) {
            try {
                bufferQueue.put(toUnsignedInt(b))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        Log.e(TAG, "add: " + Arrays.toString(dat))

        //Log.i(TAG, "add: "+ bufferQueue.size());
    }

    /**
     * Get Dat from Queue
     * @return
     */
    private val data: Int
        private get() {
            var dat = 0
            try {
                dat = bufferQueue.take()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return dat
        }

    private fun toUnsignedInt(x: Byte): Int {
        return x.toInt() and 0xFF
    }

    /**
     * a small collection of Oximeter parameters.
     * you can add more parameters as the manual.
     *
     * spo2          Pulse Oxygen Saturation
     * pulseRate     pulse rate
     * pi            perfusion index
     *
     */
    inner class OxiParams {
        var spo2 = 0
            private set
        var pulseRate = 0
            private set
        var pi //perfusion index
                = 0
            private set

         fun update(spo2: Int, pulseRate: Int, pi: Int) {
            this.spo2 = spo2
            this.pulseRate = pulseRate
            this.pi = pi
        }
    }
}