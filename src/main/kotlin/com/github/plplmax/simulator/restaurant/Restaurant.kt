package com.github.plplmax.simulator.restaurant

import kotlinx.coroutines.CoroutineScope

interface Restaurant {
    fun start(scope: CoroutineScope)
    suspend fun makeOrdersEndlessly()
}
