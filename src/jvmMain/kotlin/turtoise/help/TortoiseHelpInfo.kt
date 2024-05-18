package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import turtoise.TortoiseCommand
import turtoise.parser.TortoiseParser

class TortoiseHelpInfo : SimpleHelpInfo() {

    fun helpName(text: Char, arguments: String, description: String): HelpInfoCommand {
        return TortoiseParser.helpName(text, arguments, description)
    }

    override val name: String = ""
    override val title: AnnotatedString = TortoiseParser.helpTitle("Команды черепашки")
    override val commandList = listOf<HelpInfoCommand>(
        helpName(TortoiseCommand.TURTOISE_MOVE, "x y", "переместить позицию"),
        helpName(
            TortoiseCommand.TURTOISE_ANGLE,
            "a",
            "повернуть направление движение на угол a"
        ),
        helpName(
                TortoiseCommand.TURTOISE_ANGLE_ADD,
                "a",
                "повернуть направление движение на угол a относительно текущего угла"
            )
       ,helpName(
                    TortoiseCommand.TURTOISE_CLEAR,
                    "",
                    "Сбросить позицию на начало координат и поворот на 0"
                ),helpName(
                    TortoiseCommand.TURTOISE_LINE,
                    "d+",
                    "нарисовать длиной d. Последующие значения ресуют перпендикулярно"
                ),helpName(
            TortoiseCommand.TURTOISE_CLOSE,
            "",
            "закрыть многоугольник"
        ),helpName(
            TortoiseCommand.TURTOISE_CIRCLE,
            "r (sa ea)*",
            "круг радиуса r."
        ),helpName(
                    TortoiseCommand.TURTOISE_ELLIPSE,
                    "rx ry (sa ea)*",
                    "эллипс с радиусами rx и ry.\n" +
                            "     sa se необязательны задают начальный и конечный угол дуги"
                ),helpName(
                    TortoiseCommand.TURTOISE_RECTANGLE,
                    "w h?",
                    "прямоугольник шириной w и высотой h. Если h не задан, то квадрат"
                ),helpName(
                    TortoiseCommand.TURTOISE_ROUND_RECTANGLE,
                    "w h r",
                    "прямоугольник шириной w и высотой h cо скруглённми углами радиуса r"
                ),helpName(
                    TortoiseCommand.TURTOISE_REGULAR_POLYGON,
                    "n r",
                    "многоугольник радиуса с числом сторон n r"
                ),helpName(
                    TortoiseCommand.TURTOISE_ZIGZAG,
                    "w delta zigWidth board",
                    "Рисовать зигзаги:\n" +
                            "     w - общая длина,\n" +
                            "     delta - расстояние между началами двух зигзагов,\n" +
                            "     zigWidth- длина одноо зигзага,\n" +
                            "     board - толщина доски"
                ),helpName(
                    TortoiseCommand.TURTOISE_ZIGZAG_FIGURE,
                    "w delta zigWidth board (@program (args)?)",
                    "Рисовать зигзаги формы описаной в строке @program"
                ),helpName(
                    TortoiseCommand.TURTOISE_BEZIER,
                    "(tx1 ty1 tx2 ty2 ex ey)*",
                    "Рисовать линию безье из текущей позиции"
                ),helpName(
                    TortoiseCommand.TURTOISE_FIGURE,
                    "(@program args?)",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n"+
                                "     args - ноль или несколько значений которые передаются фигуре\n" +
                                "     каждое значение записывается как (name value) где\n"+
                                "     name - название переменной обращение в описании фиугры к которой происходит через .\n"+
                                "     value - значение подставляемое всместо переменной"
                ),helpName(
                    TortoiseCommand.TURTOISE_LOOP,
                    "c commands* <",
                    "выполнить c раз команды между > <"
                ),helpName(
                    TortoiseCommand.TURTOISE_MEMORY_ASSIGN,
                    "var arg*",
                    "присвоить переменной var сумму значений arg"
                ),
                helpName(' ', "@var", "подставить значение переменной var")
    )

}