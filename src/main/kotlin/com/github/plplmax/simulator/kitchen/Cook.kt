package com.github.plplmax.simulator.kitchen

import com.github.plplmax.simulator.order.Order

interface Cook {
    fun makeOrder(order: Order)
}
