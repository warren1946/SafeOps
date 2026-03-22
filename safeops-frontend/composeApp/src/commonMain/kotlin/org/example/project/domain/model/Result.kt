package org.example.project.domain.model

/**
 * Domain-level Result type for use cases
 * This provides a clean API for the presentation layer
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun errorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, code)
        is Loading -> Loading
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (String) -> Unit): Result<T> {
        if (this is Error) action(message)
        return this
    }
    
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Extension to convert ApiResult to domain Result
 */
fun <T> org.example.project.data.remote.service.ApiResult<T>.toDomainResult(): Result<T> = when (this) {
    is org.example.project.data.remote.service.ApiResult.Success -> Result.Success(data)
    is org.example.project.data.remote.service.ApiResult.Error -> Result.Error(
        exception.message ?: "Unknown error",
        exception.statusCode
    )
}
