package com.example.venues.data.local.utils

sealed class LocalResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int? = null
) {

    class Success<T>(data: T) : LocalResponse<T>(data = data)

    class Error<T>(message: String, errorCode: Int) :
        LocalResponse<T>(message = message, errorCode = errorCode)
}
