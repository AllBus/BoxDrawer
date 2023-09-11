import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kos.boxdrawe.drawer.ComposeFigureDrawer
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.tabs
import figure.IFigure
import java.nio.file.Path
import javax.swing.UIManager
import androidx.compose.material.Text
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val vm = DrawerViewModel()

    val figures by remember { vm.tortoise.figures }

    val boxFigures by remember { vm.box.figures }

    var displayScale by remember { mutableStateOf(2.0f) }

    var dropValueX by remember { mutableStateOf(0f) }
    var dropValueY by remember { mutableStateOf(0f) }

    val tabIndex by remember { vm.tabIndex }
    val helpText by remember { vm.tortoise.helpText }

    MaterialTheme {
        Column {
            TabBar(tabs, vm)

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02007C))) {
                when (tabIndex) {
                    TAB_TORTOISE -> {
                        DisplayTortoise(displayScale, figures)
                        Text(
                            text = helpText,
                            modifier = Modifier.width(350.dp).wrapContentHeight().align(Alignment.TopStart).padding(8.dp),
                            fontSize = 10.sp
                        )
                    }
                    TAB_SOFT -> {
                        DisplayTortoise(displayScale, vm.softRez.drawRez(figures))
                    }
                    TAB_BOX -> {
                        DisplayTortoise(displayScale, boxFigures)
                    }
                    TAB_GRID -> {
                        DisplayGrid()
                    }
                    else -> {

                    }
                }


                Slider(
                    modifier = Modifier.width(300.dp).wrapContentHeight().align(Alignment.TopEnd),
                    onValueChange = { dropValueX = it; vm.tortoise.drop(dropValueX,dropValueY) },
                    value = dropValueX,
                    valueRange = -100f..100f
                )

                Slider(
                    modifier = Modifier.padding(top = 50.dp).width(300.dp).wrapContentHeight().align(Alignment.TopEnd),
                    onValueChange = { dropValueY = it; vm.tortoise.drop(dropValueX,dropValueY) },
                    value = dropValueY,
                    valueRange = -100f..100f
                )

                Slider(
                    modifier = Modifier.width(300.dp).wrapContentHeight().align(Alignment.BottomEnd),
                    onValueChange = { displayScale = it },
                    value = displayScale,
                    valueRange = 1f..100f
                )
            }
        }
    }
}

fun main() = application {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } finally {

    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Рисовалка коробок",
    ) {

        App()
    }
}
