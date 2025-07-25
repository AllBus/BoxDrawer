package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.complex.FigureBone
import com.kos.figure.complex.model.BoneAnchor
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import turtoise.parser.TPArg.invoke
import vectors.Vec2

class SplashBone: ISplashDetail {
    override val names: List<String>
        get() = listOf("bone")

    override fun help()= HelpData(
        argument = "name (figure) (dx dy)",
        description = "Draw bone",
        params = listOf(
            HelpDataParam("name", "Имя"),
            HelpDataParam("figure", "Фигура"),
            HelpDataParam("dx", "x"),
            HelpDataParam("dy", "y"),
        ),
        creator = TPArg.create("bone",
            TPArg.text("name"),
            TPArg.figure("figure"),
            TPArg.block(
                TPArg.multi("d",
                   TPArg("xy", FIELD_2)
                )
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
//        val memory = figureExtractor.memory
        val name = com.takeBlock(1)?.innerLine.orEmpty()
        com.takeBlock(2)?.let { block ->
            figureExtractor.figure(block)?.let { figure ->
                val coordinates = com.takeBlock(3)?.let { dot ->
                    dot.inner.map {  block ->
                        val i = 0
                        val x = figureExtractor.valueAt(block, i, 0.0)
                        val y = figureExtractor.valueAt(block, i+1, 0.0)
                        val n = block.get(i+2)?.name.orEmpty()
                        BoneAnchor(n, Vec2(x, y))
                    }
                }?: emptyList()
                builder.addBone(
                    FigureBone(figure, name, coordinates)
                )
            }
        }
    }
}