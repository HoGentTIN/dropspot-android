package com.example.dropspot

import android.app.Application
import com.example.dropspot.di.myModule
import com.example.dropspot.network.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // start network callback
        NetworkMonitor(this).startNetworkCallback()

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@MyApplication)
            modules(myModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()

        // stop network callback
        NetworkMonitor(this).stopNetworkCallback()
    }
}