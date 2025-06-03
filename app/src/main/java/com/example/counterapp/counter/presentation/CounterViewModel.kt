package com.example.counterapp.counter.presentation

import com.example.counterapp.common.presentation.BaseViewModel
import com.example.counterapp.counter.domain.CounterUseCases
import com.example.counterapp.counter.domain.model.CounterEntity
import com.example.counterapp.counter.presentation.model.CounterAction
import com.example.counterapp.counter.presentation.model.CounterState

class CounterViewModel(private val useCases: CounterUseCases = CounterUseCases()) :
    BaseViewModel<CounterAction, CounterEntity, CounterState>() {
    override suspend fun ReducerScope<CounterEntity, CounterState>.reduce(action: CounterAction) {
        updateUiState { loading() } // We start by emitting a loading state. While this might be immediate for a simple counter, it's a good general practice for actions that might involve longer operations.

        val entity = updateEntity {
            if (this == null) {
                return@updateEntity useCases.init()
            }

            when (action) {
                CounterAction.Init -> useCases.init()

                CounterAction.Increment -> useCases.increment(entity = this)

                CounterAction.Decrement -> useCases.decrement(entity = this)
            }
        }

        updateUiState { content(CounterState.fromEntity(entity)) }
    }
}
