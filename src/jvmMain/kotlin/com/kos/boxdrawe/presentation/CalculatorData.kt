package com.kos.boxdrawe.presentation

import androidx.compose.runtime.Stable
import com.kos.ariphmetica.Calculator
import com.kos.ariphmetica.math.algorithms.OutExpression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map

@Stable
class CalculatorData {
    init {
        Calculator.init()
    }

    private val calcFlow = MutableStateFlow("")
    val result = calcFlow.debounce(200).map {
        try {
            Calculator.parse(it)
        } catch (e: Exception) {
            com.kos.ariphmetica.math.`Operator$`.`MODULE$`.C0()
        }
    }.map {
        try {
            Calculator.fullCalc(Calculator.diff(it))
        } catch (e: Exception) {
            com.kos.ariphmetica.math.`Operator$`.`MODULE$`.C0()
        }
    }.map { OutExpression.apply(it) as String }

    fun calculate(text: String) {
        calcFlow.value = text
    }

    val text: String get() = calcFlow.value
}