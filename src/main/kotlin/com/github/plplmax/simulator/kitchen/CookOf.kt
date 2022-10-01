package com.github.plplmax.simulator.kitchen

import androidx.compose.runtime.State
import com.github.plplmax.simulator.order.Order
import com.github.plplmax.simulator.server.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Duration

class CookOf(
    private val server: Server,
    private val state: KitchenState,
    private val interval: State<Duration>
) : Cook {
    override suspend fun startWork(): Unit = withContext(Dispatchers.Default) {
        while (isActive) {
            val processingOrder = state.waitingOrders.removeFirstOrNull()
            if (processingOrder == null) {
                delay(50)
            } else {
                state.currentOrderId = processingOrder.id()
                delay(interval.value.inWholeMilliseconds)
                server.completeOrder(processingOrder)
            }
        }
    }

    override fun makeOrder(order: Order) {
        state.waitingOrders.add(order)
        server.afterOrderMaked()
    }
}
