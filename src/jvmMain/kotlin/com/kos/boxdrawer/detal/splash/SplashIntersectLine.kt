package com.kos.boxdrawer.detal.splash

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jsevy.jdxf.DXFColor
import com.kos.figure.FigureLine
import com.kos.figure.composition.FigureColor
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import vectors.Vec2
import kotlin.math.abs

class SplashIntersectLine : ISplashDetail {
    override val names: List<String>
        get() = listOf("lv", "lh", "l")

    override fun help(): HelpData = HelpData(
        "l[v|h] x y? a?",
        "Нарисовать прямую линию до пресечения с вериткальной или горизонтальной линией указанной координатой",
        listOf(
            HelpDataParam(
                "x",
                "Координата линии"
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val isVertical = com.takeBlock(0)?.innerLine?.endsWith("v") ?: false
        val cx = com[1, figureExtractor.memory]

        builder.startPoint()
        val a = builder.angle
        val sp = builder.xy

        val eps = 0.0001

        if (com.size <= 2) {
            if (isVertical) {
                if (abs(cx - sp.x) < eps) {
                    return
                } else {
                    Vec2.intersection(
                        sp,
                        sp + Vec2(1.0, 0.0).rotate(a),
                        Vec2(cx, -1000.0),
                        Vec2(cx, 1000.0)
                    )?.let { i ->
                        builder.state.moveTo(i)
                        builder.addPoint()
                    }
                }
            } else {
                if (abs(cx - sp.y) < eps) {
                    return
                } else {
                    Vec2.intersection(
                        sp,
                        sp + Vec2(1.0, 0.0).rotate(a),
                        Vec2(-1000.0, cx),
                        Vec2(1000.0, cx)
                    )?.let { i ->
                        builder.state.moveTo(i)
                        builder.addPoint()
                    }
                }
            }
        } else {
            val cy = com[2, figureExtractor.memory]
            val ca = Math.toRadians(com[3, figureExtractor.memory])

            val css = Vec2(cx, cy)
            Vec2.intersection(
                sp,
                sp + Vec2(1.0, 0.0).rotate(a),
                css,
                css + Vec2(1.0, 0.0).rotate(ca)
            )?.let { i ->
                builder.state.moveTo(i)
                builder.addPoint()
            }
//            builder.add(FigureColor(
//                Color.Red.toArgb(),
//                DXFColor.getClosestDXFColor(Color.Red.toArgb()), FigureLine(css, css + Vec2(100.0, 0.0).rotate(ca))))
        }
    }
}