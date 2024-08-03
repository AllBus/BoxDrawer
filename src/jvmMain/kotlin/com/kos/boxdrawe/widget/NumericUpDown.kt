package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors


@Composable
fun NumericUpDown(
    title: String,
    postfix: String,
    value: NumericTextFieldState,
    fieldMaxWidth: Dp = 160.dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleWeight: Boolean = true,
) {
    //var text by remember { mutableStateOf(title) }
    Row(
        modifier = modifier.padding(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = LocalTextStyle.current.fontSize,
                modifier = if (titleWeight) Modifier.align(Alignment.CenterVertically)
                    .weight(1f) else Modifier,
                softWrap = false,
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.width(4.dp))
        }
        InputNumeric(
            value = value,
            modifier = Modifier
                .height(ThemeColors.NumericFieldHeight)
                .width(fieldMaxWidth)
                .weight(1f),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        if (postfix.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = postfix,
                fontSize = LocalTextStyle.current.fontSize,
                modifier = Modifier.align(Alignment.CenterVertically),
                softWrap = false,
            )
        }
    }
}

@Composable
fun NumericUpDownLine(
    title: String,
    postfix: String,
    value: NumericTextFieldState,
    fieldMaxWidth: Dp = 160.dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleWeight: Boolean = true,
) {
    Row(
        modifier = modifier.padding(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = LocalTextStyle.current.fontSize,
                modifier = if (titleWeight) Modifier.align(Alignment.CenterVertically)
                    .weight(1f) else Modifier,
                softWrap = false,
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.width(4.dp))
        }
        val modifier = Modifier.height(ThemeColors.NumericFieldHeight)
            .width(fieldMaxWidth).weight(1f)
        InputNumeric(
            value = value,
            modifier = modifier,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        LineBox(
            modifier = Modifier.size(12.dp, ThemeColors.NumericFieldHeight)
                .background(ThemeColors.inputBackgroundState(enabled)),
            { value.decimal }) { current, change, start ->
            if (enabled) {
                value.update(current)
            }
        }

        if (postfix.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = postfix,
                fontSize = LocalTextStyle.current.fontSize,
                modifier = Modifier.align(Alignment.CenterVertically),
                softWrap = false,
            )
        }
    }
}

@Composable
fun InputNumeric(
    value: NumericTextFieldState,
    modifier: Modifier,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
) {
    val textStyle = TextStyle.Default
    val colors = TextFieldDefaults.textFieldColors()
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value.text,
        onValueChange = { v: TextFieldValue ->
            value.update(v)
        },
        modifier = modifier
            .background(ThemeColors.inputBackgroundState(enabled))
            .border(1.dp, ThemeColors.inputBorder).padding(4.dp),
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = mergedTextStyle
    )
}

@Composable
fun InputText(
    modifier: Modifier,
    value: TextFieldValue,
    enabled: Boolean,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label:String? = null,
    onValueChange: (TextFieldValue) -> Unit
) {
    val textStyle = TextStyle.Default
    val colors = TextFieldDefaults.textFieldColors()
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(ThemeColors.inputBackgroundState(enabled))
            .border(1.dp, ThemeColors.inputBorder).padding(4.dp),
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        textStyle = mergedTextStyle,

    )
}

@Composable
fun NumericUpDownTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val shape = TextFieldDefaults.OutlinedTextFieldShape

    @OptIn(ExperimentalMaterialApi::class)
    (BasicTextField(
        value = value,
        modifier = if (label != null) {
            modifier
                // Merge semantics at the beginning of the modifier chain to ensure padding is
                // considered part of the text field.
                .semantics(mergeDescendants = true) {}
                .padding(top = 4.dp)
        } else {
            modifier
        }
            .background(colors.backgroundColor(enabled).value, shape)
            .defaultMinSize(
                minWidth = 160.dp,
                minHeight = 34.dp,
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                label = label,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
            )
        }
    ))
}

@Composable
@Preview
fun NumericUpDownPreview() {
    MaterialTheme {
        NumericUpDown("Ширина", "мм", NumericTextFieldState(20.0))
    }
}

class NumericTextFieldState(
    value: Double,
    private val digits: Int = 2,
    private val maxValue: Double = 1000000.0,
    private val minValue: Double = 0.0,
    private val updateAction: (Double) -> Unit = {}
) {
    var decimal: Double by mutableStateOf(value)

    private val fieldValue =
        mutableStateOf(TextFieldValue(String.format("%1$,.${digits}f", decimal)))

    private val spaceReg = "[\\s\\u00A0]+".toRegex()

    fun update(newValue: String) {
        val v = newValue.replace(spaceReg, "").replace(',', '.').toDoubleOrNull()
        if (v != null && v != decimal) {
            if (v > maxValue)
                decimal = maxValue
            else
                decimal = v

            updateAction(decimal)
            fieldValue.value = TextFieldValue(String.format("%1$,.${digits}f", decimal))
        }
    }

    fun update(newValue: Double) {
        val nv = newValue.coerceIn(minValue, maxValue)
        if (nv != decimal) {
            decimal = nv
            updateAction(decimal)
            fieldValue.value = TextFieldValue(String.format("%1$,.${digits}f", decimal))
        }
    }

    fun update(newValue: TextFieldValue) {
        val v = newValue.text.replace(spaceReg, "").replace(',', '.').toDoubleOrNull()

        var u = false
        if (v != null && v != decimal) {
            decimal = v.coerceIn(minValue, maxValue)
            u = true
            updateAction(decimal)
        }

        if (u) {
            fieldValue.value = newValue.copy(String.format("%1$,.${digits}f", decimal))
        } else {
            fieldValue.value = newValue
        }
    }

    val text get() = fieldValue.value // String.format("%1$,.${digits}f", decimal);


}


