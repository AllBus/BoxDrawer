package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.detal.soft.SoftRez
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashSoftRez: ISplashDetail {
    override val names: List<String>
        get() = listOf("rez")

    override fun help(): HelpData =  HelpData(
        "rez width height delta dlina soedinenie isFirstSmall",
        "Нарисовать мягкий рез",
        listOf(
            HelpDataParam(
                "width",
                "", FIELD_2
            ),
            HelpDataParam(
                "height",
                "", FIELD_NONE
            ),
            HelpDataParam(
                "delta",
                "",
            ),
            HelpDataParam(
                "dlina",
                ""
            ),
            HelpDataParam(
                "soedinenie",
                ""
            ),
            HelpDataParam(
                "isFirstSmall",
                "", FIELD_CHECK
            ),
        ),
        creator = TPArg.create("rez",
            TPArg("wh", FIELD_2),
            TPArg("delta", FIELD_1),
            TPArg("dlina", FIELD_1),
            TPArg("soedinenie", FIELD_1),
            TPArg("isFirstSmall", FIELD_CHECK),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val width = com[1, memory]
        val height = com[2, memory]
        val delta = com[3, 5.2, memory]
        val dlina = com[4, 18.0, memory]
        val soedinenie = com[5, 6.0, memory]
        val firstSmall = com[6, memory]

        val figures = SoftRez().drawRez(
            width,
            height,
            delta,
            dlina,
            soedinenie,
            firstSmall > 0
        )
        builder.addProduct(figures)
    }
}