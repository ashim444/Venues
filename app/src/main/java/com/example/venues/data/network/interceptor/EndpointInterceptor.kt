package com.example.venues.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class EndpointInterceptor @Inject constructor(): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        val maxRetry = 5
        while (!response.isSuccessful && response.code == INTERNAL_SERVER_ERROR && tryCount < maxRetry){
            tryCount++
            response.close()
            response = chain.proceed(request)
        }
        return response
    }
    companion object{
        //Four Square error
        private const val INTERNAL_SERVER_ERROR = 500
    }
}