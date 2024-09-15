package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawe.widget.model.ButtonDoubleData
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.act_hole
import com.kos.boxdrawer.generated.resources.act_line
import com.kos.boxdrawer.generated.resources.act_paz
import com.kos.boxdrawer.generated.resources.act_paz_in

@Composable
fun SegmentButton(
    selectId: State<Int>,
    buttons: List<ButtonData>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {

    Row(
        modifier = modifier
    ) {
        buttons.forEach { btn ->
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onClick(btn.id) },
                shape = RectangleShape,
                border = if (btn.id == selectId.value) {
                    BorderStroke(4.dp, MaterialTheme.colors.onPrimary)
                } else null,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = btn.icon,
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
    modifier: Modifier = Modifier,
    lines: Int = 1,
    onClick: (Int) -> Unit,
) {
    val le = (buttons.size - 1) / lines + 1
    Column(Modifier.semantics(mergeDescendants = true) { }) {
        buttons.windowed(le, le, true).forEach { btns ->
            Row(
                modifier = modifier
            ) {
                btns.forEach { btn ->
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onClick(btn.id) },
                        shape = RectangleShape,
                        border = if (btn.id == selectId.value) {
                            BorderStroke(4.dp, MaterialTheme.colors.onPrimary)
                        } else null,
                        contentPadding = PaddingValues(4.dp, 4.dp)
                    ) {
                        Text(
                            btn.text,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SegmentButtonPreview() = MaterialTheme {
    val selectId = remember { mutableStateOf(0) }
    val zigVariants = listOf(
        ButtonData(0, painterResource( Res.drawable.act_hole)),
        ButtonData(1, painterResource(Res.drawable.act_line)),
        ButtonData(2, painterResource(Res.drawable.act_paz)),
        ButtonData(3, painterResource(Res.drawable.act_paz_in)),
    )
    SegmentButton(selectId, zigVariants) { id -> selectId.value = id }

}

