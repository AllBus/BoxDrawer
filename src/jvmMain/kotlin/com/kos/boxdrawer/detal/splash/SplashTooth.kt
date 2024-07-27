package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.memory.TortoiseMemory

open class SplashTooth : ISplashDetail {
    override val names: List<String>
        get() = listOf("tooth")

    override fun help(): HelpData = HelpData(
        "tooth h w ",
        "Нарисовать зуб",
        listOf(
            HelpDataParam(
                "h",
                "",

                ),
            HelpDataParam(
                "w",
                "",
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        builder.startPoint()
        if (builder.state.zigParam.reverse) {
            toothreverse(builder, com, memory)
        } else {
            tooth(builder, com, memory)
        }
    }

    protected fun toothreverse(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        for (i in 1 until com.size step 2) {
            val a = com[i, memory]
            val b = com[i + 1, memory]
            builder.state.move(0.0, -b)
            builder.addPoint()
            builder.state.move(a, b)
            builder.addPoint()
        }
    }

    protected fun tooth(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        for (i in 1 until com.size step 2) {
            val a = com[i, memory]
            val b = com[i + 1, memory]
            builder.state.move(a, -b)
            builder.addPoint()
            builder.state.move(0.0, b)
            builder.addPoint()
        }
    }
}

class SplashToothReverse : SplashTooth() {
    override val names: List<String>
        get() = listOf("toothr")

    override fun help(): HelpData = HelpData(
        "toothr h w",
        "Нарисовать зуб в обратную сторону",
        listOf(
            HelpDataParam(
                "h",
                "",

                ),
            HelpDataParam(
                "w",
                "",

                ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        builder.startPoint()
        if (builder.state.zigParam.reverse) {
            toothreverse(builder, com, memory)
        } else {
            tooth(builder, com, memory)
        }
    }
}