package com.github.plplmax.simulator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.github.plplmax.simulator.kitchen.KitchenState
import com.github.plplmax.simulator.kitchen.KitchenStateOf
import com.github.plplmax.simulator.order.OrderState
import com.github.plplmax.simulator.order.OrderStateOf
import com.github.plplmax.simulator.restaurant.RestaurantOf

fun main() = singleWindowApplication {
    MaterialTheme(colors) {
        Box(
            modifier = Modifier.background(MaterialTheme.colors.surface)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val orderState = remember { OrderStateOf() }
    val kitchenState = remember { KitchenStateOf() }
    val restaurant = remember { RestaurantOf(orderState, kitchenState) }
    Column(modifier = Modifier.heightIn(max = 260.dp)) {
        Row {
            OrderArea(state = orderState)
        }
    }
    LaunchedEffect(Unit) {
        restaurant.start(scope = this@LaunchedEffect)
        restaurant.makeOrdersEndlessly()
    }
}

@Composable
private fun OrderArea(state: OrderState = OrderStateOf()) {
    SubcomposeFlexColumn {
        InformationCard {
            TextCardContainer {
                Text("Wait to place orders: ")
                Text("${state.customersSize}")
            }
        }
        InformationCard {
            CurrentOrderText(state.currentOrderId)
        }
    }
}

@Composable
private fun SubcomposeFlexColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    SubcomposeLayout(modifier) { constraints ->
        var slotId = 0
        val placeables = subcompose(slotId++, content).map { measurable ->
            measurable.measure(constraints)
        }
        val maxSize = placeables.fold(initial = IntSize.Zero) { max, placeable ->
            IntSize(width = maxOf(max.width, placeable.width), height = max.height + placeable.height)
        }
        val trmdPlaceables = trimmedPlaceables(maxSize, placeables, constraints, slotId, content)
        layout(width = maxSize.width, height = constraints.maxHeight) {
            var yPos = 0
            trmdPlaceables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPos)
                yPos += placeable.height
            }
        }
    }
}

private fun SubcomposeMeasureScope.trimmedPlaceables(
    maxSize: IntSize,
    placeables: List<Placeable>,
    constraints: Constraints,
    slotId: Int,
    content: @Composable () -> Unit
): Sequence<Placeable> {
    var currentHeight = maxSize.height
    val trimmedPlaceables = placeables.dropLastWhile {
        if (currentHeight > constraints.maxHeight) {
            currentHeight -= it.height
            true
        } else {
            false
        }
    }
    if (trimmedPlaceables.isEmpty()) return emptySequence()
    val height = constraints.maxHeight / trimmedPlaceables.size
    return subcompose(slotId, content).asSequence().map { measurable ->
        measurable.measure(
            Constraints(
                minWidth = maxSize.width,
                maxWidth = constraints.maxWidth,
                minHeight = height,
                maxHeight = height
            )
        )
    }.take(trimmedPlaceables.size)
}

@Composable
private fun InformationCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        Card(modifier = modifier.widthIn(min = minWidth), elevation = 4.dp) {
            content()
        }
    }
}

@Composable
private fun TextCardContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(modifier = modifier.padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        content()
    }
}

@Preview
@Composable
private fun OrderAreaPreview() {
    MaterialTheme(colors) {
        OrderArea()
    }
}

@Composable
private fun CurrentOrderText(currentOrderId: Int) {
    val text = if (currentOrderId == 0) "N/A" else currentOrderId.toString()
    TextCardContainer {
        Text("Number of current order: ")
        Text(text, modifier = Modifier.padding(start = 14.dp))
    }
}
