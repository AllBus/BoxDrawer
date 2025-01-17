package com.kos.boxdrawe.widget.tabbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarContainer(
    pagerState: State<Int>,
    content: @Composable BoxScope.(Int) -> Unit,
    actionsBlock: @Composable BoxScope.() -> Unit
) {
    Row {
//        HorizontalPager(
//            pagerState,
//            modifier = Modifier.weight(1f).clipToBounds(),
//        ) { page ->
//            Box(modifier = Modifier) {
//                content(page)
//            }
//        }

        AnimatedContent(
            targetState = pagerState.value,
            modifier = Modifier.weight(1f).clipToBounds(),
            transitionSpec = {
                val napr = if (targetState > initialState) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else
                    AnimatedContentTransitionScope.SlideDirection.Right

                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = napr
                ).togetherWith(
                    slideOutOfContainer(
                        animationSpec = tween(300),
                        towards = napr
                    )
                )
            }

                // Compare the incoming number with the previous number.
//                if (targetState > initialState) {
                    // If the target number is larger, it slides up and fades in
                    // while the initial (smaller) number slides up and fades out.

//                    slideInHorizontally(
//                        animationSpec = tween(3000),
//                        initialOffsetX = { height -> height }) togetherWith
//                            slideOutHorizontally(
//                                animationSpec = tween(3000),
//                            ) { height -> -height }
//                } else {
//                    // If the target number is smaller, it slides down and fades in
//                    // while the initial number slides down and fades out.
//                    (slideInHorizontally { height -> -height }).togetherWith(
//                        slideOutHorizontally { height -> height })
//                }.using(
//                    // Disable clipping since the faded slide-in/out should
//                    // be displayed out of bounds.
//                    SizeTransform(clip = false)
//                )
            //}
        ) { t ->
            Box(modifier = Modifier) {
                content(t)
            }
        }

        Box(modifier = Modifier.width(160.dp).padding(4.dp), content = actionsBlock)
    }
}