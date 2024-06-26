package com.obidia.todolistapp.utils

sealed class Resource<out R> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

inline infix fun <T> Resource<T>.loading(predicate: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        predicate.invoke()
    }
    return this
}

inline infix fun <T> Resource<T>.success(predicate: (data: T) -> Unit): Resource<T> {
    if (this is Resource.Success && this.data != null) {
        predicate.invoke(data)
    }
    return this
}

inline infix fun <T> Resource<T>.error(predicate: (data: Throwable) -> Unit) {
    if (this is Resource.Error) {
        predicate.invoke(this.throwable)
    }
}