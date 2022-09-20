package com.github.plplmax.simulator.order

interface OrderTaker {
    suspend fun startWork()
    fun makeOrder()
}
