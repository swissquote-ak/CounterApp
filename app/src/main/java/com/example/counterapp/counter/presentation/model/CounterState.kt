package com.example.counterapp.counter.presentation.model

import com.example.counterapp.common.presentation.model.State
import com.example.counterapp.counter.domain.model.CounterEntity

data class CounterState(
    val count: Int,
) : State {
    companion object {
        fun fromEntity(entity: CounterEntity): CounterState {
            return CounterState(count = entity.count)
        }
    }
}
