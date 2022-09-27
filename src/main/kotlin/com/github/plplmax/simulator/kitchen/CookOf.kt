package com.github.plplmax.simulator.kitchen

import com.github.plplmax.simulator.order.Order
import com.github.plplmax.simulator.server.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class CookOf(
    private val server: Server,
    private val state: KitchenState,
) : Cook {
    override suspend fun startWork(): Unit = withContext(Dispatchers.Default) {
        while (isActive) {
            val processingOrder = state.waitingOrders.removeFirstOrNull()
            if (processingOrder == null) {
                yield()
            } else {
                state.currentOrderId = processingOrder.id()
                delay(10000)
                server.completeOrder(processingOrder)
            }
        }
    }

    override fun makeOrder(order: Order) {
        state.waitingOrders.add(order)
        server.afterOrderMaked()
    }
}
