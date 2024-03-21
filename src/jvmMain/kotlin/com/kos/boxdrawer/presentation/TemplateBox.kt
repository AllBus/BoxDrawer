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
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemCheck
import com.kos.boxdrawer.template.TemplateItemInt
import com.kos.boxdrawer.template.TemplateItemLabel
import com.kos.boxdrawer.template.TemplateItemNumeric
import com.kos.boxdrawer.template.TemplateItemRect
import com.kos.boxdrawer.template.TemplateItemSize
import com.kos.boxdrawer.template.TemplateItemString
import com.kos.boxdrawer.template.TemplateItemTriple

@Composable
fun TemplateBox(
    modifier: Modifier,
    menu: State<TemplateForm>,
    templateGenerator: (String, String)-> Unit
) {

    Box(modifier = modifier) {
        TemplateFormBox(form = menu.value, prefix = "."+menu.value.argumentName, templateGenerator = templateGenerator)
    }
}

@Composable
fun TemplateFormBox(
    modifier: Modifier = Modifier,
    form: TemplateForm,
    prefix:String,
    templateGenerator: (String, String)-> Unit
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
            TemplateItemBox(item = it, prefix = prefix, templateGenerator = templateGenerator)
        }
    }
}

@Composable
fun TemplateItemBox(item: TemplateItem,
                    prefix:String,
                    templateGenerator: (String, String)-> Unit) {
    val newPrefix = prefix+"."+item.argumentName
    when (item) {
        is TemplateForm -> TemplateFormBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemNumeric -> TemplateNumericBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemSize -> TemplateSizeBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemRect -> TemplateRectBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemTriple -> TemplateTripleBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemInt -> TemplateIntBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemString -> TemplateStringBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemCheck -> TemplateCheckBox(form = item, prefix = newPrefix, templateGenerator = templateGenerator)
        is TemplateItemLabel -> TemplateLabelBox(form = item)
    }
}

@Composable
fun TemplateTripleBox(form: TemplateItemTriple,
                      prefix:String,
                      templateGenerator: (String, String)-> Unit) {
    val input1 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.0", v.toString()) } }
    val input2 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.1", v.toString()) } }
    val input3 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.2", v.toString()) } }
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
fun TemplateRectBox(form: TemplateItemRect,
                    prefix:String,
                    templateGenerator: (String, String)-> Unit) {
    val input1 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.0", v.toString()) } }
    val input2 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.1", v.toString()) } }
    val input3 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.2", v.toString()) } }
    val input4 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.3", v.toString()) } }
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
fun TemplateSizeBox(form: TemplateItemSize,
                    prefix:String,
                    templateGenerator: (String, String)-> Unit) {
    val input1 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.0", v.toString()) } }
    val input2 = remember { NumericTextFieldState(0.0){ v -> templateGenerator("$prefix.1", v.toString()) } }
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
fun TemplateNumericBox(form: TemplateItemNumeric,
                       prefix:String,
                       templateGenerator: (String, String)-> Unit) {
    val input = remember { NumericTextFieldState(0.0){ v -> templateGenerator(prefix, v.toString()) } }
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
fun TemplateIntBox(form: TemplateItemInt,
                   prefix:String,
                   templateGenerator: (String, String)-> Unit) {
    val input = remember { NumericTextFieldState(0.0){ v -> templateGenerator(prefix, v.toString()) } }
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
fun TemplateCheckBox(form: TemplateItemCheck,
                     prefix:String,
                     templateGenerator: (String, String)-> Unit) {
    val checkState = remember { mutableStateOf(true) }
    RunCheckBox(
        checked = checkState.value,
        title = form.title,
        onCheckedChange = { c ->
            checkState.value = c
            templateGenerator(prefix, c.toString())
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
fun TemplateStringBox(form: TemplateItemString,
                      prefix:String,
                      templateGenerator: (String, String)-> Unit) {
    val text = remember { mutableStateOf("") }
    OutlinedTextField(
        value = text.value.toString(),
        onValueChange = {
            text.value = it
            templateGenerator(prefix, it)
        },
        label = { Text(form.title) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        enabled = true,
    )
}