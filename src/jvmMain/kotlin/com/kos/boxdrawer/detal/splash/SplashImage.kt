package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField
import com.kos.figure.complex.FigureImage
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.parser.TPArg
import vectors.Vec2

class SplashImage: ISplashDetail {
    override val names: List<String>
        get() = listOf("image")

    override fun help(): HelpData = HelpData(
        "(file) sx sy",
        "Загрузить изображение",
        creator = TPArg.create("image",
            TPArg.block(
                TPArg("file", TemplateField.FIELD_FILE)
            ),
            TPArg("size", TemplateField.FIELD_2),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val uri = com.takeBlock(1)?.innerLine.orEmpty()
        val sx = com[2, 100.0, figureExtractor.memory]
        val sy = com[3, 100.0, figureExtractor.memory]

        if (uri.isNotEmpty()){
            builder.addProduct(FigureImage(Vec2.Zero, uri, Vec2(sx, sy)))
        }
    }
}