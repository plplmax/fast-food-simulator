package com.github.plplmax.simulator.order

class OrderOf(private val id: UInt) : Order {
    override fun id(): UInt = this.id
}
