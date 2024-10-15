package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.Tools
import com.kos.boxdrawe.widget.PlainTooltip
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.boardInfoTooltip
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoardInfoBlock(modifier: Modifier, tools: Tools, onClick: () -> Unit) {
    val selectedMovie = remember { tools.settings }
    val boardWeight = String.format("%1$,.2f", selectedMovie.value.boardWeight)
    PlainTooltip(
        tooltip = stringResource(Res.string.boardInfoTooltip)
    ) {
        Text(
            "${selectedMovie.value.name} ($boardWeight)",
            modifier = modifier.clickable { onClick() }
        )
    }
}