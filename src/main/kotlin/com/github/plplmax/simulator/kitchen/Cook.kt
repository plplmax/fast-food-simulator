package com.github.plplmax.simulator.kitchen

import com.github.plplmax.simulator.order.Order

interface Cook {
    suspend fun startWork()
    fun makeOrder(order: Order)
}
