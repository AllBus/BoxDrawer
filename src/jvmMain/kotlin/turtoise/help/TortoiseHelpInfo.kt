package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import turtoise.TortoiseCommand
import turtoise.parser.TortoiseParser

class TortoiseHelpInfo : SimpleHelpInfo() {

    fun helpName(text: Char, arguments: String, description: String): HelpInfoCommand {
        return TortoiseParser.helpName(text, arguments, description)
    }

    fun helpName(text: Char, args: List<HelpData>): HelpInfoCommand {
        return TortoiseParser.helpName("$text", args)
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
        ), helpName(
            TortoiseCommand.TURTOISE_CLEAR,
            "",
            "Сбросить позицию на начало координат и поворот на 0"
        ), helpName(
            TortoiseCommand.TURTOISE_LINE,
            "d+",
            "нарисовать длиной d. Последующие значения ресуют перпендикулярно"
        ), helpName(
            TortoiseCommand.TURTOISE_CLOSE,
            "",
            "закрыть многоугольник"
        ), helpName(
            TortoiseCommand.TURTOISE_CIRCLE,
            listOf(
                HelpData(
                    "r (sa wa)*",
                    "круг радиуса r",
                    listOf(
                        HelpDataParam("r", "радиус"),
                        HelpDataParam("sa", "Задаёт начало дуги в градусах"),
                        HelpDataParam("wa", "Задаёт длину дуги в градусах"),
                    )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_ELLIPSE,
            listOf(
                HelpData(
                    "rx ry (sa wa)*",
                    "эллипс с радиусами rx и ry",
                    listOf(
                        HelpDataParam("rx", "радиус по оси x"),
                        HelpDataParam("ry", "радиус по оси y"),
                        HelpDataParam("sa", "Задаёт начало дуги в градусах"),
                        HelpDataParam("wa", "Задаёт длину дуги в градусах"),
                    )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_RECTANGLE,
            "w h?",
            "прямоугольник шириной w и высотой h. Если h не задан, то квадрат"
        ), helpName(
            TortoiseCommand.TURTOISE_ROUND_RECTANGLE,
            "w h r",
            "прямоугольник шириной w и высотой h cо скруглённми углами радиуса r"
        ), helpName(
            TortoiseCommand.TURTOISE_REGULAR_POLYGON,
            listOf(
                HelpData(
                    "n r",
                    "многоугольник радиуса r с числом сторон n",
                    listOf(
                        HelpDataParam("n", "числом сторон"),
                        HelpDataParam("r", "радиус"),
                    )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_ZIGZAG,
            listOf(
                HelpData(
                    "w delta zigWidth board",
                    "Рисовать зигзаги:",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одноо зигзага"),
                        HelpDataParam("board", "толщина доски"),

                        )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_ZIGZAG_FIGURE,
            listOf(
                HelpData(
                    "w delta zigWidth board (@program (args)?)",
                    "Рисовать зигзаги формы описаной в строке @program",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одноо зигзага"),
                        HelpDataParam("board", "толщина доски"),
                        HelpDataParam("(@program (args)?)", "фигура для формы зигзага"),
                        )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_BEZIER,
            "(tx1 ty1 tx2 ty2 ex ey)*",
            "Рисовать линию безье из текущей позиции"
        ), helpName(
            TortoiseCommand.TURTOISE_FIGURE,
            listOf(
                HelpData(
                    "(@program args?)",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam("@program", "назвиние програмы рисования фигур"),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где"
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через ."
                        ),
                        HelpDataParam("value", "значение подставляемое всместо переменной"),
                    )
                ),
                HelpData(
                    "(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam("tortoise", "Строка черепашьих команд"),
                    )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_IF_FIGURE,
            listOf(
                HelpData(
                    "(expresion)(@program (args?))",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.5 будет рисовать фигуру иначе нет"
                        ),
                        HelpDataParam("@program", "назвиние програмы рисования фигур"),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где"
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через ."
                        ),
                        HelpDataParam("value", "значение подставляемое всместо переменной"),
                    )
                ),
                HelpData(
                    "(expression)(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.5 будет рисовать фигуру иначе нет"
                        ),
                        HelpDataParam("tortoise", "Строка черепашьих команд"),
                    )
                )
            )
        ), helpName(
            TortoiseCommand.TURTOISE_LOOP,
            "c commands* <",
            "выполнить c раз команды между > <"
        ), helpName(
            TortoiseCommand.TURTOISE_MEMORY_ASSIGN,
            "var arg*",
            "присвоить переменной var сумму значений arg"
        ),
        helpName(' ', "@var", "подставить значение переменной var"),
        helpName(
            TortoiseCommand.TURTOISE_UNION,
            listOf(
                HelpData(
                    "(figure1)(fiugre2)",
                    "Объединение мнгогоугольников. Многоугольники задаются также как и при рисовании фигур",
                    listOf(
                        HelpDataParam(
                            "figure1",
                            "Первый многоугольник для объединения"
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для объединения"
                        ),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_INTERSECT,
            listOf(
                HelpData(
                    "(figure1)(fiugre2)",
                    "Пересечение мнгогоугольников. Многоугольники задаются также как и при рисовании фигур",
                    listOf(
                        HelpDataParam(
                            "figure1",
                            "Первый многоугольник для пересечения"
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для пересечения"
                        ),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_DIFF,
            listOf(
                HelpData(
                    "(figure1)(fiugre2)",
                    "Разность мнгогоугольников. Многоугольники задаются также как и при рисовании фигур",
                    listOf(
                        HelpDataParam(
                            "figure1",
                            "Первый многоугольник для разности"
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для разности"
                        ),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_SYMDIFF,
            listOf(
                HelpData(
                    "(figure1)(fiugre2)",
                    "Симметричная разность мнгогоугольников. Многоугольники задаются также как и при рисовании фигур",
                    listOf(
                        HelpDataParam(
                            "figure1",
                            "Первый многоугольник для разности"
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для разности"
                        ),
                    )
                )
            )
        )
    )
}