package com.github.ykrank.androidtools.data

/**
 * Created by yuanke on 7/16/24
 * @author yuanke.ykrank@bytedance.com
 */
sealed class
Resource<T>(
    val source: Source,
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(source: Source, data: T) : Resource<T>(source, data)

    class Error<T>(source: Source, throwable: Throwable, data: T? = null) :
        Resource<T>(source, data, throwable)

    companion object {
        fun <T> fromResult(source: Source, result: Result<T>): Resource<T> {
            if (result.isSuccess) {
                return Success(source, result.getOrThrow())
            }
            return Error(source, result.exceptionOrNull()!!, result.getOrNull())
        }
    }
}