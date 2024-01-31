package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun Label(text:String, modifier : Modifier = Modifier, singleLine: Boolean = true) {
    Text(
        text = AnnotatedString(text),
        modifier = modifier,
        style = MaterialTheme.typography.subtitle2,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        overflow = TextOverflow.Clip
    )
}


@Preview
@Composable
private fun SegmentButtonPreview() = MaterialTheme {
    Label(
        "Primer"
    )
}