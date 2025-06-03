package com.example.counterapp.counter.domain

import com.example.counterapp.common.domain.UseCases
import com.example.counterapp.counter.domain.model.CounterEntity
import kotlinx.coroutines.delay

class CounterUseCases : UseCases {
    suspend fun init(): CounterEntity {
        fakeDelay()
        return CounterEntity(0)
    }

    suspend fun increment(entity: CounterEntity): CounterEntity {
        fakeDelay()
        return entity.copy(count = entity.count + 1)
    }

    suspend fun decrement(entity: CounterEntity): CounterEntity {
        fakeDelay()
        return entity.copy(count = entity.count - 1)
    }

    private suspend fun fakeDelay() {
        delay(FAKE_DELAY_MILLIS)
    }

    private companion object {
        private const val FAKE_DELAY_MILLIS = 1000L
    }
}
