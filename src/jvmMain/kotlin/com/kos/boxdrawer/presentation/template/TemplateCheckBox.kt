package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kos.boxdrawe.widget.CheckboxK
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItem
import turtoise.parser.TortoiseParserStackItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TemplateCheckBox(
    form: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorSimpleListener
) {
    val checkState = remember(prefix) {
        val p = templateGenerator.get(prefix).firstOrNull()?.toDoubleOrNull()
        val b = block?.get(0)?.toDoubleOrNull()
        mutableStateOf<Boolean>(
            (p?: b?: 1.0) >0.0
        )
    }

    val onChecked =  remember{
        { c : Boolean ->
            checkState.value = c
            templateGenerator.put(
                prefix,
                if (c) "1" else "-1"
            )
        }
    }
    Row(){
        Text(
            text = form.title,
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f).onClick(
                onClick = { onChecked(!checkState.value) }
            ),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onPrimary,
            softWrap = false,
        )
        CheckboxK(
            checked = checkState.value,
            onCheckedChange = onChecked,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}