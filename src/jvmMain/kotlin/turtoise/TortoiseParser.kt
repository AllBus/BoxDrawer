package turtoise

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.detal.box.BoxHelpInfo
import com.kos.boxdrawer.detal.polka.PolkaHelpInfo
import com.kos.boxdrawer.detal.polka.PolkaLine
import com.kos.boxdrawer.detal.robot.RobotHelpInfo
import com.kos.boxdrawer.detal.robot.RobotLine
import turtoise.dxf.DxfFileAlgorithm
import turtoise.dxf.DxfHelpInfo
import turtoise.help.HelpInfoCommand
import turtoise.help.HideHelpInfo
import turtoise.help.IHelpInfo
import turtoise.help.TortoiseHelpInfo
import turtoise.memory.keys.MemoryKey
import turtoise.rect.RekaAlgorithm
import java.util.Stack


object TortoiseParser {

    private val sep = charArrayOf(' ', '\t', ';', '\n')

    fun closeBrace(brace: Char): Char {
        return when (brace) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '>' -> '<'
            '<' -> '>'
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '"' -> '"'
            '\'' -> '\''
            else -> ' '
        }
    }

    val th = TortoiseHelpInfo()
    val helpers = listOf<IHelpInfo>(
        th,
        RobotHelpInfo(),
        BoxHelpInfo(),
        PolkaHelpInfo(),
        DxfHelpInfo(),
        //RekaHelpInfo(),
        //TemplateHelpInfo(),
       HideHelpInfo(),
    ).map { it.name to it }.toMap()

    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9' || c == '-' || c == '+'
    }

    public fun extractTortoiseCommands(line: String): Pair<String, TortoiseAlgorithm> {
        val items: TurtoiseParserStackBlock = TortoiseParser.parseSkobki(line)

        val n = items.name
        return if (n.name.contains("@")) {
            val f = items.name.name.split('@')
            (f.drop(1).lastOrNull() ?: "") to when (f[0]) {
                "polka" -> PolkaLine.parsePolka(items, f.drop(1).dropLast(1).toTypedArray())
                "robot" -> RobotLine.parseRobot(items, f.drop(1).dropLast(1).toTypedArray())
                "box" -> BoxAlgorithm.parseBox(items, f.drop(1).dropLast(1).toTypedArray())
                "dxf" -> DxfFileAlgorithm(
                    items.blocks.firstOrNull()?.innerLine.orEmpty()
                )
                "template" -> TemplateAlgorithm(
                    name = f.getOrElse(1) { "figure" },
                    line = items.getBlockAtName("figure") ?: TurtoiseParserStackBlock(),
                    default = items.getBlockAtName("default") ?: TurtoiseParserStackBlock(),
                    template = items.getBlockAtName("form") ?: TurtoiseParserStackBlock(),
                )
                "reka" -> RekaAlgorithm(
                    items, f.drop(1).dropLast(1).toTypedArray()
                )

                "" -> TortoiseFigureAlgorithm(f.getOrElse(1) { "figure" }, items)
                else -> parseSimpleLine(items)
            }
        } else {
            "" to parseSimpleLine(items)
        }
    }

    fun parseSimpleLine(items: TurtoiseParserStackItem): TortoiseAlgorithm {
        val result = mutableListOf<TortoiseCommand>()

        var currentCommand = TortoiseCommand.TURTOISE_MOVE
        var currentValues = mutableListOf<TurtoiseParserStackItem>()

        items.inner.forEach { item ->
            when (item) {
                is TurtoiseParserStackArgument -> {
                    val d = item.argument
                    if (d.isNotEmpty()) {
                        val c = d.prefix()

                        if (c.isDigit() || c == '-') {
                            currentValues.add(item)
                        } else
                            if (c == '@') {
                                currentValues.add(TurtoiseParserStackArgument(d.drop()))
                            } else {
                                result.add(
                                    TortoiseCommand.createFromItem(
                                        currentCommand,
                                        currentValues
                                    )
                                )
                                currentValues = mutableListOf<TurtoiseParserStackItem>()
                                val b = d.drop()
                                if (b.isNotEmpty()) {
                                    currentValues.add(TurtoiseParserStackArgument(b))
                                }
                                currentCommand = c
                            }
                    }
                }

                is TurtoiseParserStackBlock -> {
                    currentValues.add(item)
                }

                else -> {}
            }
        }

        result.add(TortoiseCommand.createFromItem(currentCommand, currentValues))

        return TortoiseSimpleAlgorithm("_", result.toList());

    }

    fun parseSkobki(a: String): TurtoiseParserStackBlock {
        val stack = Stack<TurtoiseParserStackBlock>();
        val top = TurtoiseParserStackBlock();
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

                    val next = TurtoiseParserStackBlock(
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
                    var pi: TurtoiseParserStackBlock?
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

    fun asDouble(text: MemoryKey?): Double {
        return text?.toDoubleOrNull() ?: 0.0
    }

    fun asDouble(text: MemoryKey?, defaultValue: Double): Double {
        return text?.toDoubleOrNull() ?: defaultValue
    }

    fun helpFor(subStr: String, command:String): AnnotatedString {
        return when (subStr) {
            "" -> helpFigures()
            else -> {
                val helper = helpers.get(subStr)?:th
                helper.help(command)+AnnotatedString("\n\n\n")+
                        helper.help()
            }
        }
    }

    private fun helpFigures(): AnnotatedString {
        val sb = AnnotatedString.Builder()
        sb.append(helpTitle("Доступные фигуры"))
        sb.appendLine()

        sb.append(helpName("polka"))
        sb.append(helpArgument("@figure"))
        sb.append(helpArgument("@side"))
        sb.appendLine()
        sb.append(helpArgument("@figure"))
        sb.append(helpDescr(" - Рисовать симметричный многоугольник"))
        sb.appendLine()
        sb.append(helpArgument("@side"))
        sb.append(helpDescr(" - Рисовать стенку под многоугольник figure"))

        sb.appendLine()
        sb.appendLine()

        sb.append(helpName("robot"))
        sb.append(helpDescr(" - Рисовать части робота"))
        sb.appendLine()
        sb.appendLine()

        sb.append(helpName("box"))
        sb.append(helpDescr(" - Рисовать коробку"))
        sb.appendLine()
        sb.appendLine()

        sb.append(helpName("dxf"))
        sb.append(helpDescr(" - Рисовать срдержимое dxf файла"))
        sb.appendLine()
        sb.appendLine()

        sb.append(th.help())

        return sb.toAnnotatedString()
    }

    fun helpTitle(text: String): AnnotatedString {
        return AnnotatedString(
            text, SpanStyle(
                color = Color(0x60FFE900),
                fontWeight = FontWeight(600)
            )
        )
    }

    fun helpName(text: String): AnnotatedString {
        return AnnotatedString(
            text, SpanStyle(
                color = Color(0x60FF6A00),
                fontWeight = FontWeight(600)
            )
        )
    }

    fun helpName(text: Char, arguments: String, description: String): HelpInfoCommand {
        return helpName(text.toString(), arguments, description)
    }

    fun helpName(text: String, arguments: String, description: String): HelpInfoCommand {

        val sb = AnnotatedString.Builder()
        sb.append(helpName(text))
        sb.append(" ")
        sb.append(helpArgument(arguments))
        //sb.append(" ")
        sb.append(helpDescr(" - " + description))
        sb.appendLine()

        return HelpInfoCommand(
            name = text,
            text = sb.toAnnotatedString()
        )
    }

    fun helpArgument(text: String): AnnotatedString {
        return AnnotatedString(
            text, SpanStyle(
                color = Color(0x60FFAA00),
            )
        )
    }

    fun helpDescr(text: String): AnnotatedString {
        return AnnotatedString(
            text, SpanStyle(
                color = Color(0x60A1FF00),
            )
        )
    }

}