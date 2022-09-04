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
    private var bufferQueue = ByteArray(10)

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
        var dat = bufferQueue
        lateinit var packageData: IntArray
        override fun run() {
            while (isStop) {
                dat = data
                packageData = IntArray(10)

                val spo2 = dat[7]
                val pulseRate =  dat[6]
                val pi =dat[8]/10
                if (spo2>=35 && spo2 <= 100 && pulseRate <= 220) {
                    mOxiParams.update(spo2.toInt(), pulseRate.toInt(), pi)
                    mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                }
            }
        }
    }

    /**
     * Add the data received from USB or Bluetooth
     * @param dat
     */
    fun add(dat: ByteArray) {
            try {
                bufferQueue=dat
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        Log.e(TAG, "add: " + Arrays.toString(dat))

        //Log.i(TAG, "add: "+ bufferQueue.size());
    }

    /**
     * Get Dat from Queue
     * @return
     */
    private val data: ByteArray
        private get() {

                return bufferQueue


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