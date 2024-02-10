import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {

    val viewModel = DrawerViewModel()
    application {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } finally {

        }
        val model = remember { mutableStateOf(viewModel) }

        Window(
            onCloseRequest = ::exitApplication,
            icon = painterResource("drawable/ic_launcher.png"),
            title = "Рисовалка коробок",
        ) {
            LaunchedEffect(model.value) {
                model.value.loadSettings()
            }
            App(model)
        }
    }
}
