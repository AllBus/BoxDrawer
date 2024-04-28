package com.kos.boxdrawer.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateItemInt
import turtoise.TurtoiseParserStackItem

@Composable
fun TemplateIntBox(
    form: TemplateItemInt,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input =
        remember(prefix) {
            NumericTextFieldState(
                templateGenerator.get(prefix).firstOrNull()?.toDoubleOrNull() ?: block?.doubleValue(
                    1,
                    0.0
                ) ?: 0.0, 0
            ) { v ->
                templateGenerator.put(
                    prefix,
                    v.toString()
                )
            }
        }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input, modifier = Modifier.weight(1f))
    }
}