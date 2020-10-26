package com.example.dropspot.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

object AuthInterceptor : Interceptor {

    private var sessionToken: String = ""
    private val AUTH_NAME = "authorization"


    /**
     * sets the session token
     * */
    fun setSessionToken(sessionToken: String) {
        this.sessionToken = sessionToken
    }

    fun clearSessionToken() {
        this.sessionToken = ""
    }

    /**
     * Gets the raw session token
     * */
    fun getToken(): String? {
        return sessionToken
    }

    /**
     * intercepts the request and if it does not already contains
     * a auth header it adds the current token properly formatted
     *
     * */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        if (request.header(AUTH_NAME) == null) {
            requestBuilder.addHeader(AUTH_NAME, generateFormattedToken())
        }
        return chain.proceed(requestBuilder.build())
    }

    /**
     * Formats the token in the way the api will accept it
     * */
    fun generateFormattedToken(): String {
        return "Bearer $sessionToken"
    }

}