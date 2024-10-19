package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField
import com.kos.figure.complex.FigureGrid
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashGrid : ISplashDetail {
    override val names: List<String>
        get() = listOf("grid")

    override fun help(): HelpData = HelpData(
        argument = "grid column row cw ch",
        description = "Нарисовать сетку",
        params = listOf(
            HelpDataParam("column", "Количество столбцов"),
            HelpDataParam("row", "Количество строк"),
            HelpDataParam("cw", "Ширина ячейки"),
            HelpDataParam("ch", "Высота ячейки"),
        ),
        creator = TPArg.create(
            "grid",
            TPArg("column", TemplateField.FIELD_INT),
            TPArg("row", TemplateField.FIELD_INT),
            TPArg("wh", TemplateField.FIELD_2),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        builder.addProduct(FigureGrid(
            columnCount = com[1, 1.0, figureExtractor.memory].toInt(),
            rowCount = com[2, 1.0, figureExtractor.memory].toInt(),
            cellWidth = com[3, 10.0, figureExtractor.memory],
            cellHeight = com[4, 10.0, figureExtractor.memory],
        ))
    }
}