package com.example.dropspot.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.AppUser
import com.example.dropspot.network.AuthInterceptor
import com.example.dropspot.network.UserService
import kotlinx.coroutines.launch

class UserViewModel(private val userService: UserService) : ViewModel() {

    private val _isTokenExpired = MutableLiveData<Boolean>()
    val isTokenExpired: LiveData<Boolean> get() = _isTokenExpired

    private val _currentUser = MutableLiveData<AppUser>()
    val currentUser: LiveData<AppUser> get() = _currentUser

    fun setCurrentUser(token: String) {
        AuthInterceptor.setSessionToken(token)
        viewModelScope.launch {
            try {
                val response = userService.getMe()
                if (response.code() == 200) {
                    _currentUser.value = response.body()
                } else {
                    if (response.code() == 401) {
                        AuthInterceptor.clearSessionToken()
                        _isTokenExpired.value = true
                    }
                }
            } catch (e: Throwable) {
                Log.i("current_user_req", e.message ?: "fail")
            }
        }
    }


}