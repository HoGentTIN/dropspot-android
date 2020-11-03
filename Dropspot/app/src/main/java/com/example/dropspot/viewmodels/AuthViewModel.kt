package com.example.dropspot.viewmodels

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
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class AuthViewModel(
    private val authService: AuthService
    , private val gson: Gson
) : ViewModel() {

    private val _loginResponse = MutableLiveData<JwtResponse>()
    val loginResponse: LiveData<JwtResponse> get() = _loginResponse

    private val _registerResponse = MutableLiveData<MessageResponse>()
    val registerResponse: LiveData<MessageResponse> get() = _registerResponse

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean> get() = _spinner

    fun login(emailOrUsername: String, password: String) {
        // start wheel
        _spinner.value = true

        val request = LoginRequest(emailOrUsername, password)

        viewModelScope.launch {
            try {
                val response = authService.login(request)
                if (response.code() == 200) {
                    _loginResponse.value = response.body()
                } else {
                    if (response.code() == 400) {
                        _loginResponse.value =
                            gson.fromJson(
                                response.errorBody()!!.string()
                                , JwtResponse::class.java
                            )
                    }
                }

            } catch (e: SocketTimeoutException) {
                login(emailOrUsername, password)
                Log.i("login_req", "socket timeout")
            } catch (e: Throwable) {
                _loginResponse.value = JwtResponse(
                    ""
                    , -1L, "", "", listOf(), false, "Something went wrong"
                )
            } finally {
                // end wheel
                _spinner.value = false
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
        // start wheel
        _spinner.value = true
        val request =
            RegisterRequest(firstName, lastName, username, email, password)
        viewModelScope.launch {
            try {
                val response = authService.register(request)

                // handle bad request
                if (response.code() == 200) {
                    _registerResponse.value = response.body()
                } else {
                    if (response.code() == 400) {
                        _registerResponse.value =
                            gson.fromJson(
                                response.errorBody()!!.string()
                                , MessageResponse::class.java
                            )
                    }
                }
            } catch (e: SocketTimeoutException) {
                register(firstName, lastName, username, email, password)
                Log.i("register_req", "socket timeout")

            } catch (e: Throwable) {
                _registerResponse.value = MessageResponse(false, "Something went wrong")
            } finally {
                // end wheel
                _spinner.value = false
            }

        }
    }


}