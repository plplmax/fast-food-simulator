package com.github.plplmax.simulator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.github.plplmax.simulator.kitchen.KitchenState
import com.github.plplmax.simulator.kitchen.KitchenStateOf
import com.github.plplmax.simulator.order.OrderState
import com.github.plplmax.simulator.order.OrderStateOf
import com.github.plplmax.simulator.restaurant.RestaurantOf
import com.github.plplmax.simulator.restaurant.RestaurantState
import com.github.plplmax.simulator.restaurant.RestaurantStateOf
import com.github.plplmax.simulator.server.ServerState
import com.github.plplmax.simulator.server.ServerStateOf
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() = singleWindowApplication(
    state = WindowState(
        width = 1200.dp,
        height = 700.dp,
        position = WindowPosition.Aligned(
            Alignment.Center
        )
    ),
    resizable = false
) {
    MaterialTheme(colors) {
        Box(
            modifier = Modifier.background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val restaurantState = remember { RestaurantStateOf() }
    val orderState = remember(restaurantState.started.value) { OrderStateOf() }
    val kitchenState = remember(restaurantState.started.value) { KitchenStateOf() }
    val serverState = remember(restaurantState.started.value) { ServerStateOf() }
    val restaurant =
        remember(restaurantState.started.value) { RestaurantOf(orderState, kitchenState, serverState, restaurantState) }
    Row {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            OrderArea(state = orderState)
            SettingsArea(state = restaurantState)
        }
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            KitchenArea(state = kitchenState)
            ServerArea(state = serverState)
        }
    }
    LaunchedEffect(restaurantState.started.value) {
        if (restaurantState.started.value) {
            restaurant.start(scope = this@LaunchedEffect)
            restaurant.makeOrdersEndlessly()
        }
    }
}

@Composable
private fun OrderArea(state: OrderState = OrderStateOf()) {
    InformationCard {
        ColumnWithTitle(title = "Order area") {
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
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(contentAlignment = contentAlignment) {
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
private fun KitchenArea(state: KitchenState = KitchenStateOf()) {
    val listState = rememberLazyListState()
    InformationCard(modifier = Modifier.padding(start = 14.dp)) {
        ColumnWithTitle(title = "Kitchen area") {
            Row {
                SubcomposeFlexColumn {
                    InformationCard {
                        CurrentOrderText(state.currentOrderId)
                    }
                    InformationCard {
                        TextCardContainer {
                            Text("Count of waiting orders:")
                            Text("${state.waitingOrders.size}")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxHeight().padding(start = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.waitingOrders.isEmpty()) {
                        InformationCard {
                            TextCardContainer {
                                Text("Waiting orders are empty")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight()
                                .background(color = MaterialTheme.colors.background)
                                .padding(start = 4.dp, top = 4.dp, end = 4.dp),
                            state = listState
                        ) {
                            items(items = state.waitingOrders, key = { order -> order.id() }) { order ->
                                Text(
                                    "#${order.id()}",
                                    modifier = Modifier.background(AppColors.gray16)
                                        .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 14.dp),
                                    color = MaterialTheme.colors.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(listState),
                            modifier = Modifier.background(color = AppColors.gray12)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnWithTitle(
    modifier: Modifier = Modifier,
    title: String = "Column title",
    fontSize: TextUnit = 14.sp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.width(520.dp).heightIn(max = 260.dp).padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, modifier = Modifier.padding(bottom = 14.dp), fontSize = fontSize)
        content()
    }
}

@Preview
@Composable
private fun KitchenAreaPreview() {
    MaterialTheme {
        KitchenArea()
    }
}

@Composable
private fun SettingsArea(state: RestaurantState) {
    InformationCard(modifier = Modifier.padding(top = 14.dp)) {
        ColumnWithTitle(title = "Settings area") {
            var selectedCustomersInterval by remember { mutableStateOf("${state.customersInterval.value.inWholeSeconds}") }
            var selectedCookInterval by remember { mutableStateOf("${state.cookInterval.value.inWholeSeconds}") }
            var customersIntervalError by remember { mutableStateOf(false) }
            var cookIntervalError by remember { mutableStateOf(false) }
            val transform = remember {
                { str: String ->
                    runCatching {
                        val value = str.toUByteOrNull()
                        if (value == null || value == 0U.toUByte()) {
                            error("Interval must be from 1 to 255")
                        }
                        value.toInt().toDuration(DurationUnit.SECONDS)
                    }
                }
            }
            Row(Modifier.padding(top = 14.dp)) {
                SettingTextField(
                    value = selectedCustomersInterval,
                    columnModifier = Modifier.weight(1F),
                    onValueChange = {
                        selectedCustomersInterval = it
                        customersIntervalError = false
                    },
                    transform = transform,
                    onSuccessTransform = { state.customersInterval.value = it },
                    onFailureTransform = { customersIntervalError = true },
                    enabled = !state.started.value,
                    label = { Text("Interval for customers arrival") },
                    isError = customersIntervalError
                )
                SettingTextField(
                    value = selectedCookInterval,
                    columnModifier = Modifier.weight(1F).padding(start = 14.dp),
                    onValueChange = {
                        selectedCookInterval = it
                        cookIntervalError = false
                    },
                    transform = transform,
                    onSuccessTransform = { state.cookInterval.value = it },
                    onFailureTransform = { cookIntervalError = true },
                    enabled = !state.started.value,
                    label = { Text("Interval for cook") },
                    isError = cookIntervalError
                )
            }
            Row {
                Button(
                    onClick = { state.started.value = true },
                    modifier = Modifier.weight(1F),
                    enabled = !customersIntervalError && !cookIntervalError && !state.started.value
                ) {
                    Text("Start")
                }
                Button(
                    onClick = { state.started.value = false },
                    modifier = Modifier.weight(1F).padding(start = 14.dp),
                    enabled = state.started.value
                ) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
private fun <T> SettingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    transform: (String) -> Result<T>,
    onSuccessTransform: (T) -> Unit,
    onFailureTransform: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    columnModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: MutableState<String> = remember { mutableStateOf("Error") },
    singleLine: Boolean = true
) {
    Column(columnModifier) {
        TextField(value, onValueChange = { str ->
            onValueChange(str)
            val result = transform(str)
            result.onSuccess(onSuccessTransform)
            result.onFailure {
                errorMessage.value = it.message!!
                onFailureTransform()
            }
        }, modifier, enabled, label = label, trailingIcon = {
            if (isError) Icon(Icons.Filled.Warning, contentDescription = "Error", tint = MaterialTheme.colors.error)
        }, isError = isError, singleLine = singleLine)
        Text(
            text = if (isError) errorMessage.value else "In seconds",
            color = if (isError) MaterialTheme.colors.error else Color.Gray,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun ServerArea(state: ServerState) {
    InformationCard(modifier = Modifier.padding(start = 14.dp, top = 14.dp)) {
        ColumnWithTitle(title = "Server area") {
            SubcomposeFlexColumn {
                InformationCard {
                    CurrentOrderText(state.currentOrderId)
                }
                InformationCard {
                    TextCardContainer {
                        Text("Number of waiting customers:")
                        Text("${state.customersSize}", modifier = Modifier.padding(start = 24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentOrderText(currentOrderId: UInt) {
    val text = if (currentOrderId == 0U) "N/A" else currentOrderId.toString()
    TextCardContainer {
        Text("Number of current order: ")
        Text(text, modifier = Modifier.padding(start = 24.dp))
    }
}
