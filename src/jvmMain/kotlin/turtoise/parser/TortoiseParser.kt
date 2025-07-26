package turtoise.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.detal.box.BoxHelpInfo
import com.kos.boxdrawer.detal.grid.GridAlgorithm
import com.kos.boxdrawer.detal.polka.PolkaHelpInfo
import com.kos.boxdrawer.detal.polka.PolkaLine
import com.kos.boxdrawer.detal.robot.RobotHelpInfo
import com.kos.boxdrawer.detal.robot.RobotLine
import turtoise.TemplateAlgorithm
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureAlgorithm
import turtoise.TortoiseSimpleAlgorithm
import turtoise.dxf.DxfFileAlgorithm
import turtoise.dxf.DxfHelpInfo
import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand
import turtoise.help.HideHelpInfo
import turtoise.help.IHelpInfo
import turtoise.help.TortoiseHelpInfo
import turtoise.memory.keys.MemoryKey
import turtoise.rect.RekaAlgorithm
import turtoise.svg.SvgAlgorithm
import turtoise.svg.SvgHelpInfo
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
            '/' -> '/'
            '\\' -> '\\'
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
        SvgHelpInfo(),
        //RekaHelpInfo(),
        //TemplateHelpInfo(),
        HideHelpInfo(),
    ).map { it.name to it }.toMap()

    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9' || c == '-' || c == '+'
    }

    public fun extractTortoiseCommands(line: String): Pair<String, TortoiseAlgorithm> {
        val items: TortoiseParserStackBlock = parseSkobki(line)

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
                "svg" -> SvgAlgorithm(
                    items.blocks.map{it.innerLine }
                )

                "template" -> TemplateAlgorithm.create(f.getOrElse(1) { "figure" }, items)

                "reka" -> RekaAlgorithm(
                    items, f.drop(1).dropLast(1).toTypedArray()
                )
                "grid" -> GridAlgorithm(items)

                "" -> TortoiseFigureAlgorithm(f.getOrElse(1) { "figure" }, items)
                else -> parseSimpleLine(items)
            }
        } else {
            "" to parseSimpleLine(items)
        }
    }

    fun parseSimpleLine(items: TortoiseParserStackItem): TortoiseAlgorithm {
        val result = mutableListOf<TortoiseCommand>()

        var currentCommand = TortoiseCommand.TURTOISE_MOVE
        var currentValues = mutableListOf<TortoiseParserStackItem>()

        items.inner.forEach { item ->
            when (item) {
                is TortoiseParserStackArgument -> {
                    val d = item.argument
                    if (d.isNotEmpty()) {
                        val c = d.prefix()

                        if (c.isDigit() || c == '-') {
                            currentValues.add(item)
                        } else
                            if (c == '@') {
                                currentValues.add(TortoiseParserStackArgument(d.drop()))
                            } else {
                                result.add(
                                    TortoiseCommand.createFromItem(
                                        currentCommand,
                                        currentValues
                                    )
                                )
                                currentValues = mutableListOf<TortoiseParserStackItem>()
                                val b = d.drop()
                                if (b.isNotEmpty()) {
                                    currentValues.add(TortoiseParserStackArgument(b))
                                }
                                currentCommand = c
                            }
                    }
                }

                is TortoiseParserStackBlock -> {
                    currentValues.add(item)
                }

                else -> {}
            }
        }

        result.add(TortoiseCommand.createFromItem(currentCommand, currentValues))

        return TortoiseSimpleAlgorithm("_", result.toList());

    }

    fun parseSkobki(a: String): TortoiseParserStackBlock {

        val le = a.length

        fun appendValues(top: TortoiseParserStackBlock, start: Int, end: Int) {
            val d = if (start >= end) "" else a.substring(start, end)
            top.add(d.split(' ').filter { v -> v.isNotEmpty() })
        }

        fun parseLine(position: Int, top: TortoiseParserStackBlock): Int {
            var i = position
            var startPos = position
            while (i < le) {
                val c = a[i]
                when (c) {
                    '(',
                    '[',
                    '{' -> {
                        appendValues(top, startPos, i)

                        val next = TortoiseParserStackBlock(
                            skobka = c
                        )
                        top.add(next)
                        startPos = parseLine(i + 1, next)
                        i = startPos
                        continue
                    }

                    ')',
                    ']',
                    '}' -> {
                        appendValues(top, startPos, i)
                        return i + 1
                    }
                }
                i++
            }
            appendValues(top, startPos, le)
            return le
        }


        val top = TortoiseParserStackBlock();

        parseLine(0, top)
        return top
    }

    fun parseSkobkiOld(a: String): TortoiseParserStackBlock {
        val stack = Stack<TortoiseParserStackBlock>();
        val top = TortoiseParserStackBlock();
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

                    val next = TortoiseParserStackBlock(
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
                    var pi: TortoiseParserStackBlock?
                    if (stack.isNotEmpty()) {
                        println("$i $c $clo ${stack.peek().skobka}")
                    }
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

    fun helpFor(subStr: String, command: String): AnnotatedString {
        return when (subStr) {
            "" -> helpFigures()
            else -> {
                val helper = helpers[subStr] ?: th
                if (command.isEmpty()) {
                    helper.help()
                } else {
                    helper.help(command)
                }
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
        sb.append(helpDescr(" - Рисовать содержимое dxf файла"))
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

    //    fun helpName(text: Char, arguments: String, description: String): HelpInfoCommand {
//        return helpName(text.toString(), arguments, description)
//    }
//
    fun helpName(text: String, arguments: String, description: String): HelpInfoCommand {
        return HelpInfoCommand(
            text, listOf(
                HelpData(
                    arguments, description
                )
            )
        )
//        val sb = AnnotatedString.Builder()
//        sb.append(helpName(text))
//        sb.append(" ")
//        sb.append(helpArgument(arguments))
//        //sb.append(" ")
//        sb.append(helpDescr(" - " + description))
//        sb.appendLine()
//
//        return HelpInfoCommand(
//            name = text,
//            text = sb.toAnnotatedString()
//        )
    }
//
//    fun helpName(text: String, args: List<HelpData>):HelpInfoCommand {
//        val sb = AnnotatedString.Builder()
//        args.forEach { argument ->
//            sb.append(helpName(text))
//            sb.append(" ")
//            sb.append(helpArgument(argument.argument))
//            sb.append(helpDescr(" - " + argument.description))
//            sb.appendLine()
//            argument.params.forEach { param ->
//                sb.append("    ")
//                sb.append(helpParam(param.name))
//                sb.append(" - ")
//                sb.append(helpDescr(param.description))
//                sb.appendLine()
//            }
//        }
//
//        return HelpInfoCommand(
//            name = text,
//            text = sb.toAnnotatedString()
//        )
//    }

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

    fun helpParam(text: String): AnnotatedString {
        return AnnotatedString(
            text, SpanStyle(
                color = Color(0x6026A530),
            )
        )
    }

    fun AnnotatedString.Builder.append(key: MemoryKey){
        this.append(key.name)
    }
    /**
     * Корректная подсветка синтаксиса без изменения длины текста
     */
    fun syntaxHighlight(line: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        var i = 0
        val bracketColors = listOf(
            Color(0xFF4A148C), // уровень 0 — тёмно-фиолетовый
            Color(0xFF00695C), // уровень 1 — тёмно-бирюзовый
            Color(0xFF990087), // уровень 2 — тёмно-красный
            Color(0xFF1A237E), // уровень 3 — тёмно-синий
            Color(0xFF33691E), // уровень 4 — тёмно-зелёный
            Color(0xFF3E2723)  // уровень 5+ — тёмно-коричневый
        )
        val stack = mutableListOf<Char>()
        val splashKeys = turtoise.SplashMap.splashes.keys
        while (i < line.length) {
            val c = line[i]
            when {
                c == '/' -> {
                    builder.append("/")
                    i++
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    val word = line.substring(start, i)
                    if (word.isNotEmpty()) {
                        val color = if (splashKeys.contains(word)) Color(0xFF0D47A1) else Color(0xFFB71C1C)
                        builder.pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
                        builder.append(word)
                        builder.pop()
                    }
                }
                c == '@' -> {
                    val start = i
                    i++
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    builder.pushStyle(SpanStyle(color = Color(0xFFB26A00)))
                    builder.append(line.substring(start, i))
                    builder.pop()
                }
                c.isDigit() || (c == '-' && i + 1 < line.length && line[i + 1].isDigit()) -> {
                    val start = i
                    if (c == '-') i++
                    while (i < line.length && (line[i].isDigit() || line[i] == '.')) i++
                    builder.pushStyle(SpanStyle(color = Color(0xFF388E3C)))
                    builder.append(line.substring(start, i))
                    builder.pop()
                }
                c.isLetter() -> {
                    val start = i
                    while (i < line.length && line[i].isLetter()) i++
                    builder.pushStyle(SpanStyle(color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold))
                    builder.append(line.substring(start, i))
                    builder.pop()
                }
                c == '(' || c == '[' || c == '{' -> {
                    val level = stack.size.coerceAtMost(bracketColors.lastIndex)
                    builder.pushStyle(SpanStyle(color = bracketColors[level]))
                    builder.append(c.toString())
                    builder.pop()
                    stack.add(c)
                    i++
                }
                c == ')' || c == ']' || c == '}' -> {
                    val level = (stack.size - 1).coerceAtLeast(0).coerceAtMost(bracketColors.lastIndex)
                    builder.pushStyle(SpanStyle(color = bracketColors[level]))
                    builder.append(c.toString())
                    builder.pop()
                    if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
                    i++
                }
                else -> {
                    builder.append(c.toString())
                    i++
                }
            }
        }
        return builder.toAnnotatedString()
    }

}