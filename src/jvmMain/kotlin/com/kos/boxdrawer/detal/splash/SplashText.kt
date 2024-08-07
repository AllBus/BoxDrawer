package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import com.kos.figure.FigureText
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import java.text.DecimalFormat

open class SplashText: ISplashDetail {
    protected var printFormat = DecimalFormat("0.####")

    override val names: List<String>
        get() = listOf("print")

    override fun help(): HelpData =    HelpData(
        "print [text] | (variable)",
        "Написать текст или значение переменной",
        listOf(
            HelpDataParam(
                "text",
                "Выводимый текст",
                FIELD_TEXT
            ),
        ),
        creator = TPArg.create("print",
            TPArg.multiVariant("var",
                TPArg("text",FIELD_TEXT)
            )
        )
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