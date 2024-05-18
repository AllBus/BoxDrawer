import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.gson.GsonBuilder
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.app_name
import com.kos.boxdrawer.presentation.App
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.jetbrains.compose.resources.stringResource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.swing.UIManager

fun mainTest(args: Array<String>) {

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

    val rb = ResponseBody.create(MediaType.parse("text/json"), x)
    val d = retrofit.responseBodyConverter<Retro>(Retro::class.java, emptyArray()).convert(rb)

    // val d = retrofit.stringConverter<Retro>(Retro::class.java, emptyArray()).convert(v)
    println(d)

}

data class Retro(
    val ia: Int?,
    val ic: Int?,
    val ib: Int,
    val d: RetroData?,
    val e: RetroData?,
)

data class RetroData(
    val ba: Boolean,
    val bb: Boolean?,
    val bc: Boolean?,
)

fun calcZoom(value: Float): Float {

    return kotlin.math.log(value.toDouble(), 1.2).toFloat()
//    var i = 1
//    var m = 1.2
//    while (value>m){
//        m*=1.2
//        i++
//    }
//    return i.toFloat()
}

fun main(args: Array<String>) {

    val viewModel = DrawerViewModel()
    application {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } finally {

        }
        val model = remember { mutableStateOf(viewModel) }

        Window(
            onCloseRequest = ::exitApplication,
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