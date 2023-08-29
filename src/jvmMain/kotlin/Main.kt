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
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.tabs
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
                        DisplayTortoise(displayScale, vm.softRez.drawRez(figures))
                       // DisplaySoft(displayScale, vm, figures)
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
