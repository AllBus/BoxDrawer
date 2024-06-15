package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.CircleBox
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateAngleBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val input =
        remember("$prefix") {
            NumericTextFieldState(
                value = templateGenerator.get(prefix).firstOrNull()?.toDoubleOrNull() ?: block?.doubleValue(
                    1,
                    0.0
                ) ?: 0.0,
                minValue = -1000000.0,
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
        CircleBox(
            modifier = Modifier.weight(0.5f).height(32.dp)
                .background(ThemeColors.inputBackgroundState(true))
        ) { current, change, start ->
            input.update(input.decimal + change)
        }
    }
}