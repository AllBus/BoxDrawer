package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunCheckBox(
    checked: Boolean,
    title:String,
    onCheckedChange: (Boolean) -> Unit,

) = Row(){
    CheckboxK(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.align(Alignment.CenterVertically),
    )

    Text(
        text = title,
        modifier = Modifier.align(Alignment.CenterVertically).onClick(
            onClick = { onCheckedChange(!checked) }
        ),
        textAlign = TextAlign.Center,
        softWrap = false,
    )
}

@Composable
fun CheckboxK(checked: Boolean,
              onCheckedChange: ((Boolean) -> Unit)?,
              modifier: Modifier = Modifier,
              enabled: Boolean = true,
              interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
              colors: CheckboxColors = CheckboxDefaults.colors()
){
    val onClick = if (onCheckedChange != null) { { onCheckedChange(!checked) } } else null
    val state = ToggleableState(checked)

    val toggleableModifier =
        if (onClick != null) {
            Modifier.triStateToggleable(
                state = state,
                onClick = onClick,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = 16.dp
                )
            )
        } else {
            Modifier
        }

    Box(
//        enabled = enabled,
     //   value = state,
        modifier = modifier
            .then(toggleableModifier)
            .padding(2.dp).size(20.dp).background(
                color = colors.checkmarkColor(ToggleableState.On).value
            ).border(1.dp, color = colors.borderColor(enabled, state).value)
            .padding(4.dp).background(
                color = colors.boxColor(enabled,state).value
            ),
   //     colors = colors
    )
}

@Composable
@Preview
private fun RunCheckBoxPreview(){
    MaterialTheme {
        RunCheckBox(true,"Нарисовать деталь",{})
    }
}