package com.roboticswala.hub

import android.app.Application

class RoboticsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RoboticsApp
            private set
    }
}
