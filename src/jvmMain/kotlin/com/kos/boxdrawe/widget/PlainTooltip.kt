package com.kos.boxdrawe.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PlainTooltip(
    tooltip:String,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            Text(
                text = tooltip,
                modifier = Modifier.background(ThemeColors.tooltipBackground, shape = ThemeColors.tooltipShape).border(1.dp, ThemeColors.tooltipBorder).padding(horizontal = 8.dp, vertical = 2.dp),
                color = ThemeColors.tooltipTextColor
            )
        },
        state = rememberTooltipState(),
        content = content
    )
}