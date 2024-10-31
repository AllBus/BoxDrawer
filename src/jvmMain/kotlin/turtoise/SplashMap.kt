package turtoise

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.detal.splash.SplashApproximation
import com.kos.boxdrawer.detal.splash.SplashApproximationPolar
import com.kos.boxdrawer.detal.splash.SplashApproximationUser
import com.kos.boxdrawer.detal.splash.SplashArc
import com.kos.boxdrawer.detal.splash.SplashCircle
import com.kos.boxdrawer.detal.splash.SplashCrop
import com.kos.boxdrawer.detal.splash.SplashDiamond
import com.kos.boxdrawer.detal.splash.SplashDiamondAngle
import com.kos.boxdrawer.detal.splash.ai.SplashDirigible
import com.kos.boxdrawer.detal.splash.SplashDrop
import com.kos.boxdrawer.detal.splash.SplashEdit
import com.kos.boxdrawer.detal.splash.SplashFigureAtPoints
import com.kos.boxdrawer.detal.splash.SplashIntersectFigures
import com.kos.boxdrawer.detal.splash.SplashIntersectLine
import com.kos.boxdrawer.detal.splash.SplashLine
import com.kos.boxdrawer.detal.splash.SplashNormal
import com.kos.boxdrawer.detal.splash.SplashObruch
import com.kos.boxdrawer.detal.splash.SplashParallelogram
import com.kos.boxdrawer.detal.splash.SplashPaz
import com.kos.boxdrawer.detal.splash.ai.SplashPetal
import com.kos.boxdrawer.detal.splash.SplashPoint
import com.kos.boxdrawer.detal.splash.SplashPrintCoordinate
import com.kos.boxdrawer.detal.splash.SplashRoad
import com.kos.boxdrawer.detal.splash.SplashRound
import com.kos.boxdrawer.detal.splash.SplashRoundLine
import com.kos.boxdrawer.detal.splash.SplashSimple
import com.kos.boxdrawer.detal.splash.SplashSoftRez
import com.kos.boxdrawer.detal.splash.SplashSpiral
import com.kos.boxdrawer.detal.splash.SplashStena
import com.kos.boxdrawer.detal.splash.SplashTake
import com.kos.boxdrawer.detal.splash.SplashText
import com.kos.boxdrawer.detal.splash.SplashTooth
import com.kos.boxdrawer.detal.splash.SplashToothReverse
import com.kos.boxdrawer.detal.splash.ai.SplashAxis
import com.kos.boxdrawer.detal.splash.ai.SplashChamomile
import com.kos.boxdrawer.detal.splash.SplashCube
import com.kos.boxdrawer.detal.splash.SplashCube2
import com.kos.boxdrawer.detal.splash.SplashGear
import com.kos.boxdrawer.detal.splash.SplashGrid
import com.kos.boxdrawer.detal.splash.SplashImage
import com.kos.boxdrawer.detal.splash.SplashStar
import com.kos.boxdrawer.detal.splash.SplashWave

object SplashMap {

    val splashList = listOf(
        SplashApproximation(),
        SplashApproximationPolar(),
        SplashApproximationUser(),
        SplashArc(),
        SplashCircle(),
        SplashCrop(),
        SplashDiamond(),
        SplashDiamondAngle(),
        SplashDrop(),
        SplashEdit(),
        SplashIntersectLine(),
        SplashLine(),
        SplashNormal(),
        SplashObruch(),
        SplashParallelogram(),
        SplashPaz(),
        SplashPrintCoordinate(),
        SplashRoad(),
        SplashRound(),
        SplashRoundLine(),
        SplashSoftRez(),
        SplashSpiral(),
        SplashStena(),
        SplashTake(),
        SplashText(),
        SplashTooth(),
        SplashToothReverse(),
        SplashPoint(),
        SplashFigureAtPoints(),
        SplashIntersectFigures(),
        // Todo:
        SplashChamomile(),
        // Todo:
        SplashPetal(),
        // Todo:
        SplashDirigible(),
        SplashAxis(),
        SplashCube(),
        SplashCube2(),
        SplashSimple(),
        SplashImage(),
        SplashGrid(),
        SplashStar(),
        SplashWave(),
        SplashGear(),
    )
    val splashes: Map<String, ISplashDetail> = splashList.flatMap { s -> s.names.map { it to s } }.toMap()
}