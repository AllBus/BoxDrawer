package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.figure.complex.FigureCubik
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import kotlin.math.abs

class SplashCube : ISplashDetail {
    override val names: List<String>
        get() = listOf("cube")

    override fun help(): HelpData {
        return HelpData(
            "cube size (counts *) (zigzag) corner (u ux uy ) (zigf zigd)",
            "Draw a cube with the given size.",
            params = listOf(
                HelpDataParam("size", "The size of the cube.", FIELD_1),
            ),
            creator = TPArg.create(
                "cube",
                TPArg("size", FIELD_1),
                TPArg.block(
                TPArg.oneOrMore("counts", TPArg("c", FIELD_1)),
                ),
                TPArg.block(
                    TPArg.noneOrLine(
                        TPArg("zd", FIELD_1),
                        TPArg("zw", FIELD_1),
                        TPArg("zh", FIELD_1),
                        //   TPArg.figure("zig"),
                    )
                ),
                TPArg("corner", FIELD_1),
                TPArg.block(
                    TPArg.noneOrLine(
                        TPArg("u", FIELD_CHECK),
                        TPArg("ux", FIELD_CHECK),
                        TPArg("uy", FIELD_CHECK),
                    )
                ),
                TPArg.block(
                    TPArg.noneOrLine(
                        TPArg("zigf", FIELD_INT),
                        TPArg("zigd", FIELD_INT),
                    )
                )
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
        val count = com.takeBlock(2)?.map { v -> memory.value(v, 1.0).toInt() } ?: emptyList()
        val zigInfo = BlockTortoiseReader.readZigInfo(com.takeBlock(3), memory, figureExtractor.ds)
        val cornerRadius = abs(com[4, 0.0, memory])
        val properties = com.takeBlock(5)
        val distance = com.takeBlock(6)
        val hasDrop = figureExtractor.valueAt(properties, 0, 1.0) > 0
        val reverseX = figureExtractor.valueAt(properties, 1, 1.0) > 0
        val reverseY = figureExtractor.valueAt(properties, 2, 1.0) > 0

        val zigFirst = figureExtractor.valueAt(distance, 0, 0.0).toInt()
        val zigDistance = figureExtractor.valueAt(distance, 1, 0.0).toInt()

        builder.addProduct(
            FigureCubik(
                size = size,
                sides = count,
                zigInfo = zigInfo,
                cornerRadius = cornerRadius,
                enableDrop = hasDrop,
                reverseX = reverseX,
                reverseY = reverseY,
                zigFirstIndex = zigFirst,
                zigDistance = zigDistance
            )
        )

    }
}