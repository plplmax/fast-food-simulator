package com.github.plplmax.simulator.restaurant

import com.github.plplmax.simulator.kitchen.Cook
import com.github.plplmax.simulator.kitchen.CookOf
import com.github.plplmax.simulator.order.OrderState
import com.github.plplmax.simulator.order.OrderTaker
import com.github.plplmax.simulator.order.OrderTakerOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RestaurantOf(
    orderState: OrderState,
    private val cook: Cook = CookOf(),
    private val taker: OrderTaker = OrderTakerOf(cook, orderState),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Restaurant {

    override fun start() {
        this.scope.launch { taker.startWork() }
    }

    override fun stop(): Unit = this.scope.cancel()
    override fun makeOrder(): Unit = this.taker.makeOrder()
}
