package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.figure.complex.FigureCubik
import com.kos.figure.complex.FigureDirCubik
import com.kos.figure.complex.model.CubikDirection
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import kotlin.math.abs

class SplashCube2 : ISplashDetail {
    override val names: List<String>
        get() = listOf("cube2")

    override fun help(): HelpData {
        return HelpData(
            "cube2 size ([counts] *) (zigzag) corner (u )",
            "Draw a cube with the given size.",
            params = listOf(
                HelpDataParam("size", "The size of the cube.", FIELD_1),
            ),
            creator = TPArg.create(
                "cube",
                TPArg("size", FIELD_1),
                TPArg.block(
                    TPArg.oneOrMore("counts",
                        TPArg.block(
                            TPArg("c", FIELD_1),
                            TPArg("d", FIELD_1),
                            TPArg("i", FIELD_CHECK),
                            TPArg("r", FIELD_1),
                        )
                    )
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
                    )
                ),
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
        val count = com.takeBlock(2)?.let { vv ->
            vv.inner.map { v ->
                val rev = figureExtractor.valueAt(v, 2, 0.0).toInt()
                CubikDirection(
                    count = figureExtractor.valueAt(v, 0, 1.0).toInt(),
                    direction = figureExtractor.valueAt(v, 1, 0.0).toInt(),
                    isInnerCorner = figureExtractor.valueAt(v, 3, 0.0) > 0,
                    isFlat = rev == 0,
                    isReverse = rev < 0
                )
            }
        } ?: emptyList()
        val zigInfo = BlockTortoiseReader.readZigInfo(com.takeBlock(3), memory, figureExtractor.ds)
        val cornerRadius = abs(com[4, 0.0, memory])
        val properties = com.takeBlock(5)
        val hasDrop = figureExtractor.valueAt(properties, 0, 1.0) > 0


        builder.addProduct(
            FigureDirCubik(
                size = size,
                sides = count,
                zigInfo = zigInfo,
                cornerRadius = cornerRadius,
                enableDrop = hasDrop,
            )
        )

    }
}