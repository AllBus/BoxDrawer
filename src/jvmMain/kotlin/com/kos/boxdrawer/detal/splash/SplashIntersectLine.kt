package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_SELECTOR
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
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
                "v|h",
                "lv до вертикальной или lh до горизонтальной линии", FIELD_SELECTOR,
                variants = listOf("lv", "lh")
            ),
            HelpDataParam(
                "x",
                "Координата линии",
            ),
            HelpDataParam(
                "y",
                "Координата линии y",
            ),
            HelpDataParam(
                "a",
                "Угол наклона линии", FIELD_ANGLE
            ),
        ),
        creator = TPArg.createWithoutName(
            TPArg.selector("v|h", listOf("lv", "lh")),
            TPArg("x", FIELD_1),
            TPArg.noneOrLine(
                TPArg("y", FIELD_1),
                TPArg("a",FIELD_ANGLE),
            )
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