package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TopTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null
) {
    val styledText: @Composable (() -> Unit) = @Composable {
        val style = MaterialTheme.typography.titleSmall.copy(
            textAlign = TextAlign.Center
        )
        ProvideTextStyle(style, content = {
            Box(
                Modifier.height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1,
                )
            }
        })
    }

    androidx.compose.material3.Tab(
        selected,
        onClick,
        modifier,
        enabled,
        selectedContentColor,
        unselectedContentColor,
        interactionSource
    ) {
        styledText()
    }
}