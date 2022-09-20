package com.ipath.hospitaldevice.ble.medicaldevice.bean

import java.io.Serializable

/**
 * 设备信息实体类 Device information entity
 * Created by linyb on 20/12/24.
 */
class DeviceParamsBean : Serializable {
    var type: String? = null
    var tag: Byte = 0
    var size = 0
    var explain: String? = null
}