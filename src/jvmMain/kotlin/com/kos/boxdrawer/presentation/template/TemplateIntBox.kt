package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateIntBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
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
                    v.toInt().toString()
                )
            }
        }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDownLine("", "", input, modifier = Modifier.weight(1f))
    }
}

