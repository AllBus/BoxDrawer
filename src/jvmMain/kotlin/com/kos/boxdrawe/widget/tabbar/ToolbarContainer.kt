package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarContainer(
    content: @Composable BoxScope.() -> Unit,
    actionsBlock: @Composable BoxScope.() -> Unit
) {
    Row {
        Box(modifier = Modifier.weight(1f), content = content)
        Box(modifier = Modifier.width(160.dp).padding(4.dp), content = actionsBlock)
    }
}