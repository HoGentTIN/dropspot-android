package com.example.dropspot.di

import com.example.dropspot.data.AppDatabase
import com.example.dropspot.data.repos.SpotDetailRepository
import com.example.dropspot.data.repos.SpotRepository
import com.example.dropspot.network.AuthInterceptor
import com.example.dropspot.network.AuthService
import com.example.dropspot.network.SpotService
import com.example.dropspot.network.UserService
import com.example.dropspot.utils.BASE_URL
import com.example.dropspot.viewmodels.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val myModule: Module = module {

    //gson
    single {
        GsonBuilder()
            .create()
    }

    //custom client with auth interceptor and logging
    single {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    //retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .build()
    }

    //services
    single {
        provideSpotService(get())
    }

    single {
        provideAuthService(get())
    }

    single {
        provideUserService(get())
    }

    //daos
    single {
        AppDatabase.getInstance(get()).spotDao()
    }
    single {
        AppDatabase.getInstance(get()).spotDetailDao()
    }

    //repos
    single {
        SpotRepository(get(), get())
    }

    single {
        SpotDetailRepository(get(), get(), get())
    }


    //viewmodels
    viewModel { HomeViewModel(get()) }
    viewModel { MeViewModel() }
    viewModel {
        AuthViewModel(
            get(),
            get()
        )
    }
    viewModel { UserViewModel(get()) }
    viewModel { SpotDetailViewModel(get()) }

}

private fun provideSpotService(retrofit: Retrofit): SpotService {
    return retrofit.create(SpotService::class.java)
}

private fun provideAuthService(retrofit: Retrofit): AuthService {
    return retrofit.create(AuthService::class.java)
}

private fun provideUserService(retrofit: Retrofit): UserService {
    return retrofit.create(UserService::class.java)
}
