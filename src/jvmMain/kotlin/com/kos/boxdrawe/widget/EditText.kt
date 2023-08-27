package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditText(title:String, postfix:String, value: MutableState<String>, enabled : Boolean = true, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value.value.toString(),
            onValueChange = {
                value.value = it
                onChange(it)
            },
            label = { Text(title) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,

        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = postfix,
            fontSize = LocalTextStyle.current.fontSize,
            modifier = Modifier.align(Alignment.CenterVertically),
            softWrap = false,
        )
    }
}