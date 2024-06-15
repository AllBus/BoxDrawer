package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateStringBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val text = remember("$prefix") {
        mutableStateOf(
            templateGenerator.get(prefix).firstOrNull() ?: (block?.innerLine.orEmpty())
        )
    }
    OutlinedTextField(
        value = text.value.toString(),
        onValueChange = {
            text.value = it
            templateGenerator.put(
                prefix,
                it
            )
        },
        label = { Text(form.title) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        enabled = true,
    )
}