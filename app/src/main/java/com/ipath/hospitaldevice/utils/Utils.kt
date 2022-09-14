package com.ipath.hospitaldevice.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat

object Utils {
    fun SetupView(title: String, color: String): String {
        var newString: String = title;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newString = "<span style='background:#" + color.substring(2) + "'>" + title + "</span>"

        } else {
            newString = "<font color='#" + color.substring(2) + "'>" + title + "</font>"

        }
        return newString;
    }

    fun SetupHtmlView(newString: String): Spanned {
        var newString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(
                newString, HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        else
            Html.fromHtml(
                newString
            )


        return newString;
    }
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