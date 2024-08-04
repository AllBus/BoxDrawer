package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jsevy.jdxf.DXFColor
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateColorBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val input =
        remember(prefix) {
            NumericTextFieldState(
                value = templateGenerator.get(prefix).firstOrNull()?.toDoubleOrNull()
                    ?: block?.doubleValue(
                        1,
                        0.0
                    ) ?: 0.0,
                digits = 0,
                maxValue = 255.0,
                minValue = 0.0,
            ) { v ->
                templateGenerator.put(
                    prefix,
                    v.toString()
                )
            }
        }
    val hoverColor = remember { mutableIntStateOf(0) }
    Column {
        Row() {
            Label(
                form.title,
                singleLine = true,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            NumericUpDownLine("", "", input, modifier = Modifier.weight(1f))
            Column(modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                Box(
                    modifier = Modifier.size(15.dp)
                        .background(color = Color(DXFColor.getRgbColor(input.decimal.toInt())))
                )
                Box(
                    modifier = Modifier.size(15.dp)
                        .background(color = Color(DXFColor.getRgbColor(hoverColor.value)))
                )
            }
        }
    }
    ColorBox(Modifier, onClick = { color ->
        input.update(color.toString())
    },
        onHover = { color ->
            hoverColor.value = color
        },
        onExit = {
            hoverColor.value = input.decimal.toInt()
        }
    )
}