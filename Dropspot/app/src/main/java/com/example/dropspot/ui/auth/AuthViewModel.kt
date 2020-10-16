package com.example.dropspot.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.dto.requests.LoginRequest
import com.example.dropspot.data.model.dto.requests.RegisterRequest
import com.example.dropspot.data.model.dto.responses.JwtResponse
import com.example.dropspot.data.model.dto.responses.MessageResponse
import com.example.dropspot.network.AuthService
import kotlinx.coroutines.launch

class AuthViewModel(private val authService: AuthService) : ViewModel() {

    private val _loginResponse = MutableLiveData<JwtResponse>()
    val loginResponse: LiveData<JwtResponse> get() = _loginResponse

    private val _registerResponse = MutableLiveData<MessageResponse>()
    val registerResponse: LiveData<MessageResponse> get() = _registerResponse

    fun login(emailOrUsername: String, password: String) {
        val request: LoginRequest = LoginRequest(emailOrUsername, password)
        Log.i("login", request.toString())
        viewModelScope.launch {
            try {
                _loginResponse.value = authService.login(request).await()
                Log.i("login", _loginResponse.value.toString())
            } catch (e: Throwable) {
                Log.i(
                    "login", e.message
                        ?: "something went wrong with login request"
                )
                _loginResponse.value = JwtResponse()
            }

        }
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
        viewModelScope.launch {
            try {
                _registerResponse.value = authService.register(request).await()
                Log.i("register", _registerResponse.value.toString())
            } catch (e: Throwable) {
                Log.i(
                    "register", e.message
                        ?: "something went wrong with register request"
                )
                _registerResponse.value = MessageResponse(false, "register failed")
            }
        }
    }

}