package com.kos.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
class ImmutableList<T>(val list: List<T>) {
    inline fun forEach(body: (T) -> Unit) {
        list.forEach(body)
    }

    inline fun <S> map(body: (T) -> S) = list.map(body)

    fun firstOrNull() = list.firstOrNull()

    companion object {
        @Stable
        fun <T> empty() = ImmutableList<T>(emptyList())
    }
}

