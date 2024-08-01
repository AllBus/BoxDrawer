package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_SELECTOR
import com.kos.figure.FigurePolyline
import com.kos.figure.composition.FigureTranslate
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.ZigzagInfo
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TortoiseParserStackItem
import turtoise.road.RoadCad
import turtoise.road.RoadProperties
import turtoise.road.RoadUp
import turtoise.road.RoadUps
import vectors.Vec2

class SplashRoad() : ISplashDetail {
    override val names: List<String>
        get() = listOf("road")

    override fun help(): HelpData = HelpData(
        "road (figure) (w h s ((l * *) (r * *) (t * *))) (zd zw zh (zig) (hole)) (ve) (ds)",
        "Построить дорогу",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "w",
                "Ширина"
            ),
            HelpDataParam(
                "h",
                "Высота у начала"
            ),
            HelpDataParam(
                "s",
                "Стиль [s|a|p|d]",
                FIELD_SELECTOR
            ),
            HelpDataParam(
                "zw",
                "Длина зигзага"
            ),
            HelpDataParam(
                "zd",
                "Дельта зигзагов"
            ),
            HelpDataParam(
                "zh",
                "высота зигзага"
            ),
            HelpDataParam(
                "ve",
                "Стиль рисования [a|c|v][e|f|s|n][h|o]",
                FIELD_SELECTOR
            ),
            HelpDataParam(
                "ds",
                ""
            ),
            HelpDataParam(
                "zig",
                "Фигура зигзага",
                FIELD_FIGURE
            ),
        )
    )

    private fun readUp(block: TortoiseParserStackItem, figureExtractor: TortoiseFigureExtractor): RoadUp{
        return RoadUp(
                height = figureExtractor.valueAt(block, 1, 0.0),
                radius = figureExtractor.valueAt(block, 2, 0.0),
                figure = block.inner.getOrNull(3)
            )
    }

    private fun readUps(block: TortoiseParserStackItem, figureExtractor: TortoiseFigureExtractor): RoadUps{
        return RoadUps(
            left = block.getInnerAtName("l")?.let{readUp(it, figureExtractor)},
            right = block.getInnerAtName("r")?.let{readUp(it, figureExtractor)},
            top = block.getInnerAtName("t")?.let{readUp(it, figureExtractor)},
        )
   }

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
                    val style = a?.inner?.getOrNull(2)?.innerLine.orEmpty()+" "

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
                                    zigzagInfo = BlockTortoiseReader.readZigInfo(zig, figureExtractor.memory, ds),
                                    connectStyle = BoxAlgorithm.parseConnectVariant(
                                        stl.substring(1,2)
                                    ),
                                    isHoleLine = stl.substring(2,3) in "ho",
                                    style = BoxAlgorithm.parseRoadStyle(style.substring(0,1)),
                                    zigazagModel = zig?.inner?.getOrNull(3),
                                    holeModel = zig?.inner?.getOrNull(4),
                                    ups = a?.inner?.getOrNull(3)?.let { readUps(it, figureExtractor) }
                                ),
                                ds = ds,
                                figureExtractor = figureExtractor
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