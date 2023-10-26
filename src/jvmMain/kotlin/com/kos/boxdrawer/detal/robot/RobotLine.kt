package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.detal.box.CompositeBox
import turtoise.*

class RobotLine(
    val line: List<IRobotCommand>
) : TortoiseAlgorithm {
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

        private val factories = listOf(
            RobotRect.Factory,
            RobotCircle.Factory,
            RobotHole.Factory,
            RobotAngle.Factory,
            RobotMove.Factory,
            RobotHand.Factory,
            RobotHardRect.Factory,
            RobotUnion.Factory,
            CompositeBox.Factory,
        )

        private val simpleFactories = factories.asSequence().filter { it.isSimple }.flatMap { f -> f.names.map { n -> n to f } }.toMap()
        private val mediumFactories = factories.asSequence().filter { !it.isSimple }.flatMap { f -> f.names.map { n -> n to f } }.toMap()

        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Команды рисования робота. Каждая команда окружается скобками ()"))
            sb.appendLine()
            factories.forEach { sb.append(it.help()) }
            sb.appendLine()
            return sb.toAnnotatedString()
        }

        fun parseRobot(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
            val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(a)

            val result = parseRobot(items, false)
            return RobotLine(result.toList())
        }

        fun parseRobot(v: TurtoiseParserStackItem, onlySimple: Boolean): List<IRobotCommand> {
            val result = mutableListOf<IRobotCommand>()
            var addBlocks = true
            if (v.isArgument()) {

            } else
                if (!v.isArgument()) {
                    val args = v.arguments()
                    if (args.size > 1) {
                        val com = args.first()

                        val factory = simpleFactories[com] ?: (
                            if (!onlySimple) {
                                addBlocks = false
                                mediumFactories[com] ?: RobotEmpty.Factory
                            } else
                                RobotEmpty.Factory
                            )

                        result.add(factory.create(args.drop(1), v))
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

