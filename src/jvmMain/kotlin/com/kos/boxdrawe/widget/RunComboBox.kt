package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.widget.model.ComboBoxItem
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RunComboBox(
    label:String,
    selected: ComboBoxItem,
    items : List<ComboBoxItem>,
    onClick: (ComboBoxItem) -> Unit,
){
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        // textfield
        TextField(
            modifier = Modifier, // menuAnchor modifier must be passed to the text field for correctness.
            readOnly = true,
            value = stringResource(selected.title),
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        // menu
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            },
        ) {
            // menu items
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onClick(item)
                        expanded.value = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                ) {
                    Text(stringResource(item.title))
                }
            }
        }
    }
}