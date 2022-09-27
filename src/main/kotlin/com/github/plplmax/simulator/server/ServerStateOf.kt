package com.github.plplmax.simulator.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ServerStateOf : ServerState {
    override var currentOrderId: UInt by mutableStateOf(0U)
    override var customersSize: UInt by mutableStateOf(0U)
}
