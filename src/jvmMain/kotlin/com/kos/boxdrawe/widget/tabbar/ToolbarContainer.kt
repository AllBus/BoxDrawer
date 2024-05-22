package com.kos.boxdrawe.widget.tabbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarContainer(
    tabIndex: State<Int>,
    content: @Composable BoxScope.() -> Unit,
    actionsBlock: @Composable BoxScope.() -> Unit
) {
    Row {
        AnimatedContent(
            targetState = tabIndex.value,
            modifier = Modifier.weight(1f).clipToBounds(),
            transitionSpec = {
                // Compare the incoming number with the previous number.
                if (targetState > initialState) {
                    // If the target number is larger, it slides up and fades in
                    // while the initial (smaller) number slides up and fades out.
                    slideInHorizontally { height -> height } togetherWith
                            slideOutHorizontally { height -> -height }
                } else {
                    // If the target number is smaller, it slides down and fades in
                    // while the initial number slides down and fades out.
                    (slideInHorizontally { height -> -height }).togetherWith(
                        slideOutHorizontally { height -> height })
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )
            }
        ) {
            Box(modifier = Modifier, content = content)
        }

        Box(modifier = Modifier.width(160.dp).padding(4.dp), content = actionsBlock)
    }
}