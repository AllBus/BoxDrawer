package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.template.ONE_NAME
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateItemOne
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateItemOneBox(
    form: TemplateItemOne,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val namesPrefix = "$prefix.$ONE_NAME"

    val checkState = remember {
        val s =
            templateGenerator.get(namesPrefix).firstOrNull()?.toDoubleOrNull()
        mutableStateOf<Boolean>((s ?: 0.0) > 0.0)
    }

    Row {
        Column(Modifier.weight(1f)) {
            TemplateItemBox(
                item = form.data,
                block = block,
                prefix = prefix,
                templateGenerator = templateGenerator,
                isEdit = false,
            )
        }

        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            RunCheckBox(
                checked = checkState.value,
                title = form.title,
                onCheckedChange = { c ->
                    checkState.value = c
                    templateGenerator.put(
                        namesPrefix,
                        if (c) "1" else "-1"
                    )
                },
            )
        }

    }


}