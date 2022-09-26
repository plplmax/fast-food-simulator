package com.github.plplmax.simulator.order

import com.github.plplmax.simulator.customer.Customer
import com.github.plplmax.simulator.customer.CustomerOf
import com.github.plplmax.simulator.kitchen.Cook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class OrderTakerOf(
    private val cook: Cook,
    private val state: OrderState,
    customers: Collection<Customer> = emptyList()
) : OrderTaker {
    private val customers: ConcurrentLinkedQueue<Customer> = ConcurrentLinkedQueue(customers)
    private val lastId: AtomicInteger = AtomicInteger(0)

    override fun makeOrder() {
        customers.add(CustomerOf())
        ++state.customersSize
    }

    override suspend fun startWork(): Unit = withContext(Dispatchers.Default) {
        while (isActive) {
            val customer = customers.peek()
            if (customer == null) {
                yield()
            } else {
                val order = OrderOf(lastId.incrementAndGet().toUInt())
                state.currentOrderId = order.id()
                delay(5000)
                cook.makeOrder(order)
                customers.poll()
                --state.customersSize
            }
        }
    }
}
