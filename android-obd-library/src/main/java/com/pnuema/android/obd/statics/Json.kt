package com.pnuema.android.obd.statics

import com.squareup.moshi.Moshi

import java.io.IOException

object Json {
    private val moshi: Moshi by lazy { Moshi.Builder().build() }

    fun <T> toJson(clazz: Class<T>, obj: T): String {
        return moshi.adapter(clazz).toJson(obj)
    }

    @Throws(IOException::class)
    fun <T> fromJson(clazz: Class<T>, obj: String): T? {
        return moshi.adapter(clazz).fromJson(obj)
    }

    fun getFormattedError(ex: Exception): String {
        return "{\"errorMessage\":\"" + ex.message + "\"}"
    }
}
