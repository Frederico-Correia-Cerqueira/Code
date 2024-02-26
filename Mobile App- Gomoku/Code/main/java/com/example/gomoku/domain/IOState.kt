package com.example.gomoku.domain

/**
 * Sum type that represents the state of a I/O operation.
 */
sealed class IOState<out T>

/**
 * The idle state, i.e. the state before the load operation is started.
 */
data object Idle : IOState<Nothing>()

/**
 * The loading state, i.e. the state while the load operation is in progress.
 */
data object Loading : IOState<Nothing>()

/**
 * The loaded state, i.e. the state after the load operation has finished.
 * @param value the result of the load operation.
 */
data class Loaded<T>(val value: Result<T>) : IOState<T>()

/**
 * Returns a new [IOState] in the idle state.
 */
fun idle(): Idle = Idle

/**
 * Returns a new [IOState] in the loading state.
 */
fun loading(): Loading = Loading

/**
 * Returns a new [IOState] in the loaded state.
 */
fun <T> loaded(value: Result<T>): Loaded<T> = Loaded(value)

/**
 * Returns the result of the I/O operation, if one is available.
 */
fun <T> IOState<T>.getOrNull(): T? = when (this) {
    is Loaded -> value.getOrNull()
    else -> null
}
