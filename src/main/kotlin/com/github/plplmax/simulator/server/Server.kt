package com.github.plplmax.simulator.server

import com.github.plplmax.simulator.order.Order

interface Server {
    suspend fun startWork()
    fun completeOrder(order: Order)
    fun afterOrderMaked()
}
