package com.ipath.hospitaldevice.ble.medicaldevice.bean

import java.io.Serializable

/**
 *
 * 设备信息实体类 Device information entity
 * Created by linyb on 20/12/24.
 */
class DeviceInfoBean : Serializable {
    var name: String? = null
    var state: String? = null
    var mac: String? = null
}