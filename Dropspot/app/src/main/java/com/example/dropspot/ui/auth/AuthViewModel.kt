package com.example.dropspot.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dropspot.data.model.dto.requests.LoginRequest
import com.example.dropspot.data.model.dto.requests.RegisterRequest
import com.example.dropspot.network.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AuthViewModel(private val authService: AuthService) : ViewModel() {
    //coroutines
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun login(emailOrUsername: String, password: String) {
        val request: LoginRequest = LoginRequest(emailOrUsername, password)
        Log.i("login", request.toString())
        //authService.login(request)
    }

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ) {
        val request: RegisterRequest =
            RegisterRequest(firstName, lastName, username, email, password)
        Log.i("register", request.toString())
        //authService.register(request)
    }
}