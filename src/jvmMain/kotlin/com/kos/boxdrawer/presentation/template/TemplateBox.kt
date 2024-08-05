package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemAngle
import com.kos.boxdrawer.template.TemplateItemCheck
import com.kos.boxdrawer.template.TemplateItemColor
import com.kos.boxdrawer.template.TemplateItemFigure
import com.kos.boxdrawer.template.TemplateItemInt
import com.kos.boxdrawer.template.TemplateItemLabel
import com.kos.boxdrawer.template.TemplateItemMulti
import com.kos.boxdrawer.template.TemplateItemNumeric
import com.kos.boxdrawer.template.TemplateItemOne
import com.kos.boxdrawer.template.TemplateItemRect
import com.kos.boxdrawer.template.TemplateItemSelector
import com.kos.boxdrawer.template.TemplateItemSize
import com.kos.boxdrawer.template.TemplateItemString
import com.kos.boxdrawer.template.TemplateItemTriple
import turtoise.parser.TortoiseParserStackItem

@Composable
fun TemplateBox(
    modifier: Modifier,
    menu: State<TemplateInfo>,
    templateGenerator: TemplateGeneratorListener,
) {

    val form = menu.value.form
    val block = menu.value.values
    if (form.argumentName.isNotEmpty()) {
        menu.value.values.getInnerAtName(form.argumentName)
    } else
        menu.value.values

    Column(modifier = modifier) {
        if (!form.isEmpty()) {
            val prefix =
                if (form.argumentName.isNotEmpty()) "." + form.argumentName else ""
            TemplateFormBox(
                form = form,
                prefix = prefix,
                block = block,
                templateGenerator = templateGenerator,
                isEdit = menu.value.edit,
            )
        }
    }
}

@Composable
fun TemplateFormBox(
    modifier: Modifier = Modifier,
    form: TemplateForm,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener,
    isEdit: Boolean,
) {

    Column(
        modifier = modifier
            .border(1.dp, ThemeColors.templateFormBorder)
         //   .background(ThemeColors.tabBackground)
            .padding(2.dp)
    ) {
        if (form.title.isNotEmpty()) {
            Row() {
                Text(form.title, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
              //  Text(form.argumentName, color = ThemeColors.templateArgumentColor)
            }
        }
        form.list.forEach {
            TemplateItemBox(
                item = it,
                block = block,
                prefix = prefix,
                templateGenerator = templateGenerator,
                isEdit = isEdit,
            )
        }
    }
}

@Composable
fun TemplateSimpleItemBox(
    item: TemplateItem,
    inner: TortoiseParserStackItem?,
    newPrefix: String,
    templateGenerator: TemplateGeneratorSimpleListener,
) {
    when (item) {
        is TemplateItemNumeric -> TemplateNumericBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemAngle -> TemplateAngleBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemSize -> TemplateSizeBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemRect -> TemplateRectBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemTriple -> TemplateTripleBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemInt -> TemplateIntBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemColor -> TemplateColorBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )
        is TemplateItemFigure -> TemplateFigureBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemString -> TemplateStringBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )

        is TemplateItemCheck -> TemplateCheckBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )
        is TemplateItemSelector -> TemplateSelectorBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator
        )
    }
}

@Composable
fun TemplateItemBox(
    item: TemplateItem,
    block: TortoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener,
    isEdit: Boolean,
) {
    val newPrefix = prefix + "." + item.argumentName
    val inner = block?.getInnerAtName(item.argumentName)
    Row {
        Box(Modifier.weight(1f)) {
            when (item) {
                is TemplateForm -> TemplateFormBox(
                    form = item,
                    block = inner,
                    prefix = newPrefix,
                    templateGenerator = templateGenerator,
                    isEdit = isEdit,
                )

                is TemplateItemLabel -> TemplateLabelBox(form = item)
                is TemplateItemMulti -> TemplateItemMultiBox(
                    form = item,
                    block = inner,
                    prefix = newPrefix,
                    templateGenerator = templateGenerator,
                )
                is TemplateItemOne -> TemplateItemOneBox(
                    form = item,
                    block = inner,
                    prefix = newPrefix,
                    templateGenerator = templateGenerator,
                )

                else -> {
                    TemplateSimpleItemBox(
                        item,
                        inner,
                        newPrefix,
                        templateGenerator
                    )
                }
            }


        }
        if (isEdit) {
            TemplateFormEditRemove(
                newPrefix,
                templateGenerator = templateGenerator,
            )
        }
    }
}

@Composable
fun RowScope.TemplateFormEditRemove(
    itemPrefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val iconDelete = Icons.Rounded.Delete
    Spacer(modifier = Modifier.width(4.dp))
    Text(itemPrefix, color = ThemeColors.templateArgumentColor)
    ImageButton(
        iconDelete,
        Modifier.wrapContentSize().align(Alignment.CenterVertically)
    ) {
        templateGenerator.editorRemoveItem(itemPrefix)
    }
}

@Composable
fun TemplateFormEditAdd(
    templateGenerator: TemplateGeneratorListener
) {

}

