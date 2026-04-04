
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kos.ariphmetica.Calculator
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.app_name
import com.kos.boxdrawer.presentation.App
import com.kos.example.Loading
import com.kos.example.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import java.io.File
import java.math.BigDecimal
import java.text.BreakIterator
import javax.swing.UIManager


fun main2(args: Array<String>){

    runBlocking {
        val a =  flow {
            emit(Loading)
            delay(90)
            emit(2)
            delay(50)
            emit("A")
            delay(1010)
            emit(4)
            emit(Result.Success("B"))
            delay(10)
            emit(Result.Success(56))
            delay(1500)
            emit(Result.Error(NullPointerException()))
            delay(800)
            emit(8)
            delay(1200)
            emit(9)
        }.filterIsInstance<Result.Success<*>>().firstOrNull()

        println(a)
//            .map{ a ->
//            a+10
//        }.onEach{ a ->
//            println("check $a")
//        }.debounce{
//            if (it == 1) {
//                0L
//            } else {
//                1000L
//            }
//        }.onEach { a ->
//            println("pre $a")
//        }.flatMapLatest { a ->
//           ensureActive()
//           println("flat $a")
//           delay(1000)
//           performLongRunningOperation(a)
//        }.onEach { a ->
//
//            println("transform $a")
//        }.collect { v -> println(v) }
    }
}

// Функция, имитирующая длительную операцию
fun performLongRunningOperation(number: Int): Flow<Int> = flow {
    // Имитация длительной операции (например, 500 мс)
    println("start $number")
    delay(1200)
    println("perform $number")
    emit(number*100) // Возвращаем результат операции
}

fun main(args: Array<String>) {
    Calculator.init()
  //  val component = DaggerAppComponent.builder()
    val viewModel = DrawerViewModel()
    application {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } finally {

        }
        val model = remember { mutableStateOf(viewModel) }

        Window(
            onCloseRequest = {
                model.value.saveState()
                exitApplication()
            },
            icon = painterResource( "drawable/robot.ico"),
            title = stringResource(Res.string.app_name),
        ) {
            LaunchedEffect(model.value) {
                model.value.loadSettings()
            }
            App(model)
        }
    }
}

