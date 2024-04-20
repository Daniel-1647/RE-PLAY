package com.daniel.replay.service

import android.util.Log


class LogService private constructor() {

    companion object {
        private var instance: LogService? = null

        fun getInstance(): LogService {
            if (instance == null) {
                instance = LogService()
            }
            return instance!!
        }
    }

    val TAG = "REPLAY_LOGS"

    fun LOGD(msg: String){
        Log.d(TAG, msg)
    }

    fun LOGE(msg: String){
        Log.e(TAG, msg)
    }

}