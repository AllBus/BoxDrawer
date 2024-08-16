package com.kos.boxdrawe.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors

@Composable
fun EditText(
    title: String,
    value: MutableState<String>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value.value,
            onValueChange = {
                value.value = it
                onChange(it)
            },
            label = { Text(title) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = modifier.fillMaxSize(),
            enabled = enabled,
        )
    }
}

@Composable
fun Modifier.verticalScrollbar(
    state: ScrollState,
    scrollbarWidth: Dp = 6.dp,
    color: Color = Color.Red
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (state.isScrollInProgress) 1f else 0f,
        animationSpec = tween(400, delayMillis = if (state.isScrollInProgress) 0 else 700)
    )

    return this then Modifier.drawWithContent {
        drawContent()


        val viewHeight = state.viewportSize.toFloat()
        val contentHeight = state.maxValue + viewHeight
        if (contentHeight > 0) {
            val scrollbarHeight =
                (viewHeight * (viewHeight / contentHeight)).coerceIn(10.dp.toPx()..viewHeight)
            val variableZone = viewHeight - scrollbarHeight
            val scrollbarYoffset = (state.value.toFloat() / state.maxValue) * variableZone

            drawRoundRect(
                cornerRadius = CornerRadius(scrollbarWidth.toPx() / 2, scrollbarWidth.toPx() / 2),
                color = color,
                topLeft = Offset(this.size.width - scrollbarWidth.toPx(), scrollbarYoffset),
                size = Size(scrollbarWidth.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}

@Composable
fun EditTextField(
    title: String,
    value: MutableState<TextFieldValue>,
    enabled: Boolean = true,
    onChange: (TextFieldValue) -> Unit,
    onMove: (TextFieldValue) -> Unit,
) {
    //val lastFocusValue = remember { mutableStateOf(value.value.selection) }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value.value,
            onValueChange = {

          //      lastFocusValue.value = it.selection
                val change = (value.value.text != it.text)
                value.value = it
                onMove(it)
                if (change) {
                    onChange(it)
                }
            },
            label = { Text(title) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            )
    }
}
