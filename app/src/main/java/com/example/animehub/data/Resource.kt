package com.example.animehub.data


sealed class Resource<out R> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data :T):Resource<T>()
    data class Failure(val error: String):Resource<Nothing>()
}