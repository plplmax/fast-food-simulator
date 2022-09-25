package com.github.plplmax.simulator.kitchen

import com.github.plplmax.simulator.order.Order

interface KitchenState {
    var currentOrderId: Int
    val waitingOrders: MutableList<Order>
}
