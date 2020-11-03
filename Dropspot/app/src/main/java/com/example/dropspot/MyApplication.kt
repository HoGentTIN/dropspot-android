package com.example.dropspot

import android.app.Application
import com.example.dropspot.di.myModule
import com.example.dropspot.utils.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@MyApplication)
            modules(myModule)
        }

        // start network callback
        NetworkMonitor(this).startNetworkCallback()
    }

    override fun onTerminate() {
        super.onTerminate()

        // stop network callback
        NetworkMonitor(this).stopNetworkCallback()
    }
}