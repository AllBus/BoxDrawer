package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawe.widget.model.ButtonDoubleData

@Composable
fun SegmentButton(
    selectId: State<Int>,
    buttons: List<ButtonData>,
    modifier : Modifier = Modifier,
    onClick: (Int)-> Unit
    ) {

    Row(
        modifier = modifier
    ) {
        buttons.forEach{  btn ->
            Button(
                modifier = Modifier.weight(1f),
                onClick ={ onClick(btn.id) },
                shape = RectangleShape,
                border = if (btn.id == selectId.value) {
                    BorderStroke(4.dp, MaterialTheme.colors.onPrimary)
                } else null,
            ){
                Icon(painter = btn.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SegmentDoubleButton(
    selectId: State<Int>,
    buttons: List<ButtonDoubleData>,
    modifier : Modifier = Modifier,
    onClick: (Int)-> Unit
) {
    Row(
        modifier = modifier
    ) {
        buttons.forEach{  btn ->
            Button(
                modifier = Modifier.weight(1f),
                onClick ={ onClick(btn.id) },
                shape = RectangleShape,
                border = if (btn.id == selectId.value) {
                    BorderStroke(4.dp, MaterialTheme.colors.onPrimary)
                } else null,
            ){
                Text(
                    btn.text
                )
            }
        }
    }
}

@Preview
@Composable
private fun SegmentButtonPreview() = MaterialTheme {
    val selectId = remember{ mutableStateOf(0) }
    val zigVariants = listOf(
        ButtonData(0, painterResource("drawable/act_hole.png")),
        ButtonData(1, painterResource("drawable/act_line.png")),
        ButtonData(2, painterResource("drawable/act_paz.png")),
        ButtonData(3, painterResource("drawable/act_paz_in.png")),
    )
    SegmentButton(selectId, zigVariants) { id -> selectId.value = id }

}