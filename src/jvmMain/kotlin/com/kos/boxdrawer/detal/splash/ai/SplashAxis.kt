package com.kos.boxdrawer.detal.splash.ai

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.FigureBezier
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor

class SplashAxis : ISplashDetail {
    override val names: List<String>
        get() = listOf("axis")

    override fun help(): HelpData {
        return HelpData(
            "axis size",
            "Построить оси координат",
            params = listOf(
                HelpDataParam("size", "Размер осей", FIELD_1)
            ),
            creator = TPArg.create("axis", TPArg("size", FIELD_1))
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val center = builder.xy
        val size = com[1,100.0, memory] // Read size from command

        val arrowSize = size * 0.05 // Adjust arrow size as needed

        val figures = mutableListOf<IFigure>() // Create a FigureList to store figures

        // Draw the x-axis
        val xAxisStart = center - Vec2(size, 0.0)
        val xAxisEnd = center + Vec2(size, 0.0)
        figures.add(FigureLine(xAxisStart, xAxisEnd))// Draw x-axis arrowhead
        val xArrowP1 = xAxisEnd - Vec2(arrowSize, arrowSize)
        val xArrowP2 = xAxisEnd - Vec2(arrowSize, -arrowSize)
        figures.add(FigureLine(xAxisEnd, xArrowP1))
        figures.add(FigureLine(xAxisEnd, xArrowP2))

        // Draw the y-axis
        val yAxisStart = center - Vec2(0.0, size)
        val yAxisEnd = center + Vec2(0.0, size)
        figures.add(FigureLine(yAxisStart, yAxisEnd))

        // Draw y-axis arrowhead
        val yArrowP1 = yAxisEnd - Vec2(arrowSize, arrowSize)
        val yArrowP2 = yAxisEnd + Vec2(arrowSize, -arrowSize)
        figures.add(FigureLine(yAxisEnd, yArrowP1))
        figures.add(FigureLine(yAxisEnd, yArrowP2))

        builder.addProduct(FigureList(figures)) // Add the FigureList to the builder
    }
}