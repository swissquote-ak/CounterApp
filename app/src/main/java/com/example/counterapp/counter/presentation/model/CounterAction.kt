package com.example.counterapp.counter.presentation.model

import com.example.counterapp.common.presentation.model.Action

sealed class CounterAction() : Action {
    data object Init : CounterAction()
    data object Increment : CounterAction()
    data object Decrement : CounterAction()
}
