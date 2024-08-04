package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.SemanticsProperties.EditableText
import androidx.compose.ui.semantics.editableText
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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
                        .background(color = Color(com.jsevy.jdxf.DXFColor.getRgbColor(input.decimal.toInt())))
                )
                Box(
                    modifier = Modifier.size(15.dp)
                        .background(color = Color(com.jsevy.jdxf.DXFColor.getRgbColor(hoverColor.value)))
                )
            }
        }
    }
    ColorBox(Modifier, onClick =  { color ->
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ColorBox(modifier: Modifier = Modifier, onClick: (Int) -> Unit, onHover: (Int) -> Unit, onExit: () -> Unit) {
    for (i in 0 until 26) {
        Row(
            modifier.height(8.dp).onPointerEvent(PointerEventType.Exit){
                onExit()
            }
        ) {
            for (j in 0 until 10) {
                val colorId =  i * 10 + j
                val color = com.jsevy.jdxf.DXFColor.getRgbColor(colorId)
                Box(
                    modifier.weight(0.125f, true).fillMaxHeight().background(color = Color(color)).onClick { onClick(colorId) }.onPointerEvent(
                        PointerEventType.Enter
                    ){
                        onHover(colorId)
                    }
                )
            }
        }
    }
}