package com.github.plplmax.simulator.restaurant

import androidx.compose.runtime.MutableState
import kotlin.time.Duration

interface RestaurantState {
    var customersInterval: MutableState<Duration>
    var cookInterval: MutableState<Duration>
    var started: MutableState<Boolean>
}
