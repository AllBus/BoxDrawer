import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val viewModel = DrawerViewModel()
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        val model = remember {mutableStateOf(viewModel)}
        LaunchedEffect(model.value){
            model.value.loadSettings()
        }

        App(model)
    }
}