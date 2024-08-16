package com.kos.boxdrawer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.icons.IconCopy
import com.kos.boxdrawe.presentation.CalculatorData
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.InputText
import com.kos.boxdrawe.widget.Label
import kotlinx.coroutines.launch

@Composable
fun CalculatorBox(modifier: Modifier, line: CalculatorData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier
    ) {
        Column(
            modifier = Modifier.padding(end = 4.dp).width(200.dp).verticalScroll(
                rememberScrollState()
            ).background(
                color = ThemeColors.editorBackground,
                shape = ThemeColors.figureListItemShape
            ),

            ) {

            val text = remember() {
                mutableStateOf(
                    TextFieldValue(line.text)
                )
            }
            val calculator = line.result.collectAsState("")
            Label("Посчитать")
            InputText(
                value = text.value,
                onValueChange = {
                    text.value = it
                    line.calculate(it.text)
                },
                label = "",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                enabled = true,
            )
            Row {
                ImageButton(
                    icon = IconCopy.rememberContentCopy(),
                    onClick = {
                        coroutineScope.launch {
                            clipboardManager.setText(AnnotatedString(calculator.value))
                        }
                    }
                )
                Text(calculator.value, modifier.weight(1f, true))
            }
        }
    }
}