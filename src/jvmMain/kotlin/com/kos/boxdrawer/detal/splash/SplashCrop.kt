package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_SELECTOR
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.ICropable
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashCrop : ISplashDetail {
    override val names: List<String>
        get() = listOf("crop")

    override fun help(): HelpData = HelpData(
        argument = "crop (figure) (n v +)",
        description = "Отсечение о фигуры части по горизотали или вертикали",
        params = listOf(
            HelpDataParam("figure", "фигура", FIELD_FIGURE),
            HelpDataParam("n", "ltrb", FIELD_SELECTOR),
            HelpDataParam("v", "значение", FIELD_1),
        ),
        creator = TPArg.create(
            "crop",
            TPArg.figure("figure"),
            TPArg.block(
                TPArg.oneOrMore(
                    "mc",
                    TPArg.selector("n", listOf("l", "t", "r", "b")),
                    TPArg("v", FIELD_1)
                )
            )
        )

    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {

        figureExtractor.figure(com.takeBlock(1))?.let { f ->
            if (f is ICropable) {
                var ff: ICropable = f
                com.takeBlock(2)?.let { par ->
                    val si = par.size
                    for (i in 0 until si - 1 step 2) {
                        val napr = par.inner[i].innerLine
                        val size = figureExtractor.valueAt(par, i + 1, 0.0)
                        val cropSide = when (napr) {
                            "l" -> CropSide.LEFT
                            "r" -> CropSide.RIGHT
                            "t" -> CropSide.TOP
                            "b" -> CropSide.BOTTOM
                            else -> CropSide.LEFT
                        }
                        ff = ff.crop(size, cropSide)
                    }
                }
                builder.add(ff)
            } else
                builder.add(f)
        }
    }
}