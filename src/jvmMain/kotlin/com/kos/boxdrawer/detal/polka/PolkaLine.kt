package com.kos.boxdrawer.detal.polka

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class PolkaLine(
    val startHeight: Double,
    val parts: List<PolkaPart>,
    val polkaBottomOffset: Double = 20.0,
    val polkaTopOffset: Double = 20.0,
    val useAlgorithms: Array<String>? = null
) : TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return when (name) {
            "figure" -> toFigure(ds)
            "side" -> side(ds)
            else -> emptyList<TortoiseBlock>()
        }

    }

    private fun side(ds: DrawerSettings): List<TortoiseBlock> {
        val startCommands = mutableListOf<TortoiseCommand>()
        val lineCommands = mutableListOf<TortoiseCommand>()

        var fullWidth = 0.0;
        var currentHeight = 0.0;

        for (part in parts) {
            val angle = part.angleY;
            fullWidth += part.width * Math.cos(angle * Math.PI / 180);
            currentHeight += part.width * Math.sin(angle * Math.PI / 180);

            startCommands.add(TortoiseCommand.Angle(-angle));
            lineCommands.add(TortoiseCommand.Angle(-angle));
            val mh = part.holes.sortedBy { it.position }

            var lposition = 0.0;
            for (hole in mh) {
                var zigWidth = hole.width - ds.holeDrop;
                var zigCenter = hole.position;
                var zigHeight = ds.holeWeight;

                startCommands.add(TortoiseCommand.Move(zigCenter - lposition, -zigHeight / 2));
                startCommands.add(TortoiseCommand.Rectangle(zigWidth, zigHeight));
                startCommands.add(TortoiseCommand.Move(0.0, zigHeight / 2));

                lposition = zigCenter;
            }

            startCommands.add(TortoiseCommand.Move(part.width - lposition));

            var bottomOffset = polkaBottomOffset;

            lineCommands.add(TortoiseCommand.Move(0.0, -bottomOffset));
            lineCommands.add(TortoiseCommand.Line(0.0));
            lineCommands.add(TortoiseCommand.Line(part.width));
            lineCommands.add(TortoiseCommand.Move(0.0, bottomOffset));
        }


        var topOffset = polkaTopOffset + polkaBottomOffset;


        if (parts.size > 0) {
            fullWidth += polkaBottomOffset * Math.sin(parts.first().angleY * Math.PI / 180);
            fullWidth -= polkaBottomOffset * Math.sin(parts.last().angleY * Math.PI / 180);
        }

        lineCommands.add(TortoiseCommand.Move(0.0, -polkaBottomOffset));
        lineCommands.add(TortoiseCommand.Angle(90.0));
        lineCommands.add(TortoiseCommand.Line(topOffset + currentHeight, fullWidth));
        lineCommands.add(TortoiseCommand.Line(-topOffset));

        return listOf(
            TortoiseBlock(
                listOf(
                    TortoiseCommand.Clear(),
                    TortoiseCommand.Move(0.0, 0.0)
                )
            ),
            TortoiseBlock(
                startCommands.toList()
            ),
            TortoiseBlock(
                listOf(
                    TortoiseCommand.Clear(),
                    TortoiseCommand.Move(0.0, 0.0)
                )
            ),
            TortoiseBlock(
                lineCommands.toList()
            )
        )
    }

    private fun toFigure(ds: DrawerSettings): List<TortoiseBlock> {
        val endCommands = mutableListOf<TortoiseCommand>()
        val startCommands = mutableListOf<TortoiseCommand>()

        for (part in parts) {
            val angle = part.angle;
            startCommands.add(TortoiseCommand.Angle(-angle));
            val mh = part.holes.sortedBy { it.position }

            var lposition = 0.0
            for (hole in mh) {
                var zigWidth = hole.width;
                var zigStart = hole.position - (zigWidth / 2);
                var zigHeight = if (hole.height == 0.0) ds.boardWeight else hole.height;

                startCommands.add(
                    TortoiseCommand.SuperZig(
                        zigStart - lposition,
                        zigHeight,
                        zigWidth,
                        hole.angle
                    )
                )

                lposition = zigStart + zigWidth;
            }

            startCommands.add(TortoiseCommand.Line(part.width - lposition));


            val newEndCommands = mutableListOf<TortoiseCommand>()

            newEndCommands.add(TortoiseCommand.Angle(angle + 180));

            lposition = part.width;
            for (hole in mh.reversed()) {
                var zigWidth = hole.width;
                var zigStart = hole.position + (zigWidth / 2);
                var zigHeight = if (hole.height == 0.0) ds.boardWeight else hole.height;

                newEndCommands.add(
                    TortoiseCommand.SuperZig(
                        lposition - zigStart,
                        zigHeight,
                        zigWidth,
                        -hole.angle
                    )
                )
                lposition = zigStart - zigWidth;
            }
            newEndCommands.add(TortoiseCommand.Line(lposition));
            endCommands.addAll(0, newEndCommands);
        }

        /////

        val commands = mutableListOf<TortoiseCommand>();

        commands.add(TortoiseCommand.Line(0.0));
        commands.addAll(endCommands);
        commands.add(TortoiseCommand.Angle(-90.0));
        commands.add(TortoiseCommand.Line(startHeight));
        commands.addAll(startCommands);
        commands.add(TortoiseCommand.ClosePolygon());

        return listOf(TortoiseBlock(commands.toList()))
    }

    override val names: List<String>
        get() {
            if (useAlgorithms == null || useAlgorithms.size == 0)
                return names_
            else {
                return names_.filter { n -> useAlgorithms.contains(n) }
            }
        }

    companion object {

        private val names_ = listOf("figure", "side")
        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Команды для рисования полки."))
            sb.appendLine()
            sb.append(TortoiseParser.helpArgument("sH bOff? tOff? (w a ay? (hw hp hh ha)* )*"))

            sb.append(TortoiseParser.helpName("", "sH", "Высота в начале полки"))
            sb.append(TortoiseParser.helpName("", "bOff", "отступ от нижнего края для стенки"))
            sb.append(TortoiseParser.helpName("", "tOff", "отступ от верхнего края для стенки"))
            sb.append(TortoiseParser.helpName("", "w", "длина сегмента"))
            sb.append(TortoiseParser.helpName("", "a", "Угол наклона семента"))
            sb.append(TortoiseParser.helpName("", "ay", "угол наклона сегмента для стенки"))
            sb.append(TortoiseParser.helpName("", "hw", "ширина паза"))
            sb.append(TortoiseParser.helpName("", "hp", "позиция центра паза относительно начала сегмента"))
            sb.append(TortoiseParser.helpName("", "hh", "высота паза"))
            sb.append(TortoiseParser.helpName("", "ha", "угол наклона паза относительно сегмента"))

            sb.appendLine()
            return sb.toAnnotatedString()
        }

        fun parsePolka(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
            val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(a)

            val polka = PolkaLine(
                useAlgorithms = useAlgorithms,
                startHeight = items.doubleValue(0, 0.0),
                polkaBottomOffset = items.doubleValue(1, 20.0),
                polkaTopOffset = items.doubleValue(2, 20.0),
                parts = items.blocks.map { b ->
                    PolkaPart(

                        width = b.doubleValue(0, 0.0),
                        angle = b.doubleValue(1, 0.0),
                        angleY = b.doubleValue(2, 0.0),
                        holes = b.blocks.map { h ->
                            PolkaHole(
                                width = h.doubleValue(0, 0.0),
                                position = h.doubleValue(1, 0.0),
                                height = h.doubleValue(2, 0.0),
                                angle = h.doubleValue(3, 0.0),
                            )
                        }.toList()
                    )
                }.toList()
            )

            return polka;
        }
    }
}

data class PolkaPart(
    val width: Double,
    val angle: Double,
    val angleY: Double,
    val holes: List<PolkaHole> = emptyList()
) {
    val height get() = width * Math.sin(angle)
}

data class PolkaHole(
    val width: Double,
    val position: Double,
    val height: Double = 0.0,
    val angle: Double = 0.0,

    )
