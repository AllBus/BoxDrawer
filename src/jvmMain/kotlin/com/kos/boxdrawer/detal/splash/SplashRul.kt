package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.parser.TPArg
import vectors.Vec2

class SplashRul: ISplashDetail  {
    override val names: List<String>
        get() = listOf("rul")

    override fun help(): HelpData = HelpData(
        "rul ((w h)) ((w h) (tw th td) (bw bh bd)) ((w h) (tw th) (bw bh))",
        "Рисование руля",
        listOf(),
        creator = TPArg.create("rul", TPArg.oneOrMore("1",
            TPArg.block(
                TPArg.noneOrLine(
                    TPArg("wh", FIELD_2),
                    TPArg("d", FIELD_1)
                )
            ))
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val cs = com.size
        var offset = Vec2.Zero

        val res = mutableListOf<IFigure>()
        for (i in 1 until cs) {
            val c = com.takeBlock(i)

            val w = figureExtractor.valueAt(c , 0,0.0)
            val h = figureExtractor.valueAt(c , 1,0.0)

            offset+=Vec2(w/2, 0.0)

            val rect = FigureCreator.rectangle(Vec2.Zero+offset, w, h)
            val tblock = c?.inner?.getOrNull(2)?.let { tb ->
                val tw = figureExtractor.valueAt(tb , 0,0.0)
                val th = figureExtractor.valueAt(tb , 1,0.0)
                val td = figureExtractor.valueAt(tb , 2,0.0)
                val tl = figureExtractor.valueAt(tb , 2,10.0)
                listOf(
                    FigureCreator.rectangle(Vec2(0.0, -h/2-th/2)+offset, tw, th),
                FigurePolyline(listOfNotNull(
                    Vec2(-w/2, -h/2),
                    Vec2(0.0-tw/2 - td - tl, -h/2),
                    Vec2(0.0-tw/2 - td, -h/2),
                    Vec2(0.0-tw/2, -h/2-th)
                ).map { it+offset })
                ).toFigure()
            }
            val bblock = c?.inner?.getOrNull(3)?.let { tb ->

                val tw = figureExtractor.valueAt(tb , 0,0.0)
                val th = figureExtractor.valueAt(tb , 1,0.0)
                val td = figureExtractor.valueAt(tb , 2,0.0)
                val tl = figureExtractor.valueAt(tb , 2,0.0)
                listOf(
                    FigureCreator.rectangle(Vec2(0.0, +h/2+th/2)+offset, tw, th),
                    FigurePolyline(
                        listOfNotNull(
                        Vec2(-w/2, h/2),
                        Vec2(0.0-tw/2 - td - tl, h/2),
                        Vec2(0.0-tw/2 - td, h/2),
                        Vec2(0.0-tw/2, h/2+th)
                    ).map { it+offset }
                    )
                ).toFigure()
            }
            offset+=Vec2(w/2+10, 0.0)

            res+=rect
            tblock?.let {
                res+=it
            }

            bblock?.let {
                res+=it
            }
        }
        builder.addProduct(res.toFigure())
    }
}