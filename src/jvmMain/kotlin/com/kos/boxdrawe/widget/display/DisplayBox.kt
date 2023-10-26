package com.kos.boxdrawe.widget.display

import LocalResourceLoader
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.nio.file.Path

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisplayBox(previewPage: Path?) {
    previewPage?.let { path ->
        Image(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(
                resourcePath = path.toString(),
                loader = LocalResourceLoader()
            ),
            contentDescription = "Page preview"
        )
    }
}