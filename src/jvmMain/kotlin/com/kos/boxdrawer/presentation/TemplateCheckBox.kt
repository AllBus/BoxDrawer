package com.kos.boxdrawer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateItemCheck
import turtoise.TurtoiseParserStackItem

@Composable
fun TemplateCheckBox(
    form: TemplateItemCheck,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val checkState = remember("$prefix") {
        mutableStateOf(
            (templateGenerator.get(prefix).firstOrNull() ?: block?.get(0)) == "true"
        )
    }
    RunCheckBox(
        checked = checkState.value,
        title = form.title,
        onCheckedChange = { c ->
            checkState.value = c
            templateGenerator.put(
                prefix,
                c.toString()
            )
        },
    )
}