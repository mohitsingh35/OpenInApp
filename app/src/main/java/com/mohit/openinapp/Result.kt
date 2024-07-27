package com.mohit.openinapp


data class Result<out T>(val status: Status, val data: T? = null, val message: String? = null) {
    companion object {
        fun <T> success(data: T?): Result<T> = Result(Status.SUCCESS, data)
        fun <T> error(message: String,
                      data: T? = null): Result<T> = Result(Status.ERROR, data, message)
        fun <T> loading(data: T? = null): Result<T> = Result(Status.LOADING, data)
    }
}

enum class Status { SUCCESS, ERROR, LOADING }

