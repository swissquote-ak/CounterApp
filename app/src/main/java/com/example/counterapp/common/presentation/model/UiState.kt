package com.example.counterapp.common.presentation.model

data class UiState<T : State>(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val content: T?,
    val hasError: Boolean,
) : State {
    val showLoadingScreen: Boolean
        get() = isLoading && !hasContent

    val showFullScreenError: Boolean
        get() = hasError && !hasContent

    val hasContent: Boolean
        get() = content != null

    fun loading(isRefreshing: Boolean = false, content: T? = this.content): UiState<T> {
        return this.copy(
            isLoading = true,
            isRefreshing = isRefreshing,
            content = content,
            hasError = false,
        )
    }

    fun content(content: T): UiState<T> {
        return this.copy(
            isLoading = false,
            isRefreshing = false,
            content = content,
            hasError = false,
        )
    }

    fun error(): UiState<T> {
        return this.copy(
            isLoading = false,
            isRefreshing = false,
            hasError = true,
        )
    }

    companion object {
        fun <T : State> initialLoading(): UiState<T> {
            return UiState<T>(
                isLoading = true,
                isRefreshing = false,
                content = null,
                hasError = false,
            )
        }
    }
}
