package com.example.dropspot.data.model


import java.io.Serializable


data class Role(    private val roleId: Long,
                    private var roleName: RoleName,
                    private var description: String = "undefined") : Serializable {

}