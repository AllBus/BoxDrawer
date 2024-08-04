package com.kos.boxdrawer.presentation.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateCheckBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val checkState = remember("$prefix") {

        val p = templateGenerator.get(prefix).firstOrNull()?.toDoubleOrNull()
        val b = block?.get(0)?.toDoubleOrNull()
        mutableStateOf<Boolean>(
            (p?: b?: 1.0) >0.0
        )
    }
    RunCheckBox(
        checked = checkState.value,
        title = form.title,
        onCheckedChange = { c ->
            checkState.value = c
            templateGenerator.put(
                prefix,
                if (c) "1" else "-1"
            )
        },
    )
}