package com.ipath.hospitaldevice.utils

object Utils {

    fun getManufacturerSpecificData(bytes: ByteArray): ByteArray? {
        var msd: ByteArray? = null
        var i = 0
        while (i < bytes.size - 1) {
            val len = bytes[i]
            val type = bytes[i + 1]
            if (0xFF.toByte() == type) {
                // 截取厂商自定义字段
                msd = ByteArray(len - 1)
                for (j in 0 until len - 1) {
                    msd[j] = bytes[i + j + 2]
                }
                return msd
            } else {
                // 跳过其他字段
                i += len.toInt()
            }
            ++i
        }
        return msd
    }
    }