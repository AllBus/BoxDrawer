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
                "box" -> parseBox(a.drop(1).joinToString(" "), f.drop(1).toTypedArray())
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


    fun parseBox(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
        val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(a)

        fun asDouble(text:String?): Double{
            return text?.toDoubleOrNull()?:0.0
        }

        fun asDouble(text:String?, defaultValue: Double): Double{
            return text?.toDoubleOrNull()?:defaultValue
        }

        fun zigInfo(block: TurtoiseParserStackItem?
        ):ZigzagInfo{
            return if (block == null)
                ZigzagInfo(width = 15.0, delta = 35.0)
            else
                ZigzagInfo(
                    width = asDouble(block.get(0), 15.0),
                    delta = asDouble(block.get(1), 35.0),
                    height = asDouble(block.get(2), 0.0),
                    )
        }

        fun parsePazForm(text:String?, defaultValue: PazForm):PazForm{
            return when (text?.lowercase()){
                "hole" -> PazForm.Hole
                "zig", "zag", "zigzag", "paz" -> PazForm.Paz
               // "flat" -> PazForm.Flat
                "", null -> defaultValue
                else -> PazForm.None
            }
        }

        fun waldInfo(block: TurtoiseParserStackItem?
        ): WaldParam {
            if (block == null)
                return WaldParam(
                    topOffset = 0.0,
                    bottomOffset = 0.0,
                    holeOffset = 0.0,
                    holeWeight = 0.0,
                    topForm = PazForm.None,
                    bottomForm = PazForm.Paz
                )
            else
                return WaldParam(
                    topOffset = asDouble(block.get(2)),
                    bottomOffset = asDouble(block.get(3)),
                    holeOffset = asDouble(block.get(4)),
                    holeWeight = asDouble(block.get(5)),
                    topForm = parsePazForm(block.get(0),PazForm.None),
                    bottomForm = parsePazForm(block.get(1), PazForm.Paz)
                )
        }

        return BoxAlgorithm(
            boxInfo =    BoxInfo(
                width = asDouble(items.get(0)),
                height = asDouble(items.get(1)),
                weight = asDouble(items.get(2)),
            ),
            zigW = zigInfo(items.blocks.getOrNull(0)),
            zigH = zigInfo(items.blocks.getOrNull(1)),
            zigWe = zigInfo(items.blocks.getOrNull(2)),
            wald = waldInfo(items.blocks.getOrNull(3)),
        )
    }



    fun helpFor(subStr: String): AnnotatedString {
        return when (subStr){
            "" -> helpFigures()
            "robot" -> RobotLine.help()
            "polka"-> PolkaLine.help()
            "box" -> boxHelp()
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

    fun boxHelp():AnnotatedString {
        val sb = AnnotatedString.Builder()
        sb.append(helpTitle("Рисование коробки"))
        sb.appendLine()
        sb.append(helpName("", "w h we (zW zWd) (zH zHd) (zWe zWed) (pazTop, pazBottom, toff, boff, hoff, hwe)", ""))
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