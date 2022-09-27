package com.ipath.hospitaldevice.ble.data

import android.graphics.Color
import android.util.Log
import com.berry_med.spo2_ble.data.Const
import com.ipath.hospitaldevice.R
import java.util.*

/**
 * Created by ZXX on 2016/1/8.
 */
class DataParser     //Constructor
    (private val mPackageReceivedListener: onPackageReceivedListener) {
    //Const
    var TAG = this.javaClass.simpleName

    //Buffer queue
    private var bufferQueue = ByteArray(10)
    private var ecgData: String = ""
    private var type: String = ""

    //Parse Runnable
    private var mParseRunnable: ParseRunnable? = null
    private var isStop = true
    private var DataUpdated = true
    private val mOxiParams = OxiParams()
    private var readingFeedback =""

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
                if (DataUpdated) {
                    dat = data
                    if (type.equals(Const.Oximeter)) {
                        packageData = IntArray(10)

                        val spo2 = dat[7]
                        val pulseRate = dat[6]
                        val pi = dat[8] / 10
                        if (spo2 >= 35 && spo2 <= 100 && pulseRate <= 220) {
                            mOxiParams.update(
                                spo2.toInt(),
                                pulseRate.toInt(),
                                pi,
                                0,
                                0.0,
                                0.0,
                                "",
                                "",
                                "",
                                "",
                                ColorValueSp02(spo2.toInt()),
                                ColorValuepulseRate(pulseRate.toInt()),
                                Color.WHITE,
                                readingFeedback,
                            )

                            mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        }
                        DataUpdated = false
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
                            mOxiParams.update(
                                0,
                                0,
                                0,
                                glucoseMeasurement,
                                0.0,
                                0.0,
                                "",
                                "",
                                "",
                                "",
                                ColorValueGlucometer(glucoseMeasurement),
                                Color.WHITE,
                                Color.WHITE,
                                readingFeedback,

                            )
                            mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        }
                        DataUpdated = false
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
                            var Fahrenheit = (Celcius * 1.8) + 32;
                            if (temrature > 0) {
                                mOxiParams.update(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Celcius.toDouble(),
                                    Fahrenheit.toDouble(), "", "", "", "",
                                    ColorValueTemp(Fahrenheit),
                                    ColorValueTemp(Fahrenheit),
                                    Color.WHITE,
                                    readingFeedback,
                                )
                                mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                            }
                        } else {
                            var Fahrenheit: Double = temrature.toDouble() / 10
                            var Celcius = (Fahrenheit - 32) / 1.8
                            if (temrature > 0) {
                                mOxiParams.update(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Celcius.toDouble(),
                                    Fahrenheit.toDouble(), "", "", "", "",
                                    ColorValueTemp(Fahrenheit),
                                    ColorValueTemp(Fahrenheit),
                                    Color.WHITE,
                                    readingFeedback,
                                )
                                mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                            }
                        }
                        DataUpdated = false
                    } else if (type.equals(Const.ECG)) {

//                        Log.e("MybLe", hexString.toString())

                        mOxiParams.update(
                            0,
                            0,
                            0,
                            0,
                            0.0,
                            0.0, ecgData, "", "", "",
                            Color.WHITE,
                            Color.WHITE,
                            Color.WHITE,
                            "",
                        )
                        mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        DataUpdated = false
                    } else if (type.equals(Const.MedicinePillBox)) {

//                        Log.e("MybLe", hexString.toString())

                        mOxiParams.update(
                            0,
                            0,
                            0,
                            0,
                            0.0,
                            0.0, ecgData, "", "", "",
                            Color.WHITE,
                            Color.WHITE,
                            Color.WHITE,
                            "",
                        )
                        mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        DataUpdated = false
                    } else if (type.equals(Const.BloodPressure)) {

//                        Log.e("MybLe", hexString.toString())

                        mOxiParams.update(
                            0,
                            0,
                            0,
                            0,
                            0.0,
                            0.0,
                            ecgData,
                            dat[3].toString(),
                            dat[4].toString(),
                            dat[5].toString(),
                            ColorValueBpSystolic( dat[3].toInt()),
                            ColorValueBpDiastolic( dat[3].toInt()),
                            Color.WHITE,
                            readingFeedback,
                        )
                        mPackageReceivedListener.onOxiParamsChanged(mOxiParams)
                        DataUpdated = false
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
            DataUpdated = true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.e(TAG, "add: " + Arrays.toString(dat))

        //Log.i(TAG, "add: "+ bufferQueue.size());
    }

    fun addECG(dat: String, type: String) {
        try {
            ecgData = dat
            this.type = type
            DataUpdated = true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.e(TAG, "add: " + dat)

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
        var ecgData //perfusion index
                = ""
            private set
        var mmHgHigh //perfusion index
                = ""
            private set
        var mmHgLow //perfusion index
                = ""
            private set
        var beat //perfusion index
                = ""
            private set

        var color: Int//perfusion index
                = Color.WHITE
            private set
        var color2: Int//perfusion index
                = Color.WHITE
            private set

        var color3: Int//perfusion index
                = Color.WHITE
            private set

        var feedback: String//perfusion index
                = ""
            private set

        fun update(
            spo2: Int,
            pulseRate: Int,
            pi: Int,
            mmolLvalue: Int,
            Celcius: Double,
            Fahrenheit: Double,
            ecgData: String,
            mmHgHigh: String,
            mmHgLow: String,
            beat: String,
            color: Int,
            color2: Int,
            color3: Int,
            feedback: String
        ) {
            this.spo2 = spo2
            this.pulseRate = pulseRate
            this.pi = pi
            this.mmolLvalue = mmolLvalue
            this.Celcius = Celcius
            this.Fahrenheit = Fahrenheit
            this.ecgData = ecgData
            this.mmHgHigh = mmHgHigh
            this.mmHgLow = mmHgLow
            this.beat = beat
            this.color = color
            this.color2 = color2
            this.color3 = color3
            this.feedback = feedback
        }
    }

    fun ColorValueSp02(spo2: Int): Int {
        if (spo2 <= 92 ) {
            readingFeedback= "Need urgent medical advice - call 999"
            return Color.RED
        } else if ((spo2 == 93 || spo2 == 94) ) {
            readingFeedback= "Seek advice from your GP"
            return Color.YELLOW
        } else if (spo2 == 95 ) {
            readingFeedback= "Acceptable to continue home monitoring"
            return Color.GREEN
        } else {
          readingFeedback= "Normal reading"
            return Color.WHITE
        }
    }

    fun ColorValueGlucometer(temp: Int): Int {
        if ((temp >= 220 && temp <= 300) ) {
            readingFeedback= "Diabetic"
            return Color.RED
        } else if (temp >=190&& temp <=230 ) {
            readingFeedback= "Impaired Glucose"
            return Color.YELLOW
        } else {
          readingFeedback= "Normal"
            return Color.GREEN
        }
    }

    fun ColorValueBpSystolic(temp: Int): Int {
        if ((temp >= 140 ) ) {
            readingFeedback= "HYPERTENSIVE CRISIS (consult your doctor immediately)"
            return Color.RED
        }  else  if ((temp >= 140 ) ) {
            readingFeedback= "HIGH BLOOD PRESSURE(HYPERTENSION) STAGE 2"
            return Color.parseColor("#BD534C")
        } else if ((temp >= 130 && temp <= 139) ) {
            readingFeedback= "HIGH BLOOD PRESSURE(HYPERTENSION) STAGE 1"
            return Color.parseColor("#CC7E0A")
        } else if (temp >=120&& temp <=129 ) {
            readingFeedback= "ELEVATED"
            return Color.YELLOW
        } else {
            readingFeedback= "Normal"
            return Color.GREEN
        }
    }
    fun ColorValueBpDiastolic(temp: Int): Int {
        if ((temp >= 120 ) ) {
            readingFeedback= "HYPERTENSIVE CRISIS (consult your doctor immediately)"
            return Color.RED
        }else if ((temp >= 90 ) ) {
            readingFeedback= "HIGH BLOOD PRESSURE(HYPERTENSION) STAGE 2"
            return Color.parseColor("#BD534C")
        } else if (temp >=80&& temp <=89 ) {
            readingFeedback= "HIGH BLOOD PRESSURE(HYPERTENSION) STAGE 1"
            return Color.parseColor("#CC7E0A")
        }  else {
            readingFeedback= "Normal"
            return Color.GREEN
        }
    }

    fun ColorValuepulseRate( pulseRate: Int): Int {
        if ( pulseRate > 130) {
            return Color.RED
        } else if ( pulseRate > 109 && pulseRate < 131) {
            return Color.YELLOW
        } else if ( (pulseRate > 100 && pulseRate < 110)) {
            return Color.GREEN
        } else {
            return Color.WHITE
        }
    }

    fun ColorValueTemp(temp: Double): Int {
        if (temp >= 103) {
            readingFeedback= "High fever CALL YOUR DOCTOR"
            return Color.RED
        } else if (temp >= 100.4 && temp < 103) {
            readingFeedback= "Fever"
            return Color.YELLOW
        } else if (temp >= 98.6 && temp < 100.4) {
            readingFeedback= "Normal or low grade fever"
            return Color.GREEN
        }else if (temp >= 97 && temp < 98.6) {
            readingFeedback= "Normal"
            return Color.WHITE
        }else if (temp >= 95.1 && temp <= 96.9) {
            readingFeedback= "Low but possible normal"
            return Color.GREEN
        }else if (temp <= 95) {
            readingFeedback= "Hypothermia SEEK CARE"
            return Color.YELLOW
        } else {
            readingFeedback= ""
            return Color.WHITE
        }
    }
}