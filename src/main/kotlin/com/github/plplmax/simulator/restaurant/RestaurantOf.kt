package com.github.plplmax.simulator.restaurant

import com.github.plplmax.simulator.kitchen.Cook
import com.github.plplmax.simulator.kitchen.CookOf
import com.github.plplmax.simulator.order.OrderState
import com.github.plplmax.simulator.order.OrderTaker
import com.github.plplmax.simulator.order.OrderTakerOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantOf(
    orderState: OrderState,
    private val cook: Cook = CookOf(),
    private val taker: OrderTaker = OrderTakerOf(cook, orderState),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Restaurant {

    override fun start(scope: CoroutineScope) {
        scope.launch(dispatcher) { taker.startWork() }
    }

    override suspend fun makeOrdersEndlessly(interval: Long) {
        withContext(dispatcher) {
            while (isActive) {
                taker.makeOrder()
                delay(interval)
            }
        }
    }
}
