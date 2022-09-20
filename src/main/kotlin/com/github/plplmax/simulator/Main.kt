package com.github.plplmax.simulator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.github.plplmax.simulator.kitchen.CookOf
import com.github.plplmax.simulator.order.OrderTakerOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun main() = singleWindowApplication {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray, shape = MaterialTheme.shapes.large) {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val taker = remember { OrderTakerOf(CookOf()) }
    OrderArea(taker.waitingCustomers(), taker.currentOrderId())
    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) { taker.startWork() }
        while (isActive) {
            taker.makeOrder()
            delay(2000)
        }
    }
}

@Composable
private fun OrderArea(waitingCustomers: Int = 0, currentOrderId: Int = 0) {
    Row {
        OrderText("Wait to place orders: $waitingCustomers")
        CurrentOrderText(if (currentOrderId == 0) "empty" else currentOrderId.toString())
    }
}

@Preview
@Composable
private fun OrderAreaPreview() {
    MaterialTheme {
        OrderArea()
    }
}

@Composable
private fun OrderText(text: String) {
    Surface(border = BorderStroke(4.dp, Color.Black)) {
        Text(text, modifier = Modifier.padding(24.dp))
    }
}

@Composable
private fun CurrentOrderText(text: String): Unit = OrderText("Number of current order: $text")
