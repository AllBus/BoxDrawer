package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class RobotLine(
    val line: List<IRobotCommand>
): TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return when (name) {
            "robot" -> rotor(ds)
            else -> emptyList<TortoiseBlock>()
        }
    }

    override val names: List<String>
        get() = listOf("robot")

    fun rotor(ds: DrawerSettings): List<TortoiseBlock> {
        return line.map { it.draw(ds) }
    }

    companion object {
        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Команды рисования робота. Каждая команда окружается скобками ()"))
            sb.appendLine()
            sb.append(TortoiseParser.helpName("m", "x y", "переместить позицию"))
            sb.append(TortoiseParser.helpName("a", "a", "повернуть направление движение на угол a "))

            sb.append(TortoiseParser.helpName("x", "w h zw zh", ""))
            sb.append(TortoiseParser.helpName("c", "r hw hh", "нарисовать окружность радиусу r"))
            sb.append(TortoiseParser.helpName("h", "hw hh", "прямоугольник отверстия шириной hw  высотой hh"))
            sb.append(TortoiseParser.helpName("l", "w h zw zh c1w c1h c1d (lcom*) c2w c2h c2d (rcom*)", ""))
            sb.append(TortoiseParser.helpName("u", "w h zw zh zs", ""))
            sb.appendLine()
            return sb.toAnnotatedString()
        }

        fun parseRobot(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
            val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(a)

            val result = parseRobot(items, false)
            return RobotLine(result.toList())
        }

        fun parseRobot(v: TurtoiseParserStackItem, onlySimple: Boolean): List<IRobotCommand>{
            val result = mutableListOf<IRobotCommand>()
            var addBlocks = true
            if (v.isArgument()) {

            } else
                if (!v.isArgument()) {
                    val args = v.arguments()
                    if (args.size > 1) {
                        val com = args.first()

                        result.add(
                            when (com) {
                                "x" -> RobotRect(args.drop(1))

                                "c" -> RobotCircle(args.getOrElse(1) { "" }, args.getOrElse(2) { "" }, args.getOrElse(3) { "" })

                                "h",
                                "hole" -> RobotHole(args.getOrElse(1) { "" }, args.getOrElse(2) { "" })

                                "a" -> RobotAngle(args.getOrElse(1) { "" })
                                "m" -> RobotMove(args.getOrElse(1) { "" }, args.getOrElse(2) { "" })
                                else ->
                                    if (!onlySimple) {
                                        when (com) {
                                            "line",
                                            "l",
                                            "connect" -> {
                                                addBlocks = false
                                                RobotHand(
                                                    args.drop(1),
                                                    v.blocks.firstOrNull()?.let { b -> parseRobot(b, true) } ?: emptyList(),
                                                    v.blocks.getOrNull(1)?.let { b -> parseRobot(b, true) } ?: emptyList(),
                                                )
                                            }

                                            "u",
                                            "union" -> RobotUnion(args.drop(1))
                                            else ->  RobotEmpty()
                                        }
                                    } else
                                        RobotEmpty()
                            }
                        )
                    }
                }

            if (addBlocks) {
                result.addAll(
                    v.blocks.flatMap { b ->
                        parseRobot(b, false)
                    }
                )
            }
            return result.toList()
        }
    }
}