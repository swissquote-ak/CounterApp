package com.example.counterapp.counter.domain.model

import com.example.counterapp.common.domain.model.Entity

data class CounterEntity(
    val count: Int,
) : Entity
