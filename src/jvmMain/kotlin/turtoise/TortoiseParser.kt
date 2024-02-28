package turtoise

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.kos.boxdrawer.detal.box.BoxAlgorithm
import com.kos.boxdrawer.detal.box.BoxInfo
import com.kos.boxdrawer.detal.box.PazForm
import com.kos.boxdrawer.detal.box.WaldParam
import com.kos.boxdrawer.detal.polka.PolkaHole
import com.kos.boxdrawer.detal.polka.PolkaLine
import com.kos.boxdrawer.detal.polka.PolkaPart
import com.kos.boxdrawer.detal.robot.*
import java.util.*


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

    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9' || c == '-' || c == '+'
    }

    public fun extractTortoiseCommands(line: String): Pair<String, TortoiseAlgorithm> {
        val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(line)

        val n = items.name
        return if (n.contains("@")){
            val f = items.name.split('@')
            (f.drop(1).lastOrNull()?:"") to when (f[0]){
                "polka" -> PolkaLine.parsePolka(items, f.drop(1).dropLast(1).toTypedArray())
                "robot" -> RobotLine.parseRobot(items, f.drop(1).dropLast(1).toTypedArray())
                "box" -> BoxAlgorithm.parseBox(items, f.drop(1).dropLast(1).toTypedArray())
                "" -> TortoiseFigureAlgorithm(f.getOrElse(1){"figure"}, items)
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
            when(item){
                is TurtoiseParserStackArgument -> {
                    val d = item.argument
                    if (d.isNotEmpty()){
                        val c = d.first()
                        if (c.isDigit() || c =='-'){
                            currentValues.add(item)
                        } else
                        if (c=='@'){
                            currentValues.add(TurtoiseParserStackArgument(d.drop(1)))
                        }
                        else {
                            result.add(TortoiseCommand.createFromItem(currentCommand, currentValues))
                            currentValues = mutableListOf<TurtoiseParserStackItem>()
                            val b = d.drop(1)
                            if (b.isNotEmpty()) {
                                currentValues.add(TurtoiseParserStackArgument( b))
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

    fun asDouble(text:String?): Double{
        return text?.toDoubleOrNull()?:0.0
    }

    fun asDouble(text:String?, defaultValue: Double): Double{
        return text?.toDoubleOrNull()?:defaultValue
    }

    fun helpFor(subStr: String): AnnotatedString {
        return when (subStr){
            "" -> helpFigures()
            "robot" -> RobotLine.help()
            "polka"-> PolkaLine.help()
            "box" -> BoxAlgorithm.help()
            "hide"-> AnnotatedString("")
            else -> helpCommands()
        }
    }

    private fun helpCommands(): AnnotatedString {
        val sb = AnnotatedString.Builder()
        sb.append(helpTitle("Команды черепашки"))
        sb.appendLine()
        sb.append(helpName(TortoiseCommand.TURTOISE_MOVE, "x y", "переместить позицию"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ANGLE, "a", "повернуть направление движение на угол a"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ANGLE_ADD, "a", "повернуть направление движение на угол a относительно текущего угла"))
        sb.append(helpName(TortoiseCommand.TURTOISE_CLEAR, "", "Сбросить позицию на начало координат и поворот на 0"))
        sb.append(helpName(TortoiseCommand.TURTOISE_LINE, "d+", "нарисовать длиной d. Последующие значения ресуют перпендикулярно"))
        sb.append(helpName(TortoiseCommand.TURTOISE_CLOSE, "", "закрыть многоугольник"))
        sb.append(helpName(TortoiseCommand.TURTOISE_CIRCLE, "r (sa ea)*", "круг радиуса r."))
        sb.append(helpName(TortoiseCommand.TURTOISE_ELLIPSE, "rx ry (sa ea)*", "эллипс с радиусами rx и ry.\n" +
                "     sa se необязательны задают начальный и конечный угол дуги"))
        sb.append(helpName(TortoiseCommand.TURTOISE_RECTANGLE, "w h?", "прямоугольник шириной w и высотой h. Если h не задан, то квадрат"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ROUND_RECTANGLE, "w h r", "прямоугольник шириной w и высотой h cо скруглённми углами радиуса r"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ZIGZAG, "w delta zigWidth board", "Рисовать зигзаги:\n" +
                "     w - общая длина,\n" +
                "     delta - расстояние между началами двух зигзагов,\n" +
                "     zigWidth- длина одноо зигзага,\n" +
                "     board - толщина доски"))
        sb.append(helpName(TortoiseCommand.TURTOISE_BEZIER, "(tx1 ty1 tx2 ty2 ex ey)*", "Рисовать линию безье из текущей позиции"))
        sb.append(helpName(TortoiseCommand.TURTOISE_LOOP, "c commands* <", "выполнить c раз команды между > <"))
        sb.append(helpName(TortoiseCommand.TURTOISE_MEMORY_ASSIGN, "var arg*", "присвоить переменной var сумму значений arg"))
        sb.append(helpName("", "@var", "подставить значение переменной var"))
        sb.appendLine()
        return sb.toAnnotatedString()
    }

    private fun helpFigures():AnnotatedString {
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

        sb.append(helpCommands())

        return sb.toAnnotatedString()
    }

    fun helpTitle(text:String): AnnotatedString{
        return AnnotatedString(text, SpanStyle(
            color = Color(0x60FFE900),
            fontWeight = FontWeight(600)
        ))
    }
    fun helpName(text:String): AnnotatedString{
        return AnnotatedString(text, SpanStyle(
            color = Color(0x60FF6A00),
            fontWeight = FontWeight(600)
        ))
    }

    fun helpName(text:Char, arguments:String, description:String): AnnotatedString{
        return helpName(text.toString(), arguments, description)
    }
    fun helpName(text:String, arguments:String, description:String): AnnotatedString{
        val sb = AnnotatedString.Builder()
        sb.append(helpName(text))
        sb.append(" ")
        sb.append(helpArgument(arguments))
        //sb.append(" ")
        sb.append(helpDescr(" - "+description))
        sb.appendLine()
        return sb.toAnnotatedString()
    }

    fun helpArgument(text:String): AnnotatedString{
        return AnnotatedString(text, SpanStyle(
            color = Color(0x60FFAA00),
        ))
    }

    fun helpDescr(text:String): AnnotatedString{
        return AnnotatedString(text, SpanStyle(
            color = Color(0x60A1FF00),
        ))
    }

}