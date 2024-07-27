package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.figure.FigurePolyline
import com.kos.figure.composition.FigureTranslate
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.ZigzagInfo
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.road.RoadCad
import turtoise.road.RoadProperties
import vectors.Vec2

class SplashRoad() : ISplashDetail {
    override val names: List<String>
        get() = listOf("road")

    override fun help(): HelpData = HelpData(
        "road (figure) (w h) (zw zd zh) (ve) (ds)",
        "Построить дорогу",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "w",
                ""
            ),
            HelpDataParam(
                "h",
                ""
            ),
            HelpDataParam(
                "zw",
                ""
            ),
            HelpDataParam(
                "zd",
                ""
            ),
            HelpDataParam(
                "zh",
                ""
            ),
            HelpDataParam(
                "ve",
                ""
            ),
            HelpDataParam(
                "ds",
                ""
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                f.list()
            }?.fold(Vec2.Zero) { acc, f ->
                if (f is FigurePolyline) {
                    val a = com.takeBlock(2)
                    val zig = com.takeBlock(3)
                    val st = com.takeBlock(4)
                    val dcc = com.takeBlock(5)
                    val ds = BlockTortoiseReader.readDrawerSettings(
                        dcc,
                        figureExtractor.memory,
                        figureExtractor.ds
                    )
                    val stl = st?.innerLine.orEmpty()+"   "

                    builder.addProduct(
                        FigureTranslate(
                            RoadCad.build(
                                line = f,
                                rp = RoadProperties(
                                    width = figureExtractor.valueAt(a, 0, 10.0),
                                    startHeight = figureExtractor.valueAt(a, 1, 10.0),
                                    count = 2, //com.take(4, 2.0, figureExtractor.memory).toInt(),
                                    outStyle = BoxAlgorithm.parseOutVariant(
                                        stl.substring(0,1)
                                    ),
                                    zigzagInfo = ZigzagInfo(
                                        width = figureExtractor.valueAt(zig, 0, 15.0),
                                        delta = figureExtractor.valueAt(zig, 1, 35.0),
                                        height = figureExtractor.valueAt(
                                            zig,
                                            2,
                                            ds.boardWeight,
                                        ),
                                        fromCorner = true,
                                    ),
                                    connectStyle = BoxAlgorithm.parseConnectVariant(
                                        stl.substring(1,2)
                                    ),
                                    isHoleLine = stl.substring(2,3) in "ho"
                                ),
                                ds = ds
                            ), acc
                        ),
                    )
                    acc + Vec2(f.rect().width, 0.0)
                } else
                    acc

            }
        }
    }
}