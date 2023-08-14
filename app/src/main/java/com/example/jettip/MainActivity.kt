@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                //TopHeader(134.0)
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface() {
            content()
        }
    }

}


@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                "Total Per Person",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.background
            )

            Text(
                "$ $total", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}


@Composable
fun MainContent() {
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val range = IntRange(1, 30)

    val splitByState = remember {
        mutableStateOf(1)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        BillForm(
            range = range,
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        )
    }


}

@Preview(showBackground = true)
@Composable
fun MyPreview() {
    MyApp {
        Text(text = "Hello Text")
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = remember {
        mutableStateOf(0)
    }



    TopHeader(totalPerPersonState.value)
    Surface(
        modifier = modifier
            .padding(start = 10.dp, top = 1.dp, end = 10.dp, bottom = 1.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column() {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    keyboardController?.hide()
                })
            if (validState) {
                Row(
                    modifier = modifier.padding(10.dp, 5.dp, 10.dp, 5.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(id = R.string.Split),
                        modifier = modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = modifier.width(120.dp))
                    Row(
                        modifier = modifier
                            .padding(horizontal = 3.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            if (splitByState.value > 1) splitByState.value--

                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                tipPercentage.value
                            )
                        })
                        Text(
                            text = splitByState.value.toString(),
                            modifier = Modifier.align(Alignment.CenterVertically),
                            style = TextStyle(
                                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            )
                        )
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {

                            if (splitByState.value < range.last) {
                                splitByState.value++
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    splitByState.value,
                                    tipPercentage.value
                                )
                            }
                        })
                    }
                }

                Row(
                    modifier = modifier
                        .padding(10.dp, 5.dp, 10.dp, 5.dp)
                        .fillMaxWidth(),

                    ) {
                    Text(
                        text = "Tip",
                        modifier = modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth(0.5f)
                    )

                    Box(
                        modifier = modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "$ ${tipAmountState.value}",
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${tipPercentage.value} %")
                    Spacer(modifier = modifier.height(15.dp))
                    Slider(
                        modifier = modifier.padding(start = 16.dp, end = 16.dp),
                        value = sliderPositionState.value,
                        steps = 5,
                        onValueChange = {
                            sliderPositionState.value = it
                            tipPercentage.value = (sliderPositionState.value * 100).toInt()
                            tipAmountState.value =
                                calculateTip(
                                    totalBillState.value.toDouble(),
                                    tipPercentage.value
                                )
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                tipPercentage.value
                            )
                        })
                }
            } else {
                Box() {}
            }
        }
    }

}

fun calculateTip(totalBill: Double, tipPercentage: Int): Double {

    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        (totalBill * tipPercentage) / 100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercentage: Int): Double =
    (calculateTip(totalBill, tipPercentage) + totalBill) / splitBy




