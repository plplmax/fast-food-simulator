package com.github.plplmax.simulator.restaurant

import com.github.plplmax.simulator.kitchen.Cook
import com.github.plplmax.simulator.kitchen.CookOf
import com.github.plplmax.simulator.kitchen.KitchenState
import com.github.plplmax.simulator.order.OrderState
import com.github.plplmax.simulator.order.OrderTaker
import com.github.plplmax.simulator.order.OrderTakerOf
import com.github.plplmax.simulator.server.Server
import com.github.plplmax.simulator.server.ServerOf
import com.github.plplmax.simulator.server.ServerState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantOf(
    orderState: OrderState,
    kitchenState: KitchenState,
    serverState: ServerState,
    private val restaurantState: RestaurantState,
    private val server: Server = ServerOf(serverState),
    private val cook: Cook = CookOf(server, kitchenState, restaurantState.cookInterval),
    private val taker: OrderTaker = OrderTakerOf(cook, orderState),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Restaurant {

    override fun start(scope: CoroutineScope) {
        scope.launch(dispatcher) { taker.startWork() }
        scope.launch(dispatcher) { cook.startWork() }
        scope.launch(dispatcher) { server.startWork() }
    }

    override suspend fun makeOrdersEndlessly() {
        withContext(dispatcher) {
            while (isActive) {
                taker.makeOrder()
                delay(restaurantState.customersInterval.value.inWholeMilliseconds)
            }
        }
    }
}
