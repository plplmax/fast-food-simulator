package com.github.plplmax.simulator.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OrderStateOf : OrderState {
    override var customersSize: UInt by mutableStateOf(0U)
    override var currentOrderId: UInt by mutableStateOf(0U)
}
