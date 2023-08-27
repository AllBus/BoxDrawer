import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.tabs
import com.kos.boxdrawe.widget.TabBar
import javax.swing.UIManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    val vm = DrawerViewModel()

    val figures by remember { vm.tortoise.figures }

    val tabIndex by remember { vm.tabIndex }

    val previewPage by remember { vm.pageFile }

    MaterialTheme {
        Column {
            TabBar(tabs, vm)
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02007C))) {
                when (tabIndex) {
                    TAB_TORTOISE -> {
                        Canvas(modifier = Modifier.fillMaxSize().clipToBounds(),
                            onDraw = {
                                this.drawFigures(figures)

                            })
                    }

                    TAB_BOX -> {
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
