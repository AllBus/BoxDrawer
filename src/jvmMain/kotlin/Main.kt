import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
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
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.tabs
import com.kos.boxdrawe.widget.TabBar
import figure.IFigure
import java.nio.file.Path
import javax.swing.UIManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    val vm = DrawerViewModel()

    val figures by remember { vm.tortoise.figures }

    var displayScale by remember { mutableStateOf(2.0f) }

    val tabIndex by remember { vm.tabIndex }

    val previewPage by remember { vm.pageFile }

    MaterialTheme {
        Column {
            TabBar(tabs, vm)
            Slider(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                onValueChange = { displayScale = it },
                value = displayScale,
                valueRange = 1f..100f
            )
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02007C))) {

                when (tabIndex) {
                    TAB_TORTOISE -> {
                        DisplayTortoise(displayScale, figures)
                    }

                    TAB_SOFT -> {
                        DisplaySoft(displayScale, vm, figures)
                    }

                    TAB_BOX -> {
                        DisplayBox(previewPage)
                    }
                    TAB_GRID -> {
                        DisplayGrid()
                    }


                    else -> {

                    }
                }

            }
        }
    }
}

@Composable
private fun DisplayGrid(){

}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DisplayBox(previewPage: Path?) {
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

@Composable
private fun DisplaySoft(
    displayScale: Float,
    vm: DrawerViewModel,
    figures: IFigure
) {
    Canvas(modifier = Modifier.fillMaxSize().clipToBounds(),
        onDraw = {
            val penColor = Color.Gray
            val style = Stroke(width = 1.0f)
            val c = size / 2f
            this.scale(scale = Math.log(displayScale.toDouble()).toFloat()) {
                this.translate(c.width, c.height) {
                    val drawer = ComposeFigureDrawer(this, penColor, style)
                    vm.softRez.drawRez(figures, drawer)
                }
            }
        })
}

@Composable
private fun DisplayTortoise(displayScale: Float, figures: IFigure) {
    Canvas(modifier = Modifier.fillMaxSize().clipToBounds(),
        onDraw = {
            val c = size / 2f
            this.scale(scale = Math.log(displayScale.toDouble()).toFloat()) {
                this.translate(c.width, c.height) {

                    this.drawFigures(figures)
                }
            }

        })
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
