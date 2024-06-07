package com.example.kumamon.android

sealed interface Result<out T: Any> {
    data class Success<out T:Any>(val value: T): Result<T>

    data class Failure(val ex: Exception): Result<Nothing>

    data object Loading : Result<Nothing>
}
