package com.pnuema.android.obd

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.pnuema.android.obd.statics.ObdLibrary

@Suppress("unused") //inited in the manifest
class ObdInitializer: Initializer<Context> {
    override fun create(context: Context): Context {
        ObdLibrary.init(context as Application)
        return context
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}