package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.collections.FigureList
import dagger.multibindings.IntoSet
import turtoise.FigureCreator.arcInTwoPoint
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.memory.keys.MemoryKey
import vectors.Vec2

class SplashArc : ISplashDetail {
    override val names: List<String>
        get() = listOf("arc")

    override fun help(): HelpData {
        return HelpData(
            "arc (radius pointStart pointEnd)+",
            "Нарисовать дугу заданного радиуса radius через две точки",
            listOf(
                HelpDataParam(
                    "radius",
                    "Радиус"
                ),
                HelpDataParam(
                    "pointStart",
                    "Точка начала"
                ),
                HelpDataParam(
                    "pointEnd",
                    "Точка конца"
                ),
            )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        /* (radius pointStart pointEnd)+ */
        val memory = figureExtractor.memory
        val cs = com.size
        val figures = FigureList(
            (1 until cs).map { i -> com.takeBlock(i) }.map { block ->
                val r = memory.value(block?.get(0) ?: MemoryKey.ZERO, 0.0)
                val p = Vec2(memory.value(block?.get(1) ?: MemoryKey.ZERO, 0.0), 0.0)
                val z = Vec2(memory.value(block?.get(2) ?: MemoryKey.ZERO, 0.0), 0.0)
                arcInTwoPoint(p, z, r)
            }
        )
        builder.addProduct(figures)
    }
}