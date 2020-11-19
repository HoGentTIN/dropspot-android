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
import java.net.SocketTimeoutException

class UserViewModel(private val userService: UserService) : ViewModel() {

    companion object {
        private const val TAG = "user_vm"
    }

    private val _currentUser = MutableLiveData<AppUser>()
    val currentUser: LiveData<AppUser> get() = _currentUser

    val isSessionExpired: LiveData<Boolean> = AuthInterceptor.isSessionExpired

    fun fetchUser() {
        Log.i(TAG, "fetching user...")
        viewModelScope.launch {
            try {
                val response = userService.getMe()
                Log.i(TAG, "fetched: $response")

                _currentUser.value = response

            } catch (e: SocketTimeoutException) {
                fetchUser()
                Log.i("current_user_req", "socket timeout")

            } catch (e: Throwable) {
                Log.i("current_user_req", e.message ?: "fail")
            }
        }
    }

    fun setSessionToken(token: String) {
        AuthInterceptor.setSessionToken(token)
    }


}