package com.kos.boxdrawer.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import kotlinx.coroutines.launch

@Composable
fun FigureListBox(
    figure: List<FigureInfo>,
    selectedItem: State<List<FigureInfo>>,
    onClick: (FigureInfo) -> Unit
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        state = scrollState,
        modifier = Modifier.padding(start = 1.dp).fillMaxSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        FigureItems(figures = figure, selectedItem = selectedItem, onClick = onClick)
    }

}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.FigureItems(
    figures: List<FigureInfo>,
    selectedItem: State<List<FigureInfo>>,
    onClick: (FigureInfo) -> Unit
) {
    items(figures) { figure ->
        Column(
            modifier = Modifier
                .border(1.dp, ThemeColors.figureListBorder, ThemeColors.figureListItemShape)
                .background(
                    if (figure.figure in selectedItem.value.map { it.figure })
                        MaterialTheme.colors.primary else
                        ThemeColors.figureListBackground, ThemeColors.figureListItemShape
                )
                .width(300.dp).onClick {
                    onClick(figure)
                }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = figure.figure.name(),
                maxLines = 3,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.End
            )
//            Text(
//                text = figure.transform.values.joinToString(" ")
//            )
//            Text(
//                text = figure.figure.transform.values.joinToString(" "),
//                color = Color.Yellow
//            )
        }
    }

//    items(figure.collection()) { f ->
//        FigureListBox(f)
//    }
}
