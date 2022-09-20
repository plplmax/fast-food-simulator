package com.github.plplmax.simulator.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OrderStateOf : OrderState {
    override var customersSize: Int by mutableStateOf(0)
    override var currentOrderId: Int by mutableStateOf(0)
}
