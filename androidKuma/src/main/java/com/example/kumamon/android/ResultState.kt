package com.example.kumamon.android

sealed interface ResultState<out T: Any> {
    sealed class Success<out T: Any>: ResultState<T> {
        data class NonEmpty<out T: Any>(val value: T): Success<T>()
        object Empty: Success<Nothing>()
    }
    data class Failure(val ex: Exception): ResultState<Nothing>
    object Loading: ResultState<Nothing>
}