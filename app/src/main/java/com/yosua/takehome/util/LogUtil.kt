package com.yosua.takehome.util

import android.util.Log

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

object LogUtil {
    fun debugLog(tag: String, debugMessage: String) {
        Log.d(tag, debugMessage)
    }

    fun errorLog(tag: String, debugMessage: String?) {
        when (debugMessage) {
            null -> Log.e(tag, "")
            else -> Log.e(tag, debugMessage)
        }
    }
}
