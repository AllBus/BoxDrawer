package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.LabelLight
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateTripleBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val input1 = remember("$prefix.1") {
        NumericTextFieldState(
            value = templateGenerator.get(prefix).getOrNull(0)?.toDoubleOrNull() ?: block?.doubleValue(
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
            value = templateGenerator.get(prefix).getOrNull(1)?.toDoubleOrNull() ?: block?.doubleValue(
                2,
                0.0
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
    val input3 = remember("$prefix.3") {
        NumericTextFieldState(
            value = templateGenerator.get(prefix).getOrNull(2)?.toDoubleOrNull() ?: block?.doubleValue(
                3,
                0.0
            ) ?: 0.0,
            minValue = -1000000.0,
        ) { v ->
            templateGenerator.put(
                prefix,
                3,
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
        NumericUpDown("", "", input3, modifier = Modifier.weight(1f))
    }
}