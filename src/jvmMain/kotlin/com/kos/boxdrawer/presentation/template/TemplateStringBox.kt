package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.kos.boxdrawe.widget.InputText
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateStringBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val text = remember("$prefix") {
        mutableStateOf(
            TextFieldValue(
                templateGenerator.get(prefix).firstOrNull() ?: (block?.innerLine.orEmpty())
            )
        )
    }
    InputText(
        value = text.value,
        onValueChange = {
            text.value = it
            templateGenerator.put(
                prefix,
                it.text
            )
        },
        label = form.title,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        enabled = true,
    )
}

@Composable
fun TemplateFigureBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val text = remember("$prefix") {
        mutableStateOf(
            TextFieldValue(
                templateGenerator.get(prefix).firstOrNull() ?: (block?.innerLine.orEmpty())
            )
        )
    }
    InputText(
        value = text.value,
        onValueChange = {
            text.value = it
            templateGenerator.put(
                prefix,
                "[${it.text}]"
            )
        },
        label = form.title,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        enabled = true,
    )
}