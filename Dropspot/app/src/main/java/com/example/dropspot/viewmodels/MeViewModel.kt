package com.example.dropspot.viewmodels

import androidx.lifecycle.ViewModel
import com.example.dropspot.data.model.AppUser
import com.example.dropspot.network.UserService

class MeViewModel(private val userService: UserService) : ViewModel() {

    private var user: AppUser? = null

    fun setUser(user: AppUser) {
        this.user = user
    }


}