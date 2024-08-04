package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItemSelector
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateSelectorBox(
    form: TemplateItemSelector,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val text = remember("$prefix") {
        val v = templateGenerator.get(prefix).firstOrNull() ?: (block?.innerLine.orEmpty())
        mutableStateOf(v)
    }

    Row {
        form.variants.forEach { variant ->
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    text.value = variant
                    templateGenerator.put(
                        prefix,
                        variant
                    )
                },
                shape = RectangleShape,
                border = if (variant == text.value) {
                    BorderStroke(2.dp, MaterialTheme.colors.onPrimary)
                } else null,
                contentPadding = PaddingValues(1.dp, 2.dp)
            ) {
                Text(
                    variant,
                    maxLines = 1,
                )
            }
        }
    }
}