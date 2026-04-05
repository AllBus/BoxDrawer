package com.kos.boxdrawer.detal.splash

import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import com.kos.figure.segments.model.BoneAnchor
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.parser.TPArg

class SplashMesh : ISplashDetail {
    override val names: List<String>
        get() = listOf("mesh")

    override fun help() = HelpData(
        argument = "mesh (a b)+",
        description = "Создать скелет",
        params = listOf(),
        creator = TPArg.create(
            "mesh",
            TPArg.multi(
                "bone",
                TPArg.block(
                    TPArg.text("abone"),
                    TPArg.text("bbone"),
                )
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val list = com.takeBlock(1)
        list?.blocks?.forEach { block ->
            val leftA = block.get(0)?.name.orEmpty().split(".")
            val rightA = block.get(1)?.name.orEmpty().split(".")
            val angle = figureExtractor.valueAt(block, 2)
            //println("Mesh ${leftA} ${rightA} ${builder.bones.keys}")
            if (leftA.size > 1 && rightA.size > 1) {
                val boneLeft = builder.boneAt(leftA[0])
                val boneRight = builder.boneAt(rightA[0])
                if (boneLeft != null && boneRight != null) {
                    val dl = boneLeft.dots.find { it.name == leftA[1] } ?: BoneAnchor.Empty
                    val dr = boneRight.dots.find { it.name == rightA[1] } ?: BoneAnchor.Empty
                    val move = dl.coordinate - dr.coordinate
                    val figures = mutableListOf<IFigure>(boneLeft)
                    figures += FigureTranslate(
                        FigureRotate(
                            boneRight,
                            angle,
                            dr.coordinate,
                        ),
                        //boneRight.figure,
                        move
                    )


                    builder.addProduct(figures.toFigure())
                }
            }
        }
    }
}