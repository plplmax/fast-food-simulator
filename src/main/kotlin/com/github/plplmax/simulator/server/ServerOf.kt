package com.github.plplmax.simulator.server

import com.github.plplmax.simulator.order.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class ServerOf(
    private val state: ServerState,
    orders: Collection<Order> = emptyList(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Server {
    private val orders: Queue<Order> = ConcurrentLinkedQueue(orders)

    override suspend fun startWork(): Unit = withContext(dispatcher) {
        while (isActive) {
            val order = orders.poll()
            if (order != null) {
                state.currentOrderId = order.id()
                delay(1000)
                --state.customersSize
                state.currentOrderId = 0U
            } else {
                yield()
            }
        }
    }

    override fun completeOrder(order: Order) {
        orders.add(order)
    }

    override fun afterOrderMaked() {
        ++state.customersSize
    }
}
