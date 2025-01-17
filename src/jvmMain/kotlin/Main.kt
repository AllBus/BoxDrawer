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
import org.jetbrains.compose.resources.stringResource
import java.io.File
import java.math.BigDecimal
import javax.swing.UIManager
import kotlin.math.pow

fun ipToLong(ip: String): Long {
    val parts = ip.split(".")
  //  println(parts)
    return (parts[0].toLong() shl 24) or
            (parts[1].toLong() shl 16) or
            (parts[2].toLong() shl 8)  or
            parts[3].toLong()
}

fun longToIp(ip: Long): String {
    return ((ip shr 24) and 0xFF).toString() + "." +
            ((ip shr 16) and 0xFF).toString() + "." +
            ((ip shr 8) and 0xFF).toString() + "." +
            (ip and 0xFF).toString()
}

fun isIpInRange(ipAsLong: Long, rangeStartAsLong: Long, mask: Int): Boolean {

  //  val ipAsLong = ipToLong(ip)
  //  val rangeStartAsLong = ipToLong(rangeStart)
    val maskAsLong = -1L shl (32 - mask) // Создаем маску подсети

    return (ipAsLong and maskAsLong) == (rangeStartAsLong and maskAsLong)
}

fun printBits(ipToLong: Long) {
    for (i in 0 until 32){
        if (i %8 ==0)
            print(" ")
        print("${(ipToLong and (1L shl i)) shr i}")

    }
    println()
//    for (i in 0 until 32){
//        print("${(i+1) %10}")
//    }
//    println()
}

fun groupIpsIntoRanges(ips: List<String>, filterRanges:List<String>): List<Pair<Long, Long>> {
    printBits(ipToLong("34.0.128.0"))

    val fil = filterRanges.map { it.split("/") }.map {
        ipToLong(it[0]) to it[1].toInt()
    }

    val sortedIps = ips.map { ipToLong(it) }.filter { ip ->
        !fil.any {
            isIpInRange(ip, it.first, it.second)
        }
    }.sorted()

    val ranges = mutableListOf<Pair<Long, Long>>()

    println("A size ${sortedIps.size}")
    if (sortedIps.isNotEmpty()) {
        var currentRangeStart = sortedIps[0]
        var pred = sortedIps[0]
        // var currentRangeMask = -1L // Начальная маска - все хосты
        for (i in 1 until sortedIps.size) {
            val ip = sortedIps[i]
            if (ip != pred + 1) {
                ranges.add(Pair(currentRangeStart, pred))
                currentRangeStart = ip
                pred = ip
            } else {
                pred = ip
            }
            // Расширяем маску, пока следующий IP входит в диапазон
//        var mask = currentRangeMask
//        while (mask != 0L && (ip and mask) == (currentRangeStart and mask)) {
//            mask = mask and mask - 1L // Сбрасываем последний единичный бит
//        }
//        currentRangeMask = mask
        }
        ranges.add(Pair(currentRangeStart, pred))
    }

    sortedIps.forEach {
        println(longToIp(it)+"/32")
    }
    return ranges
}


fun maine(args: Array<String>) {

    val addresses = File("C:\\Users\\Коська\\Downloads\\Telegram Desktop\\all.ips.txt").readLines()
    val adrang = File("C:\\Users\\Коська\\Downloads\\Telegram Desktop\\ranges.txt").readLines()

    println(adrang.joinToString(", "))

    println("Adress ${addresses.size}")
    println("rd ${adrang.size}")
    val ranges =  groupIpsIntoRanges(addresses, adrang)
    ranges.forEach { (start, mask) ->
        println("${longToIp(start)}/${longToIp(mask)}")
    }
//    //, "d":{ "ba" : false, "bb" : null}}
//    val x = """{"r" :{ "ia" : 492, "ib" : null}}"""
//
//    val gson = GsonBuilder().disableHtmlEscaping().setLenient().create()
//
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://RETROFIT_BASE_URL/")
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build()
//
////    val v = Gson()
////        .fromJson<Retro>(x, Retro::class.java)
//
//    //   println(v)
//
//    val rb = ResponseBody.create(MediaType.parse("text/json"), x)
//    println(rb)
//    val d = retrofit.responseBodyConverter<RetroMaze>(RetroMaze::class.java, emptyArray()).convert(rb)
//
//  //  val d = gson.fromJson<Retro>(x, Retro::class.java)
//  // val d = retrofit.stringConverter<Retro>(Retro::class.java, emptyArray()).convert(v)
//    println(d)

}
data class RetroMaze(
    val x: Int?,
    val r: Retro?
)
data class Retro(
    val ia: Int? = 60,
    val ic: Int? = 34,
    val ib: Int = 900,
    val d: RetroData? = RetroData(
        ba = true,
        bb = null,
        bc = false,
    ),
    val e: RetroData? = null,
)

data class RetroData(
    val ba: Boolean,
    val bb: Boolean?,
    val bc: Boolean?,
)

fun nonvec222() {
    vectors.maina()
    val rr = Retro()

    println(rr.d is RetroData)
    println(rr.e is RetroData)
    checkA(4)

    val start = System.currentTimeMillis()
    val count = Math.pow(2.0, 2.0).toInt()
    val result = mutableListOf<Retro>()
    repeat(count) {
        result.add(
            Retro(
                ia = it
            )
        )
    }
    val end = System.currentTimeMillis()
    println("Complete")
    println(end - start)
    println(count)
    println(result.size)
    println(1 shl 4)
}

fun main1(args: Array<String>){

    val cls =listOf( CalcInfo(
        listOf(BundleGroup(
            300,
            10,
            90,
            "A"
        )
        )),
        CalcInfo(
        listOf(
            BundleGroup(
                300,
                10,
                90,
                "A"
            ),
            BundleGroup(
            200,
            13,
            95,
            "B"
        )
        )),
        CalcInfo(
            listOf(
                BundleGroup(
                    300,
                    10,
                    90,
                    "A"
                ),
                BundleGroup(
                    200,
                    13,
                    95,
                    "B"
                ),
                BundleGroup(
                    405,
                    18,
                    99,
                    "C"
                )
            )),
        CalcInfo(
            listOf(
                BundleGroup(
                    300,
                    10,
                    90,
                    "A"
                ),
                BundleGroup(
                    200,
                    13,
                    95,
                    "B"
                ),
                BundleGroup(
                    405,
                    18,
                    99,
                    "C"
                ),
                BundleGroup(
                    120,
                    19,
                    5,
                    "D"
                )
            )),
        CalcInfo(
            listOf(
                BundleGroup(
                    300,
                    10,
                    90,
                    "A"
                ),
                BundleGroup(
                    200,
                    13,
                    95,
                    "B"
                ),
                BundleGroup(
                    405,
                    18,
                    99,
                    "C"
                ),
                BundleGroup(
                    120,
                    19,
                    5,
                    "D"
                ),
                BundleGroup(
                    367,
                    11,
                    8,
                    "E"
                )
            )),
                CalcInfo(
                listOf(
                    BundleGroup(
                        2367,
                        111,
                        82,
                        "F"
                    ),
                    BundleGroup(
                        300,
                        10,
                        90,
                        "A"
                    ),
                    BundleGroup(
                        200,
                        13,
                        95,
                        "B"
                    ),
                    BundleGroup(
                        405,
                        18,
                        99,
                        "C"
                    ),
                    BundleGroup(
                        120,
                        19,
                        5,
                        "D"
                    ),
                    BundleGroup(
                        367,
                        11,
                        8,
                        "E"
                    )
                ))
    )

    cls.forEach { c1 ->
        val r1 = createTariffsWithBundles(  c1,true, )
        val r2 = createTariffsWithBundlesBinary(  c1,true, )
      //  println( r1.joinToString("\n"))
        println("${r1 == r2} --VVVVVVVVVVVV--")
       // println( r2.joinToString("\n"))
        println("----------------")
    }

}

class BundleGroup(
    val cost : Int,
    val bundleCode:Int,
    val groupCode:Int,
    val name:String,
){

}
class CalcInfo(
    val bundleGroups : List<BundleGroup>
)

data class RealtyLoanAndAutoTariff(
    val hasService:Boolean,
    val items:List<RealtyLoanAndAutoTariffItem>,
    val sum:BigDecimal,
){
    override fun toString(): String {
        return "($sum [${items.joinToString(", ")}] $hasService)"
    }
}

data class RealtyLoanAndAutoTariffItem(
    val bundleCode:Int,
    val groupCode:Int,
    val cost:BigDecimal,
    val name:String,
    val isIncluded:Boolean,

){
    override fun toString(): String {
        return "{${name}=$cost $isIncluded}"
    }
}

private fun createTariffsWithBundlesBinary(
    calcInfo: CalcInfo,
    buyRateEnabled: Boolean,
): List<RealtyLoanAndAutoTariff> {
    val bundles = calcInfo.bundleGroups

    val bundleVariantCount = 2f.pow(bundles.size).toInt() - 1
    var hasService = buyRateEnabled

    val tariffs = mutableListOf<RealtyLoanAndAutoTariff>()

    repeat(1) {
        (bundleVariantCount downTo 0).forEach { bundleVariant ->
            var boxCost = 0
            val size = bundles.size - 1
            val tariffsItems = bundles.mapIndexed{ index, bundle  ->
                val isIncluded = (((1 shl (size - index)) and bundleVariant) > 0)
                if (isIncluded) 1 else 0
//                boxCost += if (isIncluded) bundle.cost else 0
//                RealtyLoanAndAutoTariffItem(
//                    bundleCode = bundle.bundleCode,
//                    groupCode = bundle.groupCode,
//                    cost = bundle.cost.toBigDecimal(),
//                    name = bundle.name,
//                    isIncluded = isIncluded,
//                )
            }

            println(tariffsItems)
//            val tariffItemsCost = tariffsItems
//                .filter { item -> item.isIncluded }
//                .sumOf { item -> item.cost }
//
//            tariffs.add(
//                RealtyLoanAndAutoTariff(
//                    hasService = hasService,
//                    items = tariffsItems,
//                    sum = tariffItemsCost,
//                )
//            )
        }
        hasService = false
    }

    return tariffs
}


private fun createTariffsWithBundles(
    calcInfo: CalcInfo,
    buyRateEnabled: Boolean,
): List<RealtyLoanAndAutoTariff> {
    val bundles = calcInfo.bundleGroups

    val bundleVariantCount = 2f.pow(bundles.size).toInt() - 1
    var hasService = buyRateEnabled

    val tariffs = mutableListOf<RealtyLoanAndAutoTariff>()

    repeat(1) {
        (bundleVariantCount downTo 0).forEach {
            val binary = String.format("%${bundles.size}s", Integer.toBinaryString(it))
                .replace(" ", "0")
                .toCharArray()
                .toList()
            println(binary)
            var boxCost = 0
            val tariffsItems = bundles.zip(binary) { bundle, binaryDigit ->
                val isIncluded = binaryDigit == '1'
                boxCost += if (isIncluded) bundle.cost else 0
                RealtyLoanAndAutoTariffItem(
                    bundleCode = bundle.bundleCode,
                    groupCode = bundle.groupCode,
                    cost = bundle.cost.toBigDecimal(),
                    name = bundle.name,
                    isIncluded = isIncluded,
                )
            }

            val tariffItemsCost = tariffsItems
                .filter { item -> item.isIncluded }
                .sumOf { item -> item.cost }

            tariffs.add(
                RealtyLoanAndAutoTariff(
                    hasService = hasService,
                    items = tariffsItems,
                    sum = tariffItemsCost,
                )
            )
        }
        hasService = false
    }

    return tariffs
}




fun checkA(a:Int){
    if (a == 0){
        print("a")
    } else {
        print ("b")
    }
    val bundles= listOf(4,6,7)
    val bundleVariant = 65
    println(String.format("%${bundles.size}s",  Integer.toBinaryString(bundleVariant)))
    println(bundles.mapIndexed{ index, bundle  ->
        if ((index and bundleVariant) > 0) bundle else 0
    })
    println( Integer.toBinaryString(bundleVariant))
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

