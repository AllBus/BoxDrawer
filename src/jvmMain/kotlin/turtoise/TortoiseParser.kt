package turtoise

import com.kos.boxdrawer.detal.polka.PolkaHole
import com.kos.boxdrawer.detal.polka.PolkaLine
import com.kos.boxdrawer.detal.polka.PolkaPart
import com.kos.boxdrawer.detal.robot.*
import java.util.*


object TortoiseParser {

    private val sep = charArrayOf(' ', '\t', ';', '\n')

    private fun closeBrace(brace: Char): Char {
        return when (brace) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '>' -> '<'
            '<' -> '>'
            '"' -> '"'
            '\'' -> '\''
            else -> ' '
        }
    }

    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9' || c == '-' || c == '+'
    }

    public fun extractTortoiseCommands(line: String): TortoiseAlgorithm {
        val a = line.split(*sep).filter { it.isNotEmpty() }
        return if (a.isEmpty()) {
            TortoiseSimpleAlgorithm("_", listOf<TortoiseCommand>())
        } else {
            val f = a.first().split('@')
            when (f[0]) {
                "polka" -> parsePolka(a.drop(1).joinToString(" "), f.drop(1).toTypedArray())
                "robot" -> parseRobot(a.drop(1).joinToString(" "), f.drop(1).toTypedArray())
                else ->
                    parseSimpleLine(a)
            }
        }
    }

    private fun parseSimpleLine(a: List<String>): TortoiseAlgorithm {
        val result = mutableListOf<TortoiseCommand>()

        var currentCommand = TortoiseCommand.TURTOISE_MOVE
        var currentValues = mutableListOf<String>()

        for (d in a) {
            var c = d.first()
            val sv: Int

            if (isDigit(c)) {
                c = ' ';
                sv = 0;
            } else {
                sv = 1;
            }

            val v = d.substring(sv)
            if (c != ' ' && c != '@') {
                result.add(TortoiseCommand.create(currentCommand, currentValues))

                currentCommand = c
                currentValues = mutableListOf<String>()
            }

            if (v.isNotEmpty()) {
                currentValues.add(v)
            }
        }

        result.add(TortoiseCommand.create(currentCommand, currentValues))

        return TortoiseSimpleAlgorithm("_", result.toList());

    }

    fun parseSkobki(a: String): TurtoiseParserStackItem {
        val stack = Stack<TurtoiseParserStackItem>();
        val top = TurtoiseParserStackItem();
        var item = top;

        var predIndex = 0;

        for (i in a.indices) {
            val c = a[i];
            when (c) {
                '(',
                '[',
                '{' -> {
                    val d = if (predIndex >= i) "" else a.substring(predIndex, i)
                    predIndex = i + 1;

                    val next = TurtoiseParserStackItem(
                        skobka = c
                    )


                    item.add(d.split(' ').filter { v -> v.isNotEmpty() })
                    item.add(next)

                    stack.push(item)
                    item = next;
                }

                ')',
                ']',
                '}' -> {
                    val d = if (predIndex >= i) "" else a.substring(predIndex, i)
                    predIndex = i + 1;

                    item.add(d.split(' ').filter { v -> v.isNotEmpty() })

                    val clo = closeBrace(c);
                    var pi: TurtoiseParserStackItem?
                    do {
                        if (stack.isEmpty()) {
                            pi = null;
                        } else {
                            pi = stack.pop()
                        }
                    } while (pi != null && pi.skobka != clo)

                    item = pi ?: top
                }

                else -> {}
            }
        }

        val d = if (predIndex >= a.length) "" else a.substring(predIndex, a.length)
        item.add(d.split(' ').filter { v -> v.isNotEmpty() })

        return top;
    }

    fun parsePolka(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
        val items: TurtoiseParserStackItem = parseSkobki(a)

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

    fun parseRobot(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
        val items: TurtoiseParserStackItem = parseSkobki(a)

        val result = mutableListOf<IRobotCommand>()

        items.inner.forEach { v ->
            if (v.isArgument()) {

            } else
                if (!v.isArgument()) {
                    val args = v.inner.filter { it.isArgument() }.map { it.argument }
                    if (args.size > 1) {
                        val com = args.first()

                        result.add(
                            when (com) {
                                "x" -> RobotRect(args.drop(1))

                                "c" -> RobotCircle(args.getOrElse(1) { "" }, args.getOrElse(2) { "" })

                                "h",
                                "hole" -> RobotHole(args.getOrElse(1) { "" }, args.getOrElse(2) { "" })

                                "line",
                                "l",
                                "connect" -> RobotHand(args.drop(1))

                                "u",
                                "union" -> RobotUnion(args.drop(1))

                                "a" -> RobotAngle(args.getOrElse(1) { "" })
                                "m" -> RobotMove(args.getOrElse(1) { "" }, args.getOrElse(2) { "" })
                                else ->
                                    RobotEmpty()
                            }
                        )

                    }
                }
        }

        return RobotLine(result.toList())
    }
}