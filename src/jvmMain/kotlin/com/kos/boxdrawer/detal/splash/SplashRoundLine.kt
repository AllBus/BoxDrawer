package com.kos.boxdrawer.detal.splash

import com.kos.figure.FigureCircle
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.memory.TortoiseMemory
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.sign

class SplashRoundLine: ISplashDetail {
    override val names: List<String>
        get() = listOf(
            "rline",
            "roundline"
        )

    override fun help(): HelpData = HelpData(
        "rline r x y",
        "Нарисовать скругление радиуса r текущей линии к точке x y ",
        listOf(
            HelpDataParam(
                "r",
                ""
            ),
            HelpDataParam(
                "x",
                ""
            ),
            HelpDataParam(
                "y",
                ""
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        //-
        //Todo: Не все случаи Не зависит от поворота
        /* r ax ay */
        builder.saveLine()
        val (rest, aap, aaa) = roundLine(com, figureExtractor.memory)

        builder.addProduct(FigureList(rest))
        builder.state.move(aap.x, aap.y)
        builder.state.angleInDegrees = aaa
    }

    private fun roundLine(
        com: TortoiseCommand,
        memory: TortoiseMemory
    ): Triple<List<IFigure>, Vec2, Double> {
        /* r ax ay */
        val cr = Vec2.Zero
        val rest = mutableListOf<IFigure>()

        val i = 1
        val r = com[i, memory]
        val ax = com[i + 1, memory]
        val ay = com[i + 2, memory]
        val ll = if (com.size > 4) com[i + 3, memory] else null
        val aa = Vec2(ax, ay)

        val (aap, angle) = if (r > 0) {
            val preda = sign(ay) * PI/2
            val cc = Vec2(0.0, sign(ay) * r)

            val al = (aa - cc).angle
            val hl = Vec2.distance(cc, aa)
            val rv = asin(r / hl) * sign(ay)
            val alp = al + rv
            val p = cc + Vec2(0.0, r).rotate((if (ay > 0) PI + alp else alp))

            //   println (" ${ rv *180/ PI} : ${al*PI/180} : ${p} ${hl} ${r}")
            //rest += FigureLine(p + cr, cc + cr)

            val ap = if (ll == null) {
                aa
            } else {
                p + Vec2.normalize(p, aa) * ll
            }
            if (ap != p) {
                rest += FigureLine(p + cr, cr + ap)
            }

            rest += FigureCircle(cr + cc, r, true,  preda, -alp)
            Pair(ap , (ap-p).angle*180/ PI)
        } else {
            val ap = if (ll == null) {
                aa
            } else {
                Vec2.normalize(Vec2.Zero, aa) * ll
            }

            if (ap != Vec2.Zero) {
                rest += FigureLine(cr, ap + cr)
            }
            Pair(ap , ap.angle*180/ PI)

        }
        return Triple(rest, aap, angle)
    }
}

