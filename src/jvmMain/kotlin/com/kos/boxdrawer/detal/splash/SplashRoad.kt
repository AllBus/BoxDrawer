package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_SELECTOR
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import com.kos.figure.FigurePolyline
import com.kos.figure.composition.FigureTranslate
import turtoise.BlockTortoiseReader
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackItem
import turtoise.road.RoadCad
import turtoise.road.RoadHeight
import turtoise.road.RoadHeightList
import turtoise.road.RoadHeights
import turtoise.road.RoadProperties
import turtoise.road.RoadUp
import turtoise.road.RoadUps
import vectors.Vec2

class SplashRoad() : ISplashDetail {
    override val names: List<String>
        get() = listOf("road")

    override fun help(): HelpData = HelpData(
        "road (figure) (w h s ((l * *) (r * *) (t * *) (h (hl hr)+))) (zd zw zh (zig) (hole)) (ve) (ds)",
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
                FIELD_SELECTOR,
                listOf("s", "a", "p", "d", "n")
            ),
            HelpDataParam(
                "l",
                "Форма ботика слева", FIELD_2
            ),
            HelpDataParam(
                "r",
                "Форма бортика справа", FIELD_2
            ),
            HelpDataParam(
                "zd",
                "Дельта зигзагов"
            ),
            HelpDataParam(
                "zw",
                "Длина зигзага"
            ),
            HelpDataParam(
                "zh",
                "высота зигзага"
            ),
            HelpDataParam(
                "ve",
                "Стиль рисования [a|c|v][e|f|s|n][ |o]",
                FIELD_SELECTOR,
                listOf("a", "c", "v")
            ),
            HelpDataParam(
                "efsn",
                "Длина доски на стыке [e|f|s|n]",
                FIELD_SELECTOR,
                listOf("e","f","s","n")
            ),
            HelpDataParam(
                "ho",
                "Рисовать отверстия [_|h]",
                FIELD_SELECTOR,
                listOf("_","h")
            ),
            HelpDataParam(
                "ds",
                "", FIELD_TEXT
            ),
            HelpDataParam(
                "zig",
                "Фигура зигзага",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "hole",
                "Фигура зигзага отверстия",
                FIELD_FIGURE
            ),
            //-------------
            HelpDataParam(
                "t",
                "Фигура зигзага отверстия",
                FIELD_2
            ),
        ),
        creator = TPArg.create( "road",
            TPArg.figure("figure"),
            TPArg.block(
                TPArg("w", FIELD_1),
                TPArg("h", FIELD_1),
                TPArg.selector("s",  listOf("s", "a", "p", "d", "n")),
                TPArg.noneOrOne("b",
                    TPArg.block(
                        TPArg.oneVariant("v",
                            TPArg.item("l",
                                TPArg("l",FIELD_2)
                            ),
                            TPArg.item("r",
                                TPArg("r",FIELD_2)
                            ),
                            TPArg.item("t",
                                TPArg("t",FIELD_2)
                            ),
                            TPArg.item("h",
                                TPArg.oneOrMore("hi",
                                    TPArg.block(
                                        TPArg("hlr",FIELD_2)
                                    )
                                )
                            ),
                        )
                    )
                ),
            ),
            TPArg.noneOrLine(
                TPArg.block(
                    TPArg.noneOrLine(
                        TPArg("zd",FIELD_1),
                        TPArg("zw",FIELD_1),
                        TPArg("zh",FIELD_1),
                        TPArg.figure("zig"),
                        TPArg.figure("hole"),
                    )
                ),
                TPArg.block(
                    TPArg.union(
                        TPArg.selector("ve",  listOf("a", "c", "v")),
                        TPArg.selector("efsn",listOf("e","f","s","n")),
                        TPArg.selector("ho", listOf("_","h")),
                    ),
                ),
                TPArg.block(
                    TPArg("ds", FIELD_TEXT)
                )
            )
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
                f.collection()
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
                    val stl = st?.innerLine.orEmpty().trim()+"    "
                    val style = a?.inner?.getOrNull(2)?.innerLine.orEmpty().trim()+" "

                    val ltp = a?.inner?.getOrNull(3)

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
                                    ups = ltp?.let { readUps(it, figureExtractor) },
                                    heights = ltp?.getInnerAtName("h")?.let{ readHeights(it, figureExtractor )}
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

    private fun readHeights(
        items: TortoiseParserStackItem,
        figureExtractor: TortoiseFigureExtractor
    ): RoadHeightList {
        val he = items.blocks.map { block ->
            RoadHeights(
                RoadHeight(figureExtractor.valueAt(block, 0, 0.0)),
                RoadHeight(figureExtractor.valueAt(block, 1, 0.0)),
            )
        }
        return  RoadHeightList(he)
    }
}