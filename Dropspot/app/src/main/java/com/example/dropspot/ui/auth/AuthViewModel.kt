package com.example.dropspot.ui.auth

import android.net.ConnectivityManager
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(
    private val authService: AuthService
    , private val connectivityManager: ConnectivityManager
    , private val gson: Gson
) : ViewModel() {

    private val _loginResponse = MutableLiveData<JwtResponse>()
    val loginResponse: LiveData<JwtResponse> get() = _loginResponse

    private val _registerResponse = MutableLiveData<MessageResponse>()
    val registerResponse: LiveData<MessageResponse> get() = _registerResponse

    fun login(emailOrUsername: String, password: String) {
        val request: LoginRequest = LoginRequest(emailOrUsername, password)
        Log.i("login", request.toString())
        viewModelScope.launch {
            try {
                val call = authService.login(request)

                // support bad request responses
                call.enqueue(object : Callback<JwtResponse> {
                    override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                        throw t
                    }

                    override fun onResponse(
                        call: Call<JwtResponse>,
                        response: Response<JwtResponse>
                    ) {
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
                    }
                })

            } catch (e: Throwable) {
                Log.i(
                    "login", e.message
                        ?: "something went wrong with login request"
                )
                _loginResponse.value = JwtResponse(
                    ""
                    , -1L, "", "", listOf(), false, "Something went wrong"
                )
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
                val call = authService.register(request)

                //support bad request responses
                call.enqueue(object : Callback<MessageResponse> {
                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        throw t
                    }

                    override fun onResponse(
                        call: Call<MessageResponse>,
                        response: Response<MessageResponse>
                    ) {
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

                    }

                })
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