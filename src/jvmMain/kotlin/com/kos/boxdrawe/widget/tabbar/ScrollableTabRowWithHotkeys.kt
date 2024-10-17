package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScrollableTabRowWithHotkeys(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier =Modifier,
    tabs: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    // LaunchedEffect to request focus when the composable enters the composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = modifier.focusRequester(focusRequester)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.onPreviewKeyEvent { event ->
                if (event.key == Key.AltLeft || event.key == Key.AltRight) {
                    // Consume Alt key press to prevent default behavior
                    true
                } else if (event.type == KeyEventType.KeyDown && event.key.keyCode in Key.Zero.keyCode..Key.Nine.keyCode) {
                    // Check if Alt is pressed
                    if (event.isAltPressed) {
                        val tabIndex = (event.key.keyCode - Key.Zero.keyCode).toInt()
                        if (tabIndex in 0 until 10) { // Limit to 10 tabs
                            if (tabIndex == 0)
                                onTabSelected(9)
                            else
                                onTabSelected(tabIndex-1)
                        }
                    }
                    true
                } else {
                    false
                }
            },
        ) {
            tabs()
        }
    }
}