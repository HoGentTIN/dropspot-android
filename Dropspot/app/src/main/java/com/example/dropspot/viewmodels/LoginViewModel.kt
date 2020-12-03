package com.example.dropspot.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.requests.LoginRequest
import com.example.dropspot.data.model.responses.JwtResponse
import com.example.dropspot.network.AuthService
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class LoginViewModel(
    private val authService: AuthService
    , private val gson: Gson
) : ViewModel() {

    private val _loginResponse = MutableLiveData<JwtResponse>()
    val loginResponse: LiveData<JwtResponse> get() = _loginResponse

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
                _spinner.value = false
            } catch (e: SocketTimeoutException) {
                login(emailOrUsername, password)
                Log.i("login_req", "socket timeout")
            } catch (e: Throwable) {
                _loginResponse.value =
                    JwtResponse(
                        ""
                        , -1L, "", "", listOf(), false, "Something went wrong"
                    )
                _spinner.value = false
            }

        }
    }




}