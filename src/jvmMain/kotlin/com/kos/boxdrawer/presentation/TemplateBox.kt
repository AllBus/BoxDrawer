package com.kos.boxdrawer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawer.template.TemplateCreator.dropSkobki
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemCheck
import com.kos.boxdrawer.template.TemplateItemInt
import com.kos.boxdrawer.template.TemplateItemLabel
import com.kos.boxdrawer.template.TemplateItemNumeric
import com.kos.boxdrawer.template.TemplateItemRect
import com.kos.boxdrawer.template.TemplateItemSize
import com.kos.boxdrawer.template.TemplateItemString
import com.kos.boxdrawer.template.TemplateItemTriple
import turtoise.TurtoiseParserStackItem

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


    if (!form.isEmpty()) {
        Box(modifier = modifier) {
            val prefix =
                if (form.argumentName.isNotEmpty()) "." + form.argumentName else ""
            TemplateFormBox(
                form = form,
                prefix = prefix,
                block = block,
                templateGenerator = templateGenerator,
            )
        }
    }
}

@Composable
fun TemplateFormBox(
    modifier: Modifier = Modifier,
    form: TemplateForm,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener,
) {

    Column(
        modifier = modifier
            .border(1.dp, ThemeColors.templateFormBorder)
            .background(ThemeColors.tabBackground)
            .padding(2.dp)
    ) {
        Row() {
            Text(form.title)
            Spacer(modifier = Modifier.width(4.dp))
            Text(form.argumentName, color = ThemeColors.templateArgumentColor)
        }
        form.list.forEach {
            TemplateItemBox(
                item = it,
                block = block,
                prefix = prefix,
                templateGenerator = templateGenerator,
            )
        }
    }
}

@Composable
fun TemplateItemBox(
    item: TemplateItem,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener,
) {
    val newPrefix = prefix + "." + item.argumentName
    val inner = block?.getInnerAtName(item.argumentName)
    when (item) {
        is TemplateForm -> TemplateFormBox(
            form = item,
            block = inner,
            prefix = newPrefix,
            templateGenerator = templateGenerator,
        )

        is TemplateItemNumeric -> TemplateNumericBox(
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

        is TemplateItemLabel -> TemplateLabelBox(form = item)
    }
}

@Composable
fun TemplateTripleBox(
    form: TemplateItemTriple,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input1 = remember("$prefix.1") {
        NumericTextFieldState(block?.doubleValue(1, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                1,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input2 = remember("$prefix.2") {
        NumericTextFieldState(block?.doubleValue(2, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                2,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input3 = remember("$prefix.3") {
        NumericTextFieldState(block?.doubleValue(3, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                3,
                form.argumentCount,
                v.toString()
            )
        }
    }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input3, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateRectBox(
    form: TemplateItemRect,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input1 = remember("$prefix.1") {
        NumericTextFieldState(block?.doubleValue(1, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                1,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input2 = remember("$prefix.2") {
        NumericTextFieldState(block?.doubleValue(2, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                2,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input3 = remember("$prefix.3") {
        NumericTextFieldState(block?.doubleValue(3, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                3,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input4 = remember("$prefix.4") {
        NumericTextFieldState(block?.doubleValue(4, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                4,
                form.argumentCount,
                v.toString()
            )
        }
    }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input3, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input4, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateSizeBox(
    form: TemplateItemSize,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input1 = remember("$prefix.1") {
        NumericTextFieldState(block?.doubleValue(1, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                1,
                form.argumentCount,
                v.toString()
            )
        }
    }
    val input2 = remember("$prefix.2") {
        NumericTextFieldState(block?.doubleValue(2, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                2,
                form.argumentCount,
                v.toString()
            )
        }
    }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateNumericBox(
    form: TemplateItemNumeric,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input =
        remember("$prefix") { NumericTextFieldState(block?.doubleValue(1, 0.0)?:0.0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                v.toString())
        } }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateIntBox(
    form: TemplateItemInt,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val input =
        remember("$prefix") { NumericTextFieldState(block?.doubleValue(1, 0.0)?:0.0, 0) { v ->
            templateGenerator.templateGenerator(
                prefix,
                v.toString()
            )
        } }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateCheckBox(
    form: TemplateItemCheck,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val checkState = remember("$prefix") { mutableStateOf(block?.line == "true") }
    RunCheckBox(
        checked = checkState.value,
        title = form.title,
        onCheckedChange = { c ->
            checkState.value = c
            templateGenerator.templateGenerator(
                prefix,
                c.toString()
            )
        },
    )
}

@Composable
fun TemplateLabelBox(form: TemplateItemLabel) {
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}


@Composable
fun TemplateStringBox(
    form: TemplateItemString,
    block: TurtoiseParserStackItem?,
    prefix: String,
    templateGenerator: TemplateGeneratorListener
) {
    val text = remember("$prefix") { mutableStateOf(block?.line.orEmpty().dropSkobki()) }
    OutlinedTextField(
        value = text.value.toString(),
        onValueChange = {
            text.value = it
            templateGenerator.templateGenerator(
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