package com.github.plplmax.simulator.restaurant

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class RestaurantStateOf(
    override var customersInterval: MutableState<Duration> = mutableStateOf((5).toDuration(DurationUnit.SECONDS)),
    override var cookInterval: MutableState<Duration> = mutableStateOf((5).toDuration(DurationUnit.SECONDS)),
    override var started: MutableState<Boolean> = mutableStateOf(false)
) : RestaurantState {
}
