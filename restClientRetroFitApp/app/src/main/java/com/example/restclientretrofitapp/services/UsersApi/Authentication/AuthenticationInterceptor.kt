package com.example.restclientretrofitapp.services.UsersApi.Authentication

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor(private val authToken:String ):Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.i(TAG,"the request has been intercepted... ")
        val originalRequest: Request = chain.request()
        val newRequest = originalRequest.newBuilder().apply {
            header("Authorization",authToken )
        }.build()
        return chain.proceed(newRequest)
    }
    companion object{
        const val TAG = "AuthInterceptorL"
    }
}