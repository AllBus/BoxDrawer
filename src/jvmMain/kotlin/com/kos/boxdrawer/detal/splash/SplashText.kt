package com.kos.boxdrawer.detal.splash

import com.kos.figure.FigureText
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.parser.TortoiseParserStackBlock
import java.text.DecimalFormat

open class SplashText: ISplashDetail {
    protected var printFormat = DecimalFormat("0.####")

    override val names: List<String>
        get() = listOf("print")

    override fun help(): HelpData =    HelpData(
        "print [text] | (variable)",
        "Написать текст или значение переменной"
    )
    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        /* [text] | (variable) */
        val text = (1 until com.size).mapNotNull { i -> com.takeBlock(i) }.map { block ->
            if (block is TortoiseParserStackBlock && block.skobka != '(') {
                block.innerLine
            } else {
                printFormat.format(figureExtractor.memory.value(block.argument, 0.0))
            }
        }.joinToString(" ")
        builder.addProduct(FigureText(text))
    }
}