package turtoise

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
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
                "polka" -> PolkaLine.parsePolka(a.drop(1).joinToString(" "), f.drop(1).toTypedArray())
                "robot" -> RobotLine.parseRobot(a.drop(1).joinToString(" "), f.drop(1).toTypedArray())
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






    fun helpFor(subStr: String): AnnotatedString {
        return when (subStr){
            "" -> helpFigures()
            "robot" -> RobotLine.help()
            "polka"-> PolkaLine.help()
            "hide"-> AnnotatedString("")
            else -> helpCommands()
        }

    }

    private fun helpCommands(): AnnotatedString {
        val sb = AnnotatedString.Builder()
        sb.append(helpTitle("Команды черепашки"))
        sb.appendLine()
        sb.append(helpName(TortoiseCommand.TURTOISE_MOVE, "x y", "переместить позицию"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ANGLE, "a", "повернуть направление движение на угол a "))
        sb.append(helpName(TortoiseCommand.TURTOISE_ANGLE_ADD, "a", "повернуть направление движение на угол a относительно текущего угла "))
        sb.append(helpName(TortoiseCommand.TURTOISE_LINE, "d+", "нарисовать длиной d. Последующие значения ресуют перпендикулярно"))
        sb.append(helpName(TortoiseCommand.TURTOISE_CLOSE, "", "закрыть многоугольник"))
        sb.append(helpName(TortoiseCommand.TURTOISE_CIRCLE, "r (sa ea)*", "круг радиуса r.\n" +
                "     sa se необязательны задают начальный и конечный угол дуги"))
        sb.append(helpName(TortoiseCommand.TURTOISE_ELLIPSE, "r rm (sa ea)*", "эллипс с радиусами r rm.\n" +
                "     sa se необязательны задают начальный и конечный угол дуги"))
        sb.append(helpName(TortoiseCommand.TURTOISE_RECTANGLE, "w h?", "прямоугольник шириной w и высотой h. Если h не задан, то квадрат"))
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

        sb.append(helpCommands())

        return  sb.toAnnotatedString()

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