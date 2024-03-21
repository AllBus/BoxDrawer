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
    menu: State<TemplateForm>
) {

    Box(modifier = modifier) {
        TemplateFormBox(form = menu.value)
    }
}

@Composable
fun TemplateFormBox(
    modifier: Modifier = Modifier,
    form: TemplateForm
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
            TemplateItemBox(it)

        }

    }
}

@Composable
fun TemplateItemBox(item: TemplateItem) {
    when (item) {
        is TemplateForm -> TemplateFormBox(form = item)
        is TemplateItemNumeric -> TemplateNumericBox(form = item)
        is TemplateItemSize -> TemplateSizeBox(form = item)
        is TemplateItemRect -> TemplateRectBox(form = item)
        is TemplateItemTriple -> TemplateTripleBox(form = item)
        is TemplateItemInt -> TemplateIntBox(form = item)
        is TemplateItemString -> TemplateStringBox(form = item)
        is TemplateItemCheck -> TemplateCheckBox(form = item)
        is TemplateItemLabel -> TemplateLabelBox(form = item)
    }
}

@Composable
fun TemplateTripleBox(form: TemplateItemTriple) {
    val input1 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input2 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input3 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input3.value, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateRectBox(form: TemplateItemRect) {
    val input1 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input2 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input3 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input4 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input3.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input4.value, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateSizeBox(form: TemplateItemSize) {
    val input1 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    val input2 = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input1.value, modifier = Modifier.weight(1f))
        NumericUpDown("", "", input2.value, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateNumericBox(form: TemplateItemNumeric) {
    val input = remember { mutableStateOf(NumericTextFieldState(0.0)) }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input.value, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateIntBox(form: TemplateItemInt) {
    val input = remember { mutableStateOf(NumericTextFieldState(0.0, digits = 0)) }
    Row() {
        Label(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        NumericUpDown("", "", input.value, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TemplateCheckBox(form: TemplateItemCheck) {
    val checkState = remember { mutableStateOf(true) }
    RunCheckBox(
        checked = checkState.value,
        title = form.title,
        onCheckedChange = { c ->
            checkState.value = c
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
fun TemplateStringBox(form: TemplateItemString) {
    val text = remember { mutableStateOf("") }
    OutlinedTextField(
        value = text.value.toString(),
        onValueChange = {
            text.value = it
        },
        label = { Text(form.title) },
        singleLine = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        enabled = true,
    )
}