package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.AxisBox
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.LabelLight
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2

@Composable
fun TemplateSizeBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener,
) {
    val input1 = remember("$prefix.1") {
        NumericTextFieldState(
            value = templateGenerator.get(prefix).getOrNull(0)?.toDoubleOrNull()
                ?: block?.doubleValue(
                    1,
                    0.0
                ) ?: 0.0,
            minValue = -1000000.0,
        ) { v ->
            templateGenerator.put(
                prefix,
                1,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input2 = remember("$prefix.2") {
        NumericTextFieldState(
            value = templateGenerator.get(prefix).getOrNull(1)?.toDoubleOrNull()
                ?: block?.doubleValue(
                    2,
                    0.0,
                ) ?: 0.0,
            minValue = -1000000.0,
        ) { v ->
            templateGenerator.put(
                prefix,
                2,
                form.argumentCount,
                v.toString()
            )
        }
    }
    Row() {
        LabelLight(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2, modifier = Modifier.weight(1f))
        AxisBox(  modifier = Modifier.size(20.dp, ThemeColors.NumericFieldHeight).padding(1.dp)
            .background(ThemeColors.inputBackgroundState(true)),
            { Vec2(input1.decimal, input2.decimal) }) { current, change, start ->
            if (true) {
                input1.update(current.x)
                input2.update(current.y)
            }
        }
    }
}