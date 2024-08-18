package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_3
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_COLOR
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import turtoise.SplashMap
import turtoise.TortoiseCommand
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParser

class TortoiseHelpInfo : SimpleHelpInfo() {

    fun helpName(text: Char, description: String): HelpInfoCommand {
        return HelpInfoCommand(
            "$text", listOf(
                HelpData("", description)
            )
        )
    }

    fun helpName(text: Char, args: List<HelpData>, description: String = ""): HelpInfoCommand {
        return HelpInfoCommand("$text", args, description)
    }

    override val name: String = ""
    override val title: AnnotatedString = TortoiseParser.helpTitle("Команды черепашки")

    val tortleList =
        listOf<HelpInfoCommand>(

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
            ),
            "Задать текущую координату"
        ),
        helpName(
            TortoiseCommand.TURTOISE_ANGLE,
            listOf(
                HelpData(
                    "a",
                    "повернуть направление движения на угол a",
                    listOf(
                        HelpDataParam("a", "угол в градусах", FIELD_ANGLE),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движенияе в направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x", FIELD_2),
                        HelpDataParam(
                            "y",
                            "координата радиус-вектора расстояние по оси y",
                            FIELD_NONE
                        ),
                    )
                )
            ),
            "Задать направление движения"
        ),
        helpName(
            TortoiseCommand.TURTOISE_ANGLE_ADD,
            listOf(
                HelpData(
                    "a",
                    "повернуть направление движения относительно текущего угла на угол a",
                    listOf(
                        HelpDataParam("a", "угол в градусах", FIELD_ANGLE),
                    )
                ),
                HelpData(
                    "x y",
                    "повернуть направление движения относительно текущего угла на направлении радиус-вектора x y",
                    listOf(
                        HelpDataParam("x", "координата радиус-вектора по оси x", FIELD_2),
                        HelpDataParam(
                            "y",
                            "координата радиус-вектора расстояние по оси y",
                            FIELD_NONE
                        ),
                    )
                )
            ),
            "Повернуть направление движения относительно текущего угла"
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
            "Закрыть многоугольник"
        ),
        helpName(
            TortoiseCommand.TURTOISE_LINE,
            listOf(
                HelpData(
                    "d+",
                    "Прямая длиной d. Последующие значения ресуют перпендикулярно",
                    listOf(
                        HelpDataParam("d", "Длина прямой"),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.oneOrMore(
                            "1",
                            TPArg("d", FIELD_1)
                        )
                    )
                ),

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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.oneOrMore(
                            "1",
                            TPArg("xy", FIELD_2),
                        )
                    )
                )
            ),
            "Полилиния по координатам"
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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.oneOrMore(
                            "1",
                            TPArg("d", FIELD_1),
                            TPArg("a", FIELD_ANGLE)
                        )
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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("r", FIELD_1),
                        TPArg.multi(
                            "arc",
                            TPArg("sa", FIELD_ANGLE),
                            TPArg("wa", FIELD_ANGLE),
                        )
                    )
                ),

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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("r", FIELD_2),
                        TPArg.multi(
                            "arc",
                            TPArg("sa", FIELD_ANGLE),
                            TPArg("wa", FIELD_ANGLE),
                        )
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
                        HelpDataParam(
                            "h",
                            "Высота прямоугольника. Если не задан, то квадрат",
                            FIELD_NONE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.oneOrMore(
                            "rect",
                            TPArg("wh", FIELD_2),
                        )
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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("wh", FIELD_2),
                        TPArg("r", FIELD_1),
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
                    creator = TPArg.create(
                        "",
                        TPArg("w", FIELD_1),

                        TPArg("delta", FIELD_1),
                        TPArg("zigWidth", FIELD_1),
                        TPArg("board", FIELD_1),

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
                    creator = TPArg.create(
                        "",
                        TPArg("w", FIELD_1),
                        TPArg.block(
                            TPArg("delta", FIELD_1),
                            TPArg("zigWidth", FIELD_1),
                            TPArg("board", FIELD_1),
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
                            "координата x первой контрольной точки относительно начала кривой",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ty1",
                            "координата y первой контрольной точки относительно начала кривой",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "tx2",
                            "координата x второй контрольной точки относительно начала кривой",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ty2",
                            "координата y второй контрольной точки относительно начала кривой",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "ex",
                            "координата x конца кривой относительно начала кривой",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ey",
                            "координата y конца кривой относительно начала кривой",
                            FIELD_NONE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("t1", FIELD_2),
                        TPArg("t2", FIELD_2),
                        TPArg("e", FIELD_2)
                    )
                )
            )
        ),
        helpName(
            TortoiseCommand.TURTOISE_BEZIER_AT_POINTS,
            listOf(
                HelpData(
                    "tx1 ty1 tx2 ty2 ex ey *",
                    "Рисовать линию безье из текущей позиции по указанным координатам",
                    listOf(
                        HelpDataParam(
                            "tx1",
                            "координата x первой контрольной точки",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ty1",
                            "координата y первой контрольной точки",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "tx2",
                            "координата x второй контрольной точки",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ty2",
                            "координата y второй контрольной точки",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "ex",
                            "координата x конца кривой",
                            FIELD_2
                        ),
                        HelpDataParam(
                            "ey",
                            "координата y конца кривой",
                            FIELD_NONE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("t1", FIELD_2),
                        TPArg("t2", FIELD_2),
                        TPArg("e", FIELD_2)
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
                        HelpDataParam("program", "название програмы рисования фигур", FIELD_FIGURE),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через .",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "value",
                            "значение подставляемое всместо переменной",
                            FIELD_NONE
                        ),
                    )
                ),
                HelpData(
                    "(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam("tortoise", "Строка черепашьих команд", FIELD_TEXT),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.block(
                            TPArg("tortoise", FIELD_TEXT)
                        )
                    )
                )
            ),
            "Фигура"
        ),
        helpName(
            TortoiseCommand.TURTOISE_IF_FIGURE,
            listOf(
                HelpData(
                    "(expresion)(figure)",
                    "Рисовать фигуру которая записана в строке начинающейся с @program\n",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.0 будет рисовать фигуру иначе нет",
                            FIELD_TEXT
                        ),
                        HelpDataParam("figure", "фигура", FIELD_FIGURE),
                        HelpDataParam(
                            "args",
                            "ноль или несколько значений которые передаются фигуре\n каждое значение записывается как (name value) где",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "name",
                            "название переменной обращение в описании фиугры к которой происходит через .",
                            FIELD_NONE
                        ),
                        HelpDataParam(
                            "value",
                            "значение подставляемое всместо переменной",
                            FIELD_NONE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.block(TPArg("expression", FIELD_TEXT)),
                        TPArg.figure("figure"),
                    )
                ),
                HelpData(
                    "(expression)(tortoise)",
                    "Рисовать фигуру которая записана в строке по правилам черепашки",
                    listOf(
                        HelpDataParam(
                            "expression",
                            "Если значение вычисления выражение больше 0.5 будет рисовать фигуру иначе нет",
                            FIELD_TEXT
                        ),
                        HelpDataParam("tortoise", "Строка черепашьих команд", FIELD_TEXT),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.block(TPArg("expression", FIELD_TEXT)),
                        TPArg.block(TPArg("tortoise", FIELD_TEXT))
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
                    "Рисовать фигуры f вдоль кривой path",
                    listOf(
                        HelpDataParam(
                            "path",
                            "Путь вдоль которого рисуем фигуры", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "edge",
                            "Номер стороны в пути path начинается с 0",
                            FIELD_INT
                        ),
                        HelpDataParam(
                            "delta",
                            "расстояние от начала линии в диапазоне от 0 до 1 (конец линии).\n Может быть несколько"
                        ),
                        HelpDataParam(
                            "f",
                            "Фигура рисуемая в указанной позиции (задаётся по правилам рисования фигур)",
                            FIELD_FIGURE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.figure("path"),
                        TPArg.block(TPArg("edge", FIELD_INT), TPArg("delta", FIELD_1)),
                        TPArg.figure("f"),
                    )
                ),
            ),
            "Расположить фигуры вдоль кривой"
        ),

        helpName(
            TortoiseCommand.TURTOISE_PATH,
            listOf(
                HelpData(
                    "(path) (count distance offset angle normal px py reverse) (figure)",
                    "Рисовать фигуры вдоль пути\n",
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
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.figure("path"),
                        TPArg.block(
                            TPArg("count", FIELD_INT),
                            TPArg("distance", FIELD_1),
                            TPArg("offset", FIELD_1),
                            TPArg("angle", FIELD_ANGLE),
                            TPArg("normal", FIELD_CHECK),
                            TPArg("p", FIELD_2),
                            TPArg("reverse", FIELD_CHECK),
                        ),
                        TPArg.figure("figure"),
                    )
                ),
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
                        HelpDataParam("commands", "команды черепашки", FIELD_TEXT)
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg("c", FIELD_INT),
                        TPArg("commands", FIELD_TEXT),
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
                    argument = "var arg*",
                    description = "присвоить переменной var сумму значений arg",
                    params = listOf(
                        HelpDataParam("var", "Название переменной", FIELD_TEXT),
                        HelpDataParam(
                            "arg",
                            "Значение которое будет сохранено в переменную",
                            FIELD_TEXT
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.text("var"),
                        TPArg.text("arg"),
                    )
                )
            ),
            "Присвоение"
        ),
        helpName(
            '@',
            listOf(
                HelpData(
                    argument = "var",
                    description = "подставить значение переменной var. Пишется слитно",
                    params = listOf(
                        HelpDataParam("var", "Название переменной", FIELD_TEXT),
                    ),
                    creator = TPArg.create("", TPArg.text("var"))
                )
            ),
            "Подстановка значения переменной"
        ),
        helpName(
            TortoiseCommand.TURTOISE_UNION,
            listOf(
                HelpData(
                    argument = "(figure1)(fiugre2)",
                    description = "Объединение мнгогоугольников. Многоугольники задаются также как и при рисовании фигур",
                    params = listOf(
                        HelpDataParam(
                            "figure1",
                            "Первый многоугольник для объединения", FIELD_FIGURE
                        ),
                        HelpDataParam(
                            "figure2",
                            "Второй многоугольник для объединения", FIELD_FIGURE
                        ),
                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.figure("figure1"),
                        TPArg.figure("figure2"),
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
                    ),
                    creator = TPArg.create(
                            "",
                    TPArg.figure("figure1"),
                    TPArg.figure("figure2"),
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
                    ),
                    creator = TPArg.create(
                            "",
                    TPArg.figure("figure1"),
                    TPArg.figure("figure2"),
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

                    ),
                    creator = TPArg.create(
                        "",
                        TPArg.figure("figure1"),
                        TPArg.figure("figure2"),
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
                            "Индекс цвета в таблице dxf", FIELD_COLOR
                        ),
                        HelpDataParam(
                            "figure",
                            "Фигура рисуется по правилам фигур", FIELD_FIGURE
                        ),
                    ),creator = TPArg.create(
                        "",
                        TPArg("color",FIELD_COLOR),
                        TPArg.figure("figure"),
                    )

                )
            ),
            "Цвет для фигуры"
        ),
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
                creator = TPArg.create(
                    "",
                    TPArg.figure("figure"),
                    TPArg.block(
                        TPArg.item("c", TPArg("c", FIELD_2)),
                        TPArg.item("r", TPArg("r", FIELD_2)),
                        TPArg.item("s", TPArg("s", FIELD_2)),
                        TPArg.item("m", TPArg("m", FIELD_2)),
                    )
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
                creator = TPArg.create(
                    "",
                    TPArg.block(TPArg("xyz", FIELD_3)),
                    TPArg.block(
                        TPArg("ax", FIELD_ANGLE),
                        TPArg("ay", FIELD_ANGLE),
                        TPArg("az", FIELD_ANGLE)
                    ),
                    TPArg.figure("figure"),
                    TPArg.block(
                        TPArg.item("c", TPArg("c", FIELD_2)),
                        TPArg.item("r", TPArg("r", FIELD_2)),
                        TPArg.item("s", TPArg("s", FIELD_2)),
                        TPArg.item("m", TPArg("m", FIELD_2)),
                    )
                )
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
                        "Измеряемая фигура", FIELD_FIGURE
                    ),
                    HelpDataParam(
                        "variable",
                        "Название переменной", FIELD_TEXT
                    ),
                ),
                creator = TPArg.create("length",
                    TPArg.figure("figure"),
                    TPArg.block(
                        TPArg.oneOrMore("varBlock",
                            TPArg("variable", FIELD_TEXT),
                        )
                    )
                )
            ),
            HelpData(
                "board (variable)",
                "поместить толщину доски в переменную variable",
                listOf(
                    HelpDataParam(
                        "variable",
                        "Название переменной", FIELD_TEXT
                    ),
                ),
                creator = TPArg.create("board",
                    TPArg.block(
                        TPArg("variable", FIELD_TEXT),
                    )
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
                ),
                creator = TPArg.create("pos",
                    TPArg.block(
                        TPArg("varX", FIELD_TEXT),
                        TPArg("varY", FIELD_TEXT),
                        TPArg("varA", FIELD_TEXT),
                    )
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

    override val commandList = listOf(
        helpForSplash(),
        helpForVariablesSplash(),
        helpInfo3dTransform(),
        helpForArray(),
    )+tortleList
}