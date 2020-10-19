package com.example.dropspot.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.dropspot.data.AppDatabase
import com.example.dropspot.data.repos.SpotRepository
import com.example.dropspot.network.AuthService
import com.example.dropspot.network.SpotService
import com.example.dropspot.ui.auth.AuthViewModel
import com.example.dropspot.ui.home.HomeViewModel
import com.example.dropspot.ui.me.MeViewModel
import com.example.dropspot.utils.BASE_URL
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule: Module = module {

    //gson
    single {
        GsonBuilder()
            .create()
    }

    //custom client with auth interceptor and logging
    single {
        OkHttpClient.Builder()
            //.addInterceptor(AuthInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    //retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(get())
            .build()
    }

    //api services
    single {
        provideSpotService(get())
    }
    single {
        provideAuthService(get())
    }
    //connectivity_service
    single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    //daos
    single {
        AppDatabase.getInstance(get()).spotDao
    }

    //repos
    single {
        SpotRepository(get(), get(), get())
    }


    //viewmodels
    viewModel { HomeViewModel(get()) }
    viewModel { MeViewModel() }
    viewModel { AuthViewModel(get(), get(), get()) }

}

private fun provideSpotService(retrofit: Retrofit): SpotService {
    return retrofit.create(SpotService::class.java)
}

private fun provideAuthService(retrofit: Retrofit): AuthService {
    return retrofit.create(AuthService::class.java)
}

