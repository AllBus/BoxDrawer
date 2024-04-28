package com.kos.boxdrawe.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors

@Composable
fun SimpleEditText(title:String, postfix:String, value: State<String>,
                   fieldMaxWidth: Dp = 160.dp,
                   modifier: Modifier = Modifier,
                   enabled : Boolean = true,
                   titleWeight: Boolean = true,
                   onChange:(String) -> Unit
) {
    //var text by remember { mutableStateOf(title) }
    Row(
        modifier = modifier.padding(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        val textStyle = TextStyle.Default
        val colors = TextFieldDefaults.textFieldColors()
        val textColor = textStyle.color.takeOrElse {
            colors.textColor(enabled).value
        }

        val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
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
        BasicTextField(
            value = value.value,
            onValueChange = { v ->
                onChange(v)
            },
            modifier = Modifier
                .height(30.dp)
                .width(fieldMaxWidth)
                .weight(1f)
                //   .background(colors.backgroundColor(enabled).value)
                .background(ThemeColors.inputBackgroundState(enabled))
                .border(1.dp, ThemeColors.inputBorder).padding(4.dp),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = mergedTextStyle

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