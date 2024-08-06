package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashLine: ISplashDetail {
    override val names: List<String>
        get() = listOf("line")

    override fun help(): HelpData =  HelpData(
        "line (x y)+",
        "Нарисовать линию задавая сдвиг относительно предудыщей точки",
        listOf(

            HelpDataParam(
                "x",
                "Координата угла линии", FIELD_2
            ),
            HelpDataParam(
                "y",
                "Координата угла линии", FIELD_NONE
            ),
        ),
        creator = TPArg.create("line",
            TPArg.oneOrMore("1",
                TPArg("xy",FIELD_2)
            ))
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        builder.startPoint()
        for (i in 1 until com.size step 2) {
            val a = com[i, figureExtractor.memory]
            val b = com[i + 1, figureExtractor.memory]
            builder.state.move(a, -b)
            builder.add(builder.xy)
        }
    }
}

