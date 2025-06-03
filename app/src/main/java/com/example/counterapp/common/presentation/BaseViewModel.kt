@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.counterapp.common.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.counterapp.common.domain.model.Entity
import com.example.counterapp.common.presentation.model.Action
import com.example.counterapp.common.presentation.model.State
import com.example.counterapp.common.presentation.model.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel<A : Action, E : Entity, S : State>() : ViewModel() {
    private val _actionFlow by lazy {
        MutableSharedFlow<A>(
            replay = 1,
            extraBufferCapacity = 64,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }

    val uiState: StateFlow<UiState<S>> by lazy {
        var state = ViewModelState<E, S>()
        _actionFlow
            .flatMapConcat { action ->
                flow<ViewModelState<E, S>> {
                    this.safeReduce(action) { state }
                }.onEach { newState ->
                    state = newState
                }
            }
            .map { newState ->
                newState.uiState
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, UiState.initialLoading())
    }

    fun setAction(action: A) {
        _actionFlow.tryEmit(action)
    }

    protected abstract suspend fun ReducerScope<E, S>.reduce(action: A)

    private suspend fun FlowCollector<ViewModelState<E, S>>.safeReduce(
        action: A,
        getCurrentState: () -> ViewModelState<E, S>,
    ) {
        try {
            val scope = object : ReducerScope<E, S> {
                override fun getEntity(): E? {
                    return getCurrentState().entity
                }

                override fun getUiState(): UiState<S> {
                    return getCurrentState().uiState
                }

                override suspend fun updateEntity(update: suspend E?.() -> E): E {
                    return getEntity().update()
                        .apply { emit(getCurrentState().copy(entity = this)) }
                }

                override suspend fun updateUiState(update: suspend UiState<S>.() -> UiState<S>): UiState<S> {
                    return getUiState().update()
                        .apply { emit(getCurrentState().copy(uiState = this)) }
                }
            }

            scope.reduce(action)
        } catch (error: Throwable) {
            Log.e("BaseViewModel", "Error during reduce operation", error)
            val errorUiState = getCurrentState().uiState.error()
            emit(getCurrentState().copy(uiState = errorUiState))
        }
    }

    private data class ViewModelState<E : Entity, S : State>(
        val entity: E? = null,
        val uiState: UiState<S> = UiState.initialLoading(),
    )

    interface ReducerScope<E : Entity, S : State> {
        fun getEntity(): E?
        fun getUiState(): UiState<S>

        suspend fun updateEntity(update: suspend E?.() -> E): E
        suspend fun updateUiState(update: suspend UiState<S>.() -> UiState<S>): UiState<S>
    }
}
