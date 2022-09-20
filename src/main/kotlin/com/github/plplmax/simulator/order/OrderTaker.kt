package com.github.plplmax.simulator.order

interface OrderTaker {
    suspend fun startWork()
    fun makeOrder()
    fun waitingCustomers(): Int
    fun currentOrderId(): Int
}
