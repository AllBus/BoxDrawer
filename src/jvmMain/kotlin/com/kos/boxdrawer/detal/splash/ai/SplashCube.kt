package com.kos.boxdrawer.detal.splash.ai

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.figure.complex.FigureCubik
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.abs

class SplashCube : ISplashDetail {
    override val names: List<String>
        get() = listOf("cube")

    override fun help(): HelpData {
        return HelpData(
            "cube size",
            "Draw a cube with the given size.",
            params = listOf(
                HelpDataParam("size", "The size of the cube.", FIELD_1),
            ),
            creator = TPArg.create("cube",
                TPArg("size", FIELD_1),
            )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val size = com[1, memory]
        val countX = com[2, 1.0, memory].toInt()
        val countY = com[3, 1.0, memory].toInt()
        val zigInfo = BlockTortoiseReader.readZigInfo(com.takeBlock(4),memory, figureExtractor.ds)
        val cornerRadius = abs(com[5, 0.0, memory])
        val hasDrop = com[6, 1.0, memory] > 0.0
        val reverseX = com[7, 0.0, memory] > 0.0
        val reverseY = com[8, 1.0, memory] > 0.0

       builder.addProduct(FigureCubik(size, countX, countY, zigInfo, cornerRadius, hasDrop, reverseX, reverseY))

    }
}