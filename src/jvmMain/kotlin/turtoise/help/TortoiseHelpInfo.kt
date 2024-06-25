package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import turtoise.SplashMap
import turtoise.TortoiseCommand
import turtoise.parser.TortoiseParser

class TortoiseHelpInfo : SimpleHelpInfo() {

    fun helpName(text: Char, description: String): HelpInfoCommand {
        return HelpInfoCommand(
            "$text", listOf(
                HelpData("", description)
            )
        )
    }

    fun helpName(text: Char, args: List<HelpData>): HelpInfoCommand {
        return HelpInfoCommand("$text", args)
    }

    override val name: String = ""
    override val title: AnnotatedString = TortoiseParser.helpTitle("Команды черепашки")
    override val commandList = listOf<HelpInfoCommand>(
        helpName(
            TortoiseCommand.TURTOISE_MOVE,
            listOf(
                HelpData(
                    "x y a xa ya",
                    "переместить позицию относительно текущей",
                    listOf(
                        HelpDataParam("x", "расстояние по оси x"),
                        HelpDataParam("y", "расстояние по оси y"),
                        HelpDataParam("a", "поворот в градусах"),
                        HelpDataParam("xa", "расстояние по оси x после поворота"),
                        HelpDataParam("ya", "расстояние по оси y после поворота"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_MOVE_TO,
            listOf(
                HelpData(
                    "x y",
                    "поставить позицию в указанных координатах",
                    listOf(
                        HelpDataParam("x", "координата по оси x"),
                        HelpDataParam("y", "координата по оси y"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ANGLE,
            listOf(
                HelpData(
                    "a",
                    "повернуть направление движение на угол a",
                    listOf(
                        HelpDataParam("a", "угол в градусах"),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движение в направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x"),
                        HelpDataParam("y", "координата радиус-вектора расстояние по оси y"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ANGLE_ADD,
            listOf(
                HelpData(
                    "a",
                    "повернуть направление движение относительно текущего угла на угол a",
                    listOf(
                        HelpDataParam("a", "угол в градусах"),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движение относительно текущего угла на направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x"),
                        HelpDataParam("y", "координата радиус-вектора расстояние по оси y"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_SPLIT,
            "Завершить рисование текущей фигуры и начать новую"
        ),
        helpName(
            TortoiseCommand.TURTOISE_CLEAR,
            "Сбросить позицию на начало координат и поворот на 0"
        ),
        helpName(
            TortoiseCommand.TURTOISE_CLOSE,
            "закрыть многоугольник"
        ),
        helpName(
            TortoiseCommand.TURTOISE_LINE,
            listOf(
                HelpData(
                    "d+",
                    "Прямая длиной d. Последующие значения ресуют перпендикулярно",
                    listOf(
                        HelpDataParam("d", "Длина прямой"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_POLYLINE,
            listOf(
                HelpData(
                    "x y +",
                    "Построение полилинии по координатам относительно текущей",
                    listOf(
                        HelpDataParam("x", "координата точки угла по оси x"),
                        HelpDataParam("y", "координата точки угла по оси y"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_LINE_WITH_ANGLE,
            listOf(
                HelpData(
                    "d a d +",
                    "Построение полилинии задаётся длина линии и угол между ними",
                    listOf(
                        HelpDataParam("d", "Длина стороны"),
                        HelpDataParam("a", "Угол поворта для следующей стороны"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_VERTICAL,
            listOf(
                HelpData(
                    "y",
                    "вертикальная прямая длиной y",
                    listOf(
                        HelpDataParam("y", "Длина линии"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_HORIZONTAL,
            listOf(
                HelpData(
                    "x",
                    "горизонтальная прямая длиной x",
                    listOf(
                        HelpDataParam("x", "Длина линии"),
                    )
                )
            )

        ),
        helpName(
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
        ),
        helpName(
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
        ),
        helpName(
            TortoiseCommand.TURTOISE_ARC,
            listOf(
                HelpData(
                    "r angle circle?",
                    "Дуга из текущей точки радиуса r заданной угла",
                    listOf(
                        HelpDataParam(
                            "r",
                            "радиус дуги, если отрицательный то закруглении в другом направлении"
                        ),
                        HelpDataParam("angle", "угол дуги"),
                        HelpDataParam("circle", "Добавить окружность в центре дуги"),
                    )
                )
            )
        ),


        helpName(
            TortoiseCommand.TURTOISE_RECTANGLE,
            listOf(
                HelpData(
                    "w h?",
                    "Прямоугольник шириной w и высотой h. Если h не задан, то квадрат",
                    listOf(
                        HelpDataParam("w", "Ширина прямоугольника"),
                        HelpDataParam("h", "Высота прямоугольника. Если не задан, то квадрат"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ROUND_RECTANGLE,
            listOf(
                HelpData(
                    "w h r",
                    "Прямоугольник шириной w и высотой h cо скруглённми углами радиуса r",
                    listOf(
                        HelpDataParam("w", "Ширина прямоугольника"),
                        HelpDataParam("h", "Высота прямоугольника"),
                        HelpDataParam("r", "радиус кругления углов"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_REGULAR_POLYGON,
            listOf(
                HelpData(
                    "n r",
                    "Многоугольник радиуса r с числом сторон n",
                    listOf(
                        HelpDataParam("n", "числом сторон"),
                        HelpDataParam("r", "радиус"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ZIGZAG,
            listOf(
                HelpData(
                    "w delta zigWidth board",
                    "Зигзаги",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одноо зигзага"),
                        HelpDataParam("board", "толщина доски"),

                        )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ZIGZAG_FIGURE,
            listOf(
                HelpData(
                    "w delta zigWidth board (figure (args)?)",
                    "Зигзаги формы описаной в строке figure",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одноо зигзага"),
                        HelpDataParam("board", "толщина доски"),
                        HelpDataParam("(figure (args)?)", "фигура для формы зигзага"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_BEZIER,
            listOf(
                HelpData(
                    "(tx1 ty1 tx2 ty2 ex ey)*",
                    "Рисовать линию безье из текущей позиции",
                    listOf(
                        HelpDataParam(
                            "tx1",
                            "координата x первой контрольной точки относительно начала кривой"
                        ),
                        HelpDataParam(
                            "ty1",
                            "координата y первой контрольной точки относительно начала кривой"
                        ),
                        HelpDataParam(
                            "tx2",
                            "координата x второй контрольной точки относительно начала кривой"
                        ),
                        HelpDataParam(
                            "ty2",
                            "координата y второй контрольной точки относительно начала кривой"
                        ),
                        HelpDataParam("ex", "координата x конца кривой относительно начала кривой"),
                        HelpDataParam("ey", "координата y конца кривой относительно начала кривой"),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_FIGURE,
            listOf(
                HelpData(
                    "(@program args?)",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam("@program", "название програмы рисования фигур"),
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
        ),
        helpName(
            TortoiseCommand.TURTOISE_IF_FIGURE,
            listOf(
                HelpData(
                    "(expresion)(@figure (args?))",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.0 будет рисовать фигуру иначе нет"
                        ),
                        HelpDataParam("@program", "название програмы рисования фигур"),
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
        ),
        helpName(
            TortoiseCommand.TURTOISE_GROUP,
            listOf(
                HelpData(
                    "(path) (edge delta*) (f) + ",
                    "Рисовать фигуры f в доль кривой path",
                    listOf(
                        HelpDataParam(
                            "path",
                            "Пуь в доль которого рисуем фигуры"
                        ),
                        HelpDataParam("edge", "Номер стороны в путь path начинается с 0"),
                        HelpDataParam(
                            "delta",
                            "расстояние от начала линии в дмапазоне от 0 до 1 (конец линии).\n Может бть несколько"
                        ),
                        HelpDataParam(
                            "f",
                            "Фиггура рисуемая в указанной позиции (здаётся по правилам рисования фигур)"
                        ),
                    )
                ),
            )
        ),

        helpName(
            TortoiseCommand.TURTOISE_PATH,
            listOf(
                HelpData(
                    "(path) (count distance offset angle normal px py reverse) (figure)",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam(
                            "path",
                            "фигура вдоль которой будут располагться другие фигуры"
                        ),
                        HelpDataParam(
                            "count",
                            "Количество фигкур вдоль пути"
                        ),
                        HelpDataParam(
                            "distance",
                            "Дистанция между копиями от 0 до 1.0 "
                        ),
                        HelpDataParam(
                            "offset",
                            "Сдвиг относительно начала пути первой копии от 0 до 1.0"
                        ),
                        HelpDataParam(
                            "angle",
                            "Угол расположения элементов"
                        ),
                        HelpDataParam(
                            "normal",
                            "Располагать копии по нормали относительно к пути"
                        ),
                        HelpDataParam(
                            "px",
                            "Точка вращения копии по оси x"
                        ),
                        HelpDataParam(
                            "py",
                            "Точка вращения копии по оси y"
                        ),
                        HelpDataParam(
                            "reverse",
                            "Располагать копии в обратном порядке"
                        ),
                        HelpDataParam(
                            "figure",
                            "Фигура, копии которой распологаются вдоль пути"
                        ),
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
        ),
        helpName(
            TortoiseCommand.TURTOISE_LOOP,
            listOf(
                HelpData(
                    "c commands* <",
                    "выполнить c раз команды между > <"
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_MEMORY_ASSIGN,
            listOf(
                HelpData(
                    "var arg*",
                    "присвоить переменной var сумму значений arg",
                    listOf(
                        HelpDataParam("var", "Название переменной"),
                        HelpDataParam("arg", "Значение которое будет сохранено в переменную"),
                    )
                )
            )
        ),
        helpName(
            '@',
            listOf(
                HelpData(
                    "var", "подставить значение переменной var. Пишется слитно"
                )
            )
        ),
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
        ),
        helpName(
            TortoiseCommand.TURTOISE_COLOR,
            listOf(
                HelpData(
                    "color (fiugre)",
                    "Раскрасить фигуру цветом color",
                    listOf(
                        HelpDataParam(
                            "color",
                            "Индекс цвета в таблице dxf"
                        ),
                        HelpDataParam(
                            "figure",
                            "Фигура рисуется по правилам фигур"
                        ),
                    )
                )
            )
        ),

        helpInfo3dTransform(),
        helpForArray(),
        helpForSplash(),
        helpForVariablesSplash(),

        )

    private fun helpForArray() = helpName(
        TortoiseCommand.TURTOISE_ARRAY,
        listOf(
            HelpData(
                "(figure)((c *)(r *)(s * *)(m * *))",
                "Нарисовать массив фигур figure",
                listOf(
                    HelpDataParam(
                        "figure",
                        "Фигура рисуется по правилам фигур"
                    ),
                    HelpDataParam(
                        "c * *",
                        "Количество повторений по оси x и дистанция"
                    ),
                    HelpDataParam(
                        "r * *",
                        "Количество повторений по оси y и дистанция"
                    ),
                    HelpDataParam(
                        "s * *",
                        "Масштабирование по осям x и y"
                    ),
                    HelpDataParam(
                        "m * *",
                        "Начальная точка по осям x и y"
                    ),
                )
            )
        )
    )

    private fun helpInfo3dTransform() = helpName(
        TortoiseCommand.TURTOISE_3D,
        listOf(
            HelpData(
                "(x y z)(ax ay az)(figure)((c *)(r *)(s * *)(m * *))?",
                "Нарисовать фигуру с трёхмерной трансформацией",
                listOf(
                    HelpDataParam(
                        "figure",
                        "Фигура рисуется по правилам фигур"
                    ),
                    HelpDataParam(
                        "x",
                        "Сдвиг по оси x до вращения"
                    ),
                    HelpDataParam(
                        "y",
                        "Сдвиг по оси y до вращения"
                    ),
                    HelpDataParam(
                        "z",
                        "Сдвиг по оси z до вращения"
                    ),
                    HelpDataParam(
                        "ax",
                        "Поворот вокруг оси x в градусах"
                    ),
                    HelpDataParam(
                        "ay",
                        "Поворот вокруг оси y в градусах"
                    ),
                    HelpDataParam(
                        "az",
                        "Поворот вокруг оси z в градусах"
                    ),
                    HelpDataParam(
                        "c * *",
                        "Количество повторений по оси x и дистанция"
                    ),
                    HelpDataParam(
                        "r * *",
                        "Количество повторений по оси y и дистанция"
                    ),
                    HelpDataParam(
                        "s * *",
                        "Масштабирование по осям x и y"
                    ),
                    HelpDataParam(
                        "m * *",
                        "Начальная точка по осям x и y"
                    ),
                )
            )
        )
    )

    private fun helpForVariablesSplash() = helpName(
        TortoiseCommand.TURTOISE_VARIABLES,
        listOf(
            HelpData(
                "length (figure) (variable+)",
                "поместить длину каждого пути figure в переменные variable"
            ),
            HelpData(
                "board (variable)",
                "поместить толщину доски в переменную variable"
            ),
            HelpData(
                "pos (x y a)",
                "поместить текущую позицию и поворот в переменные x y a соответственно"
            ),
            HelpData(
                "r",
                "Включить опцию рисования зигзагов в обратном направлении"
            ),
            HelpData(
                "f",
                "Включить опцию рисования зигзагов в прямом направлении"
            ),
        )
    )

    private fun helpForSplash() = helpName(
        TortoiseCommand.TURTOISE_SPLASH,
        SplashMap.splashList.map { it.help() }

    )
}