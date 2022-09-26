package com.github.plplmax.simulator.kitchen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.plplmax.simulator.order.Order

class KitchenStateOf : KitchenState {
    override var currentOrderId: UInt by mutableStateOf(0U)
    override val waitingOrders: MutableList<Order> = mutableStateListOf()
}
