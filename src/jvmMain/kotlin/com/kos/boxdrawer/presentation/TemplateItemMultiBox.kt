package com.kos.boxdrawer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateItemMulti
import turtoise.TurtoiseParserStackItem

@Composable
fun TemplateItemMultiBox(
    form: TemplateItemMulti,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val namesPrefix = "$prefix._multi_names_"

    val x = remember {
        val s =
            templateGenerator.get(namesPrefix).mapNotNull { it.toIntOrNull() }
                .takeIf { it.isNotEmpty() } ?: listOf(1)
        mutableStateOf(s)
    }

    Column(
        modifier = Modifier
            .border(1.dp, ThemeColors.templateFormBorder)
            .background(ThemeColors.tabBackground)
            .padding(2.dp)
    ) {

        Row() {
            Text(form.title, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(4.dp))
            Text(form.argumentName, color = ThemeColors.templateArgumentColor)
            val icon = Icons.Rounded.Add
            ImageButton(icon, Modifier.wrapContentSize()) {
                val xv = x.value
                x.value += if (xv.isEmpty()) 1 else (xv.max() + 1)
                templateGenerator.putList(namesPrefix, x.value.map { it.toString() }.toList())
            }
        }



        x.value.forEach() { v ->
            val itemPrefix = "$prefix.$v"
            Row {
                Box(Modifier.weight(1f)) {
                    TemplateItemBox(
                        item = form.data,
                        block = block,
                        prefix = itemPrefix,
                        templateGenerator = templateGenerator,
                        isEdit = false,
                    )
                }

                val iconDelete = Icons.Rounded.Delete
                ImageButton(
                    iconDelete,
                    Modifier.wrapContentSize().align(Alignment.CenterVertically)
                ) {
                    x.value = x.value.filter { it != v }
                    templateGenerator.removeItem(itemPrefix)
                    templateGenerator.putList(namesPrefix, x.value.map { it.toString() }.toList())
                }
            }
        }


    }
}