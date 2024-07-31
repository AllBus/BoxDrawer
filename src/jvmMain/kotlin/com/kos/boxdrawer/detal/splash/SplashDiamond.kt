package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SplashDiamond : ISplashDetail {
    override val names: List<String>
        get() = listOf(
            "romb",
            "diamond"
        )

    override fun help(): HelpData = HelpData(
        "diamond dx dy",
        "Построить ромб по двум диагоналям",
                listOf(
                    HelpDataParam(
                        "dx",
                        "Диагональ по оси x", FIELD_2
                    ),
                    HelpDataParam(
                        "dy",
                        "Диагональ по оси y", FIELD_NONE
                    ),
                )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val diagonalShort = com.take(1, 0.0, figureExtractor.memory)
        val diagonalLong = com.take(2, diagonalShort, figureExtractor.memory)

        val apoint = listOf<Vec2>(
            Vec2(0.0, diagonalLong / 2),
            Vec2(-diagonalShort / 2, 0.0),
            Vec2(0.0, -diagonalLong / 2),
            Vec2(diagonalShort / 2, 0.0),
        )
        builder.addPolyline(apoint, true)
    }

}

class SplashDiamondAngle : ISplashDetail {
    override val names: List<String>
        get() = listOf(
            "romba",
            "diamonda",
            "diamond_angle",
            "diamondAngle"
        )

    override fun help(): HelpData = HelpData(
        "diamond_angle d a",
        "Построить ромб по стороне и углу",
        listOf(
            HelpDataParam(
                "d",
                "Сторона", FIELD_1
            ),
            HelpDataParam(
                "a",
                "Угол", FIELD_ANGLE
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val d = com.take(1, 0.0, figureExtractor.memory)
        val alpha = com.take(2, 90.0, figureExtractor.memory)
        val diagonalLong = (2 * d * sin(alpha * PI / 180 / 2))
        val diagonalShort = (2 * d * cos(alpha * PI / 180 / 2))
        val apoint = listOf<Vec2>(
            Vec2(0.0, diagonalLong / 2),
            Vec2(-diagonalShort / 2, 0.0),
            Vec2(0.0, -diagonalLong / 2),
            Vec2(diagonalShort / 2, 0.0),
        )
        builder.addPolyline(apoint, true)
    }
}

class SplashParallelogram : ISplashDetail {
    override val names: List<String>
        get() = listOf(
            "par",
            "parallelogram",
            "parallelogramm"
        )

    override fun help(): HelpData = HelpData(
        "par d a b",
        "Построить параллелограмм по двум сторонам углу между ними",
        listOf(
            HelpDataParam(
                "d",
                "Сторона", FIELD_1
            ),
            HelpDataParam(
                "a",
                "Угол", FIELD_ANGLE
            ),
            HelpDataParam(
                "b",
                "Сторона", FIELD_1
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val storona = com.take(1, 0.0, memory)
        val povorot = com.take(2, 45.0, memory)
        val storonaDlinnaya = com.take(3, storona, memory)
        val h = -storona * sin(povorot * PI / 180)
        val d = storona * cos(povorot * PI / 180)
        val apoint = listOf<Vec2>(
            Vec2(storonaDlinnaya, 0.0),
            Vec2(0.0, 0.0),
            Vec2(d, h),
            Vec2(storonaDlinnaya + d, h),
        )
        builder.addPolyline(apoint, true)
    }
}