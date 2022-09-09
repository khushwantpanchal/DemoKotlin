package com.berry_med.spo2_ble.data

import java.util.*

/**
 * Created by ZXX on 2015/8/31.
 */
object Const {
    val UUID_SERVICE_DATA_Oximeter = UUID.fromString("CDEACB80-5235-4C07-8846-93A37EE6B86D")
    val UUID_CHARACTER_RECEIVE_Oximeter = UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D")

    val UUID_SERVICE_DATA_GlucoMeter = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val UUID_CHARACTER_RECEIVE_GlucoMeter = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")


    val UUID_SERVICE_DATA_Thermometer = UUID.fromString("4d540100-0074-6865-726d-6f6d65746572")
    val UUID_CHARACTER_RECEIVE_Thermometer = UUID.fromString("4d540001-0074-6865-726d-6f6d65746572")

    val Thermometer="Thermometer"
    val Glucometer="Glucometer"
    val Oximeter="Oximeter"
    val UUID_CHARACTER_RECEIVE_GlucoMeter2 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")

    val UUID_MODIFY_BT_NAME = UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D")
    val UUID_CLIENT_CHARACTER_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

}