import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.tabs
import javax.swing.UIManager
import androidx.compose.material.Text
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kos.boxdrawe.widget.display.DisplayGrid
import com.kos.boxdrawe.widget.display.DisplayTortoise
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

fun mainTest(args:Array<String>){

    val x = """{ "ia" : 492, "ib" : null, "d":{ "ba" : false, "bb" : null}}"""

    val gson = GsonBuilder().disableHtmlEscaping().setLenient().create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://RETROFIT_BASE_URL/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

//    val v = Gson()
//        .fromJson<Retro>(x, Retro::class.java)

 //   println(v)

    val rb = ResponseBody.create( MediaType.parse("text/json"), x    )
    val d = retrofit.responseBodyConverter<Retro>(Retro::class.java, emptyArray()).convert(rb)

   // val d = retrofit.stringConverter<Retro>(Retro::class.java, emptyArray()).convert(v)
    println(d)

}

data class Retro(
    val ia:Int?,
    val ic:Int?,
    val ib:Int,
    val d: RetroData?,
    val e: RetroData?,
)

data class RetroData(
    val ba:Boolean,
    val bb:Boolean?,
    val bc:Boolean?,
)

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
                        DisplayGrid(vm.grid.cad)
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

fun main(args:Array<String>) = application {
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
