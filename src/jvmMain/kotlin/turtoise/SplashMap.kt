package turtoise

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.detal.splash.SplashArc
import com.kos.boxdrawer.detal.splash.SplashDiamond
import com.kos.boxdrawer.detal.splash.SplashDiamondAngle
import com.kos.boxdrawer.detal.splash.SplashDrop
import com.kos.boxdrawer.detal.splash.SplashEdit
import com.kos.boxdrawer.detal.splash.SplashLine
import com.kos.boxdrawer.detal.splash.SplashObruch
import com.kos.boxdrawer.detal.splash.SplashParallelogram
import com.kos.boxdrawer.detal.splash.SplashPaz
import com.kos.boxdrawer.detal.splash.SplashPrintCoordinate
import com.kos.boxdrawer.detal.splash.SplashRoad
import com.kos.boxdrawer.detal.splash.SplashRoundLine
import com.kos.boxdrawer.detal.splash.SplashSoftRez
import com.kos.boxdrawer.detal.splash.SplashStena
import com.kos.boxdrawer.detal.splash.SplashTake
import com.kos.boxdrawer.detal.splash.SplashText
import com.kos.boxdrawer.detal.splash.SplashTooth
import com.kos.boxdrawer.detal.splash.SplashToothReverse

object SplashMap {

    val splashList = listOf(
        SplashArc(),
        SplashTooth(),
        SplashToothReverse(),
        SplashRoundLine(),
        SplashLine(),
        SplashSoftRez(),
        SplashPaz(),
        SplashStena(),
        SplashObruch(),
        SplashEdit(),
        SplashDrop(),
        SplashTake(),
        SplashText(),
        SplashPrintCoordinate(),
        SplashDiamond(),
        SplashDiamondAngle(),
        SplashParallelogram(),
        SplashRoad()
    )
    val splashes: Map<String, ISplashDetail> = splashList.flatMap { s -> s.names.map { it to s } }.toMap()
}