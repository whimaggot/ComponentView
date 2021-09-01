package com.netease.componentviewapp

import android.app.Application
import com.netease.componentview.manager.ComponentViewManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComponentViewManager.register(this)
    }
}