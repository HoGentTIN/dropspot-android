package com.example.dropspot.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.requests.RegisterRequest
import com.example.dropspot.data.model.responses.MessageResponse
import com.example.dropspot.network.AuthService
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class RegisterViewModel(
    private val authService: AuthService
    , private val gson: Gson
) : ViewModel() {

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean> get() = _spinner

    private val _registerResponse = MutableLiveData<MessageResponse>()
    val registerResponse: LiveData<MessageResponse> get() = _registerResponse

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
                _spinner.value = false
            } catch (e: SocketTimeoutException) {
                register(firstName, lastName, username, email, password)
                Log.i("register_req", "socket timeout")

            } catch (e: Throwable) {
                _registerResponse.value =
                    MessageResponse(
                        false,
                        "Something went wrong"
                    )
                _spinner.value = false
            }

        }
    }
}