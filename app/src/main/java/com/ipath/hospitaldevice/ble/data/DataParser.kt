package com.ipath.hospitaldevice.ble.data

import android.util.Log
import com.berry_med.spo2_ble.data.Const
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
    private var type: String = ""

    //Parse Runnable
    private var mParseRunnable: ParseRunnable? = null
    private var isStop = true
    private var DataUpdated = true
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
        isStop = false

    }

    /**
     * ParseRunnable
     */
    internal inner class ParseRunnable : Runnable {
        var dat = bufferQueue
        lateinit var packageData: IntArray
        override fun run() {
            while (isStop) {
                if(DataUpdated) {
                    dat = data
                    if (type.equals(Const.Oximeter)) {
                        packageData = IntArray(10)

                        val spo2 = dat[7]
                        val pulseRate = dat[6]
                        val pi = dat[8] / 10
                        if (spo2 >= 35 && spo2 <= 100 && pulseRate <= 220) {
                            mOxiParams.update(spo2.toInt(), pulseRate.toInt(), pi, 0, 0.0, 0.0)
                            mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        }
                        DataUpdated=false
                    } else if (type.equals(Const.Glucometer)) {
                        packageData = IntArray(8)
                        val hi = dat[4]
                        val low = dat[5]
                        val hinumber = hi.toInt()
                        val lownumber = low.toInt()
                        val hinumberunsignedHex = String.format("%02X", hinumber and 0xff)
                        val lownumberunsignedHex = String.format("%02X", lownumber and 0xff)
                        val lownumberdecimal: Int = hinumberunsignedHex.toInt(16)
                        val hinumberdecimal: Int = lownumberunsignedHex.toInt(16)
                        Log.e("MybLe", lownumberdecimal.toString())
                        val glucoseMeasurement = lownumberdecimal + hinumberdecimal;
                        if (glucoseMeasurement > 0) {
                            mOxiParams.update(0, 0, 0, glucoseMeasurement, 0.0, 0.0)
                            mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        }
                        DataUpdated=false
                    } else if (type.equals(Const.Thermometer)) {
                        packageData = IntArray(7)
                        val last = dat[4]
                        val first = dat[5]
                        val lastnumber = last.toInt()
                        val firstnumber = first.toInt()
                        val lastnumbersignedHex = String.format("%02X", lastnumber and 0xff)
                        val firstnumbersignedHex = String.format("%02X", firstnumber and 0xff)
                        var hexString = firstnumbersignedHex + lastnumbersignedHex;
                        Log.e("MybLe", hexString.toString())

                        val temrature: Int = hexString.toInt(16)


                        if (firstnumber == 1) {
                            var Celcius = temrature.toDouble() / 10
                            var Fahrenheit = (Celcius * (9 / 5)) + 32
                            if (temrature > 0) {
                                mOxiParams.update(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Celcius.toDouble(),
                                    Fahrenheit.toDouble()
                                )
                                mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                            }
                        } else {
                            var Fahrenheit: Double = temrature.toDouble() / 10
                            var Celcius = ((Fahrenheit - 32) * 5) / 9
                            if (temrature > 0) {
                                mOxiParams.update(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Celcius.toDouble(),
                                    Fahrenheit.toDouble()
                                )
                                mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                            }
                        }
                        DataUpdated=false
                    }
                }
            }
        }
    }

    /**
     * Add the data received from USB or Bluetooth
     * @param dat
     */
    fun add(dat: ByteArray, type: String) {
        try {
            bufferQueue = dat
            this.type = type
            DataUpdated=true
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
        var mmolLvalue //perfusion index
                = 0
            private set
        var Celcius //perfusion index
                = 0.0
            private set
        var Fahrenheit //perfusion index
                = 0.0
            private set

        fun update(spo2: Int, pulseRate: Int, pi: Int, mmolLvalue: Int, Celcius: Double, Fahrenheit: Double) {
            this.spo2 = spo2
            this.pulseRate = pulseRate
            this.pi = pi
            this.mmolLvalue = mmolLvalue
            this.Celcius = Celcius
            this.Fahrenheit = Fahrenheit
        }
    }
}