package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_3
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import turtoise.SplashMap
import turtoise.TortoiseCommand
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock

class TortoiseHelpInfo : SimpleHelpInfo() {

    fun helpName(text: Char, description: String): HelpInfoCommand {
        return HelpInfoCommand(
            "$text", listOf(
                HelpData("", description)
            )
        )
    }

    fun helpName(text: Char, args: List<HelpData>, description:String = ""): HelpInfoCommand {
        return HelpInfoCommand("$text", args, description)
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
                        HelpDataParam("x", "расстояние по оси x", FIELD_2),
                        HelpDataParam("y", "расстояние по оси y", FIELD_NONE),
                        HelpDataParam("a", "поворот в градусах", FIELD_ANGLE),
                        HelpDataParam("xa", "расстояние по оси x после поворота", FIELD_2),
                        HelpDataParam("ya", "расстояние по оси y после поворота", FIELD_NONE),
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
                        HelpDataParam("x", "координата по оси x", FIELD_2),
                        HelpDataParam("y", "координата по оси y", FIELD_NONE),
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
                        HelpDataParam("a", "угол в градусах", FIELD_ANGLE),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движение в направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x", FIELD_2),
                        HelpDataParam("y", "координата радиус-вектора расстояние по оси y", FIELD_NONE),
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
                        HelpDataParam("a", "угол в градусах", FIELD_ANGLE),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движение относительно текущего угла на направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x", FIELD_2),
                        HelpDataParam("y", "координата радиус-вектора расстояние по оси y", FIELD_NONE),
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
            ),
            "Прямая линия"
        ),
        helpName(
            TortoiseCommand.TURTOISE_POLYLINE,
            listOf(
                HelpData(
                    "x y +",
                    "Построение полилинии по координатам относительно текущей",
                    listOf(
                        HelpDataParam("x", "координата точки угла по оси x", FIELD_2),
                        HelpDataParam("y", "координата точки угла по оси y", FIELD_NONE),
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
                        HelpDataParam("a", "Угол поворта для следующей стороны", FIELD_ANGLE),
                    )
                )
            ),
            "Построение полилинии"
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
            ),
            "вертикальная прямая"

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
            ),
            "горизонтальная прямая"

        ),
        helpName(
            TortoiseCommand.TURTOISE_CIRCLE,
            listOf(
                HelpData(
                    "r (sa wa)*",
                    "круг радиуса r",
                    listOf(
                        HelpDataParam("r", "радиус"),
                        HelpDataParam("sa", "Задаёт начало дуги в градусах", FIELD_ANGLE),
                        HelpDataParam("wa", "Задаёт длину дуги в градусах", FIELD_ANGLE),
                    )
                )
            ),
            "круг"
        ),
        helpName(
            TortoiseCommand.TURTOISE_ELLIPSE,
            listOf(
                HelpData(
                    "rx ry (sa wa)*",
                    "эллипс с радиусами rx и ry",
                    listOf(
                        HelpDataParam("rx", "радиус по оси x", FIELD_2),
                        HelpDataParam("ry", "радиус по оси y", FIELD_NONE),
                        HelpDataParam("sa", "Задаёт начало дуги в градусах", FIELD_ANGLE),
                        HelpDataParam("wa", "Задаёт длину дуги в градусах", FIELD_ANGLE),
                    )
                )
            ),
            "эллипс"
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
                        HelpDataParam("angle", "угол дуги", FIELD_ANGLE),
                        HelpDataParam("circle", "Добавить окружность в центре дуги"),
                    )
                )
            ),
            "Дуга"
        ),


        helpName(
            TortoiseCommand.TURTOISE_RECTANGLE,
            listOf(
                HelpData(
                    "w h?",
                    "Прямоугольник шириной w и высотой h. Если h не задан, то квадрат",
                    listOf(
                        HelpDataParam("w", "Ширина прямоугольника", FIELD_2),
                        HelpDataParam("h", "Высота прямоугольника. Если не задан, то квадрат", FIELD_NONE),
                    )
                )
            ),
            "Прямоугольник"
        ),
        helpName(
            TortoiseCommand.TURTOISE_ROUND_RECTANGLE,
            listOf(
                HelpData(
                    "w h r",
                    "Прямоугольник шириной w и высотой h cо скруглённми углами радиуса r",
                    listOf(
                        HelpDataParam("w", "Ширина прямоугольника", FIELD_2),
                        HelpDataParam("h", "Высота прямоугольника", FIELD_NONE),
                        HelpDataParam("r", "радиус кругления углов"),
                    )
                )
            ),
            "Прямоугольник cо скруглённми углами"
        ),
        helpName(
            TortoiseCommand.TURTOISE_REGULAR_POLYGON,
            listOf(
                HelpData(
                    "n r",
                    "Правильный многоугольник радиуса r с числом сторон n",
                    listOf(
                        HelpDataParam("n", "числом сторон", FIELD_INT),
                        HelpDataParam("r", "радиус"),
                    )
                )
            ),
            "Правильный многоугольник"
        ),
        helpName(
            TortoiseCommand.TURTOISE_ZIGZAG,
            listOf(
                HelpData(
                    "w (delta zigWidth board)",
                    "Зигзаги",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одноо зигзага"),
                        HelpDataParam("board", "толщина доски"),

                        ),
                    creator = TPArg.create("",
                        TPArg("w"),

                        TPArg("delta"),
                        TPArg("zigWidth"),
                        TPArg("board"),

                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_ZIGZAG_FIGURE,
            listOf(
                HelpData(
                    "w (delta zigWidth board) (figure)",
                    "Зигзаги формы описаной в строке figure",
                    listOf(
                        HelpDataParam("w", "общая длина"),
                        HelpDataParam("delta", "расстояние между началами двух зигзагов"),
                        HelpDataParam("zigWidth", "длина одного зигзага"),
                        HelpDataParam("board", "толщина доски"),
                        HelpDataParam("figure", "фигура для формы зигзага", FIELD_FIGURE),
                    ),
                    creator = TPArg.create("",
                        TPArg("w"),
                        TPArg.block(
                            TPArg("delta"),
                            TPArg("zigWidth"),
                            TPArg("board"),
                        ),
                        TPArg.figure("figure")
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_BEZIER,
            listOf(
                HelpData(
                    "tx1 ty1 tx2 ty2 ex ey *",
                    "Рисовать линию безье из текущей позиции",
                    listOf(
                        HelpDataParam(
                            "tx1",
                            "координата x первой контрольной точки относительно начала кривой", FIELD_2
                        ),
                        HelpDataParam(
                            "ty1",
                            "координата y первой контрольной точки относительно начала кривой", FIELD_NONE
                        ),
                        HelpDataParam(
                            "tx2",
                            "координата x второй контрольной точки относительно начала кривой", FIELD_2
                        ),
                        HelpDataParam(
                            "ty2",
                            "координата y второй контрольной точки относительно начала кривой", FIELD_NONE
                        ),
                        HelpDataParam("ex", "координата x конца кривой относительно начала кривой", FIELD_2),
                        HelpDataParam("ey", "координата y конца кривой относительно начала кривой", FIELD_NONE),
                    ),
                    creator = TPArg.create("", TPArg("tx1"), TPArg("tx2"),TPArg("ex"))
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
                        HelpDataParam("@program", "название програмы рисования фигур", FIELD_FIGURE),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где", FIELD_NONE
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через .", FIELD_NONE
                        ),
                        HelpDataParam("value", "значение подставляемое всместо переменной", FIELD_NONE),
                    )
                ),
                HelpData(
                    "(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam("tortoise", "Строка черепашьих команд", FIELD_TEXT),
                    )
                )
            ),
            "Фигура"
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
                            "Если значение вычисления выражение больше 0.0 будет рисовать фигуру иначе нет", FIELD_TEXT
                        ),
                        HelpDataParam("@program", "название програмы рисования фигур", FIELD_FIGURE),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где", FIELD_NONE
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через .", FIELD_NONE
                        ),
                        HelpDataParam("value", "значение подставляемое всместо переменной", FIELD_NONE),
                    )
                ),
                HelpData(
                    "(expression)(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.5 будет рисовать фигуру иначе нет", FIELD_TEXT
                        ),
                        HelpDataParam("tortoise", "Строка черепашьих команд", FIELD_TEXT),
                    )
                )
            ),
            "Условие для рисования фигуры"
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
                            "Пуь в доль которого рисуем фигуры", FIELD_FIGURE
                        ),
                        HelpDataParam("edge", "Номер стороны в путь path начинается с 0"),
                        HelpDataParam(
                            "delta",
                            "расстояние от начала линии в дмапазоне от 0 до 1 (конец линии).\n Может бть несколько"
                        ),
                        HelpDataParam(
                            "f",
                            "Фигура рисуемая в указанной позиции (здаётся по правилам рисования фигур)", FIELD_FIGURE
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
                            "фигура вдоль которой будут располагться другие фигуры", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "count",
                            "Количество фигкур вдоль пути", FIELD_INT
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
                            "Угол расположения элементов", FIELD_ANGLE
                        ),
                        HelpDataParam(
                            "normal",
                            "Располагать копии по нормали относительно к пути", FIELD_CHECK
                        ),
                        HelpDataParam(
                            "px",
                            "Точка вращения копии по оси x", FIELD_2
                        ),
                        HelpDataParam(
                            "py",
                            "Точка вращения копии по оси y", FIELD_NONE
                        ),
                        HelpDataParam(
                            "reverse",
                            "Располагать копии в обратном порядке", FIELD_CHECK
                        ),
                        HelpDataParam(
                            "figure",
                            "Фигура, копии которой распологаются вдоль пути", FIELD_FIGURE
                        ),
                    )
                ),
                HelpData(
                    "(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam("tortoise", "Строка черепашьих команд", FIELD_TEXT),
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_LOOP,
            listOf(
                HelpData(
                    "c commands* <",
                    "выполнить c раз команды между > <",
                    listOf(
                        HelpDataParam("c", "количество повторений", FIELD_INT),
                        HelpDataParam("commands", "команды черепашки", FIELD_FIGURE)
                    ),
                    creator = TPArg.create("",
                        TPArg.figure("commands"),
                        TPArg.text("<"),
                    )
                )
            ),
            "Повторение"
        ),
        helpName(
            TortoiseCommand.TURTOISE_MEMORY_ASSIGN,
            listOf(
                HelpData(
                    "var arg*",
                    "присвоить переменной var сумму значений arg",
                    listOf(
                        HelpDataParam("var", "Название переменной", FIELD_TEXT),
                        HelpDataParam("arg", "Значение которое будет сохранено в переменную", FIELD_TEXT),
                    )
                )
            ),
            "Присвоение"
        ),
        helpName(
            '@',
            listOf(
                HelpData(
                    "var", "подставить значение переменной var. Пишется слитно"
                )
            ),
            "Подстановка значения переменной"
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
                            "Первый многоугольник для объединения", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для объединения", FIELD_FIGURE
                        ),
                    )
                )
            ),
            "Объединение мнгогоугольников"
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
                            "Первый многоугольник для пересечения", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для пересечения", FIELD_FIGURE
                        ),
                    )
                )
            ),
            "Пересечение мнгогоугольников"
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
                            "Первый многоугольник для разности", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для разности", FIELD_FIGURE
                        ),
                    )
                )
            ),
            "Разность мнгогоугольников"
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
                            "Первый многоугольник для разности", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для разности", FIELD_FIGURE
                        ),
                    )
                )
            ),
            "Симметричная разность мнгогоугольников"
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
                            "Индекс цвета в таблице dxf", FIELD_INT
                        ),
                        HelpDataParam(
                            "figure",
                            "Фигура рисуется по правилам фигур", FIELD_FIGURE
                        ),
                    )
                )
            ),
            "Цвет для фигуры"
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
                        "Фигура рисуется по правилам фигур", FIELD_FIGURE
                    ),
                    HelpDataParam(
                        "c",
                        "Количество повторений по оси x и дистанция", FIELD_2
                    ),
                    HelpDataParam(
                        "r",
                        "Количество повторений по оси y и дистанция", FIELD_2
                    ),
                    HelpDataParam(
                        "s",
                        "Масштабирование по осям x и y", FIELD_2
                    ),
                    HelpDataParam(
                        "m",
                        "Начальная точка по осям x и y", FIELD_2
                    ),
                ),
                creator = TPArg.create("", TPArg.figure("figure"), TPArg.block(
                    TPArg.item("c",  TPArg("c")),
                    TPArg.item("r",  TPArg("r")),
                    TPArg.item("s",  TPArg("s")),
                    TPArg.item("m",  TPArg("m")),
                ))
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
                        "Фигура рисуется по правилам фигур", FIELD_FIGURE
                    ),
                    HelpDataParam(
                        "x",
                        "Сдвиг по оси x до вращения", FIELD_3
                    ),
                    HelpDataParam(
                        "y",
                        "Сдвиг по оси y до вращения", FIELD_NONE
                    ),
                    HelpDataParam(
                        "z",
                        "Сдвиг по оси z до вращения", FIELD_NONE
                    ),
                    HelpDataParam(
                        "ax",
                        "Поворот вокруг оси x в градусах", FIELD_ANGLE
                    ),
                    HelpDataParam(
                        "ay",
                        "Поворот вокруг оси y в градусах", FIELD_ANGLE
                    ),
                    HelpDataParam(
                        "az",
                        "Поворот вокруг оси z в градусах", FIELD_ANGLE
                    ),
                    HelpDataParam(
                        "c",
                        "Количество повторений по оси x и дистанция", FIELD_2,
                    ),
                    HelpDataParam(
                        "r",
                        "Количество повторений по оси y и дистанция", FIELD_2,
                    ),
                    HelpDataParam(
                        "s",
                        "Масштабирование по осям x и y", FIELD_2
                    ),
                    HelpDataParam(
                        "m",
                        "Начальная точка по осям x и y", FIELD_2
                    ),
                ),
                creator = TPArg.create("",
                    TPArg.block(TPArg("x")),
                    TPArg.block(TPArg("ax"), TPArg("ay"),TPArg("az")),
                    TPArg.figure("figure"),
                    TPArg.block(
                        TPArg.item("c",  TPArg("c")),
                        TPArg.item("r",  TPArg("r")),
                        TPArg.item("s",  TPArg("s")),
                        TPArg.item("m",  TPArg("m")),
                    ))
            )
        )
    )

    private fun helpForVariablesSplash() = helpName(
        TortoiseCommand.TURTOISE_VARIABLES,
        listOf(
            HelpData(
                "length (figure) (variable+)",
                "поместить длину каждого пути figure в переменные variable",
                listOf(
                    HelpDataParam(
                        "figure",
                        "Масштабирование по осям x и y", FIELD_FIGURE
                    ),
                    HelpDataParam(
                        "variable",
                        "Масштабирование по осям x и y", FIELD_TEXT
                    ),
                )
            ),
            HelpData(
                "board (variable)",
                "поместить толщину доски в переменную variable",
                listOf(
                    HelpDataParam(
                        "variable",
                        "Масштабирование по осям x и y", FIELD_TEXT
                    ),
                )
            ),
            HelpData(
                "pos (x y a)",
                "поместить текущую позицию и поворот в переменные x y a соответственно",
                listOf(
                    HelpDataParam(
                        "x",
                        "поместить текущую позицию x", FIELD_TEXT
                    ),
                    HelpDataParam(
                        "y",
                        "поместить текущую позицию y", FIELD_TEXT
                    ),
                    HelpDataParam(
                        "a",
                        "поместить текущий поворот", FIELD_TEXT
                    ),
                )
            ),
            HelpData(
                "r",
                "Включить опцию рисования зигзагов в обратном направлении"
            ),
            HelpData(
                "f",
                "Включить опцию рисования зигзагов в прямом направлении"
            ),
        ),
        "Вычисления и переменные"
    )

    private fun helpForSplash() = helpName(
        TortoiseCommand.TURTOISE_SPLASH,
        SplashMap.splashList.map { it.help() },
        "Действия над фигурами"
    )
}