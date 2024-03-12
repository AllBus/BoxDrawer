package turtoise

import turtoise.TortoiseCommand.Companion.commandToName
import turtoise.memory.TortoiseMemory

interface TortoiseCommand {
    val command: Char
    val size: Int
    fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double
    fun takeBlock(index:Int): TurtoiseParserStackItem? = null

    fun assign(memory: TortoiseMemory) {}

    operator fun get(index: Int, memory: TortoiseMemory) = take(index, 0.0, memory)

    operator fun get(index: Int, defaultValue: Double, memory: TortoiseMemory) = take(index, defaultValue, memory)

    fun value(memory: TortoiseMemory) = take(0, 0.0, memory)

     fun print():String

    companion object {
        fun createFromItem(currentCommand: Char, currentValues: List<TurtoiseParserStackItem>): TortoiseCommand {
            if (currentValues.size == 0) {
                return ZeroTortoiseCommand(currentCommand)
            }
            if (currentValues.size == 1) {
                val v = currentValues[0]
                if (v is TurtoiseParserStackArgument) {
                    return SmallTortoiseCommand(currentCommand, v.argument)
                }
            }
            val b = TurtoiseParserStackBlock(' ')
            b.addItems(currentValues)
            return BlockTortoiseCommand(currentCommand,  b)
        }

        fun create(currentCommand: Char, currentValues: List<String>): TortoiseCommand {
            if (currentValues.size == 0) {
                return ZeroTortoiseCommand(currentCommand)
            }
            if (currentValues.size == 1) {
                currentValues[0].toDoubleOrNull()?.let {
                    DoubleTortoiseCommand(currentCommand, it)
                } ?: SmallTortoiseCommand(currentCommand, currentValues[0])
            }
            return UniTortoiseCommand(currentCommand, currentValues)
        }

        fun commandToName(c:Char):String
        {
            return when (c) {
                TURTOISE_ZIGZAG -> "TURTOISE_ZIGZAG"
                TURTOISE_VERTICAL -> "TURTOISE_VERTICAL"
                TURTOISE_SPLINE -> "TURTOISE_SPLINE"
                TURTOISE_RECTANGLE -> "TURTOISE_RECTANGLE"
                TURTOISE_POLYLINE -> "TURTOISE_POLYLINE"
                TURTOISE_MOVE -> "TURTOISE_MOVE"
                TURTOISE_LINE -> "TURTOISE_LINE"
                TURTOISE_LINE_WITH_ANGLE -> "TURTOISE_LINE_WITH_ANGLE"
                TURTOISE_LINE_PERPENDICULAR -> "TURTOISE_LINE_PERPENDICULAR"
                TURTOISE_HORIZONTAL -> "TURTOISE_HORIZONTAL"
                TURTOISE_ELLIPSE -> "TURTOISE_ELLIPSE"
                TURTOISE_CIRCLE -> "TURTOISE_CIRCLE"
                TURTOISE_BEZIER -> "TURTOISE_BEZIER"
                TURTOISE_ANGLE -> "TURTOISE_ANGLE"
                TURTOISE_ANGLE_ADD -> "TURTOISE_ANGLE_ADD"
                TURTOISE_SPLIT -> "TURTOISE_SPLIT"
                TURTOISE_CLEAR -> "TURTOISE_CLEAR"
                TURTOISE_CLOSE -> "TURTOISE_CLOSE"
                TURTOISE_SAVE -> "TURTOISE_SAVE"
                TURTOISE_LOAD-> "TURTOISE_LOAD"
                TURTOISE_PEEK -> "TURTOISE_PEEK"
                TURTOISE_METHOD_NAME -> "TURTOISE_METHOD_NAME"
              //  TURTOISE_METHOD_RUN -> "TURTOISE_METHOD_RUN"
                TURTOISE_LOOP -> "TURTOISE_LOOP"
                TURTOISE_END_LOOP -> "TURTOISE_END_LOOP"
                TURTOISE_MATRIX_ROTATE -> "TURTOISE_MATRIX_ROTATE"
                TURTOISE_MATRIX_TRANSLATE -> "TURTOISE_MATRIX_TRANSLATE"
                TURTOISE_MATRIX_SCALE -> "TURTOISE_MATRIX_SCALE"
                TURTOISE_MEMORY_ASSIGN -> "TURTOISE_MEMORY_ASSIGN"
                TURTOISE_COLOR -> "TURTOISE_COLOR"
                else -> c.toString()
            }
        }

        const val TURTOISE_ZIGZAG = 'z'
        const val TURTOISE_ZIGZAG_FIGURE = 'Z'
        const val TURTOISE_FIGURE = 'f';
        const val TURTOISE_3D = 'd';
        const val TURTOISE_VERTICAL = 'v';
        const val TURTOISE_SPLINE = 's';
        const val TURTOISE_RECTANGLE = 'x';
        const val TURTOISE_ROUND_RECTANGLE = 'r';
        const val TURTOISE_POLYLINE = 'L';
        const val TURTOISE_MOVE = 'm';
        const val TURTOISE_LINE = 'l';
        const val TURTOISE_LINE_WITH_ANGLE = 'л';
        const val TURTOISE_LINE_PERPENDICULAR = 'p';
        const val TURTOISE_HORIZONTAL = 'h';
        const val TURTOISE_ELLIPSE = 'e';
        const val TURTOISE_CIRCLE = 'c';
        const val TURTOISE_BEZIER = 'b';
        const val TURTOISE_ANGLE = 'a';
        const val TURTOISE_ANGLE_ADD = 'q';
        const val TURTOISE_SPLIT = '|';
        const val TURTOISE_CLEAR = '!';
        const val TURTOISE_CLOSE = '`';
        const val TURTOISE_SAVE = 'Q';
        const val TURTOISE_LOAD = 'W';
        const val TURTOISE_PEEK = 'E';
        const val TURTOISE_METHOD_NAME = '@';
      //  const val TURTOISE_METHOD_RUN = '=';
        const val TURTOISE_LOOP = '>';
        const val TURTOISE_END_LOOP = '<';
        const val TURTOISE_MOVE_TO = 'M';
        const val TURTOISE_MATRIX_ROTATE = 'R';
        const val TURTOISE_MATRIX_TRANSLATE = 'T';
        const val TURTOISE_MATRIX_SCALE = 'S';
        const val TURTOISE_MEMORY_ASSIGN = '='
        const val TURTOISE_COLOR = 'C';

        fun Move(x: Double) = DoubleTortoiseCommand(TURTOISE_MOVE, x)
        fun Move(x: String) = SmallTortoiseCommand(TURTOISE_MOVE, x)
        fun Move(x: Double, y: Double) = TwoDoubleTortoiseCommand(TURTOISE_MOVE, x, y)
        fun Move(x: String, y: String) = UniTortoiseCommand(TURTOISE_MOVE, listOf(x, y))


        fun Line(x: Double) = DoubleTortoiseCommand(TURTOISE_LINE, x)
        fun Line(x: String) = SmallTortoiseCommand(TURTOISE_LINE, x)
        fun Line(x: Double, y: Double) = TwoDoubleTortoiseCommand(TURTOISE_LINE, x, y)
        fun Line(x: String, y: String) = UniTortoiseCommand(TURTOISE_LINE, listOf(x, y))

        /** Завершить рисование текущей линии */
        fun Split() = ZeroTortoiseCommand(TURTOISE_SPLIT)

        fun Rectangle(width: Double, height: Double) = TwoDoubleTortoiseCommand(TURTOISE_RECTANGLE, width, height)
        fun Rectangle(width: String, height: String) = UniTortoiseCommand(TURTOISE_RECTANGLE, listOf(width, height))

        fun Circle(r: Double) = DoubleTortoiseCommand(TURTOISE_CIRCLE, r)
        fun Circle(r: String) = SmallTortoiseCommand(TURTOISE_CIRCLE, r)
        fun Arc(r: Double, startAngle: Double, endAngle: Double) =
            ThreeDoubleTortoiseCommand(TURTOISE_CIRCLE, r, startAngle, endAngle)

        fun Ellipse(r1: Double, r2 :Double) = TwoDoubleTortoiseCommand(TURTOISE_ELLIPSE, r1, r2)

        fun Angle(angle: Double) = DoubleTortoiseCommand(TURTOISE_ANGLE, angle)
        fun Angle(angle: String) = SmallTortoiseCommand(TURTOISE_ANGLE, angle)

        fun AngleAdd(angle: Double) = DoubleTortoiseCommand(TURTOISE_ANGLE_ADD, angle)
        fun AngleAdd(angle: String) = SmallTortoiseCommand(TURTOISE_ANGLE_ADD, angle)

        fun Polyline(points: List<String>) = UniTortoiseCommand(TURTOISE_POLYLINE, points)
        fun PolylineDouble(points: List<Double>) = ListDoubleTortoiseCommand(TURTOISE_POLYLINE, points)

        /** Нарисовать зигзаги*/
        fun Zig(startPosition: Double, height: Double, length: Double) = ListDoubleTortoiseCommand(
            TURTOISE_LINE, listOf(
                startPosition,
                -height,
                length,
                height
            )
        )

        /** Нарисовать зигзаги с наклоном*/
        fun SuperZig(startPosition: Double, height: Double, length: Double, angle: Double): TortoiseCommand {

            val si = Math.abs(Math.cos(angle * Math.PI / 180))
            return if (si == 0.0) {
                DoubleTortoiseCommand(TURTOISE_LINE, startPosition)
            } else {
                ListDoubleTortoiseCommand(
                    TURTOISE_LINE_WITH_ANGLE, listOf(
                        startPosition,
                        90 + angle,
                        -height / si,
                        0.0,
                        length,
                        90 + angle,
                        height / si,
                    )
                )
            }
        }

        /** Построить линию от текущей точки к началу линии. Получаем многоугольник*/
        fun ClosePolygon() = ZeroTortoiseCommand(TURTOISE_CLOSE)

        /** Сохранить текущее состояние черепашки в стек */
        fun Save() = ZeroTortoiseCommand(TURTOISE_SAVE)

        /** Достать из стека предыдущее стостояние черепеашки и воссановть её*/
        fun Load() = ZeroTortoiseCommand(TURTOISE_LOAD)

        /** Восстановить состояние черапшки*/
        fun Peek() = ZeroTortoiseCommand(TURTOISE_PEEK)

        /** Сбросить состояние черапшки к первоначальныму*/
        fun Clear() = ZeroTortoiseCommand(TURTOISE_CLEAR)

        /** Начало цикла черепашки*/
        fun StartLoop(count: Int) = DoubleTortoiseCommand(TURTOISE_LOOP, count.toDouble())

        /** Конец цикла черепашки */
        fun EndLoop() = ZeroTortoiseCommand(TURTOISE_END_LOOP)
    }
}

class SmallTortoiseCommand(
    override val command: Char,
    private val value: String,
) : TortoiseCommand {
    override val size: Int
        get() = 1

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index == 0)
            return memory.value(value, defaultValue)
        return defaultValue
    }

    override fun assign(memory: TortoiseMemory) {
        memory.clear(value)
    }

    override fun print(): String {
        return "SmallTortoiseCommand(${commandToName(command)}, \"$value\")"
    }
}

class DoubleTortoiseCommand(
    override val command: Char,
    private val value: Double,
) : TortoiseCommand {
    override val size: Int
        get() = 1

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index == 0)
            return value
        return defaultValue
    }

    override fun print(): String {
        return "DoubleTortoiseCommand(${commandToName(command)}, $value)"
    }
}

class TwoDoubleTortoiseCommand(
    override val command: Char,
    private val value0: Double,
    private val value1: Double,
) : TortoiseCommand {
    override val size: Int
        get() = 2

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index == 0)
            return value0
        if (index == 1)
            return value1
        return defaultValue
    }

    override fun print(): String {
        return "TwoDoubleTortoiseCommand(${commandToName(command)}, $value0, $value1)"
    }
}

class ThreeDoubleTortoiseCommand(
    override val command: Char,
    private val value0: Double,
    private val value1: Double,
    private val value2: Double,
) : TortoiseCommand {
    override val size: Int
        get() = 3

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index == 0)
            return value0
        if (index == 1)
            return value1
        if (index == 2)
            return value2
        return defaultValue
    }

    override fun print(): String {
        return "ThreeDoubleTortoiseCommand(${commandToName(command)}, $value0, $value1, $value2)"
    }
}

class ListDoubleTortoiseCommand(
    override val command: Char,
    private val values: List<Double>,
) : TortoiseCommand {
    override val size: Int
        get() = values.size

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index >= 0 && index < values.size)
            return values[index]
        return defaultValue
    }

    override fun print(): String {
        val args = values.joinToString(", ")
        return "ListDoubleTortoiseCommand(${commandToName(command)}, $args)"
    }
}

