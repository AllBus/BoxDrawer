package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_3
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.collections.toFigure
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2

class SplashRul: ISplashDetail  {
    override val names: List<String>
        get() = listOf("rul")

    override fun help(): HelpData = HelpData(
        "rul (w h (tw th td tsl tsr tzd tzh) (bw bh bd bsl bsr bzd bzh))*",
        "Рисование руля",
        listOf(),
        creator = TPArg.create("rul", TPArg.oneOrMore("1",
            TPArg.block(
                TPArg.noneOrLine(
                    TPArg("wh", FIELD_2),
                    TPArg.block(
                        TPArg("twh", FIELD_2),
                        TPArg("td", FIELD_3),
                        TPArg("tz", FIELD_2),
                    ),
                    TPArg.block(
                        TPArg("bwh", FIELD_2),
                        TPArg("bd", FIELD_3),
                        TPArg("bz", FIELD_2),
                    )
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
                topBox(figureExtractor, tb, w, h, offset, 1.0)
            }
            val bblock = c?.inner?.getOrNull(3)?.let { tb ->
                topBox(figureExtractor, tb, w, h, offset, -1.0)
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

    private fun topBox(
        figureExtractor: TortoiseFigureExtractor,
        tb: TortoiseParserStackItem,
        w: Double,
        h: Double,
        offset: Vec2,
        multi:Double
    ): FigureList {
        val tw = figureExtractor.valueAt(tb, 0, 0.0)
        val th = figureExtractor.valueAt(tb, 1, 0.0)*multi
        val td = figureExtractor.valueAt(tb, 2, 0.0)
        val tsl = figureExtractor.valueAt(tb, 3, 0.0)
        val tsr = figureExtractor.valueAt(tb, 4, 0.0)
        val tzd = figureExtractor.valueAt(tb, 5, 0.0)
        val tzh = figureExtractor.valueAt(tb, 6, 0.0)

        val hasTd = tb.size >= 3

        val leftPos = if (hasTd) {
            -w / 2 + td
        } else -tw/2
        val topLine = (-h / 2)*multi


        return listOf(
            FigureCreator.rectangle(Vec2(leftPos+tw/2, topLine - th / 2) + offset, tw, th),
            FigurePolyline(listOfNotNull(
                Vec2(-w / 2, topLine),
                Vec2(leftPos + tsl, topLine),
                Vec2(leftPos, topLine - th),
                Vec2(leftPos + tw, topLine - th),
                Vec2(leftPos + tw - tsr, topLine),
                Vec2(w / 2 - tzd, topLine - tzh*multi).takeIf { tzh != 0.0 },
                Vec2(w / 2, topLine),
            ).map { it + offset })
        ).toFigure()
    }
}