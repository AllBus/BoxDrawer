package turtoise

interface TortoiseCommand {
    val command: Char
    val size: Int
    fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double

    operator fun get(index: Int, memory: TortoiseMemory) = take(index, 0.0, memory)

    operator fun get(index: Int, defaultValue: Double, memory: TortoiseMemory) = take(index, defaultValue, memory)

    fun value(memory: TortoiseMemory) =  take(0, 0.0, memory)

    companion object {
        fun create(currentCommand: Char, currentValues: MutableList<String>): TortoiseCommand {
            if (currentValues.size == 0){
                return ZeroTortoiseCommand(currentCommand)
            }
            if (currentValues.size ==1){
                currentValues[0].toDoubleOrNull()?.let {
                    DoubleTortoiseCommand(currentCommand, it)
                } ?: SmallTortoiseCommand(currentCommand, currentValues[0])
            }
            return UniTortoiseCommand(currentCommand, currentValues)
        }

        const val TURTOISE_ZIGZAG = 'z';
        const val TURTOISE_RECTANGLE = 'x';
        const val TURTOISE_MOVE = 'm';
        const val TURTOISE_LINE = 'l';
        const val TURTOISE_LINE_WITH_ANGLE = 'л';
        const val TURTOISE_LINE_PERPENDICULAR = 'p';
        const val TURTOISE_HORIZONTAL = 'h';
        const val TURTOISE_VERTICAL = 'v';
        const val TURTOISE_ELLIPSE = 'e';
        const val TURTOISE_CIRCLE = 'c';
        const val TURTOISE_BEZIER = 'b';
        const val TURTOISE_ANGLE = 'a';
        const val TURTOISE_ANGLE_ADD = 'q';
        const val TURTOISE_SPLIT = '|';
        const val TURTOISE_CLEAR = '!';
        const val TURTOISE_CLOSE = '`';
        const val TURTOISE_SAVE = 's';
        const val TURTOISE_LOAD = 'y';
        const val TURTOISE_METHOD_NAME = '@';
        const val TURTOISE_METHOD_RUN = '=';
        const val TURTOISE_LOOP = '>';
        const val TURTOISE_END_LOOP = '<';

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
}

class ZeroTortoiseCommand(
    override val command: Char,
) : TortoiseCommand {
    override val size: Int
        get() = 0

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        return defaultValue
    }
}

class UniTortoiseCommand(
    override val command: Char,
    private val values: List<String>,
) : TortoiseCommand {
    override val size: Int
        get() = values.size

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index >= 0 && index < values.size)
            return memory.value(values[index], defaultValue)
        return defaultValue
    }
}

interface TortoiseMemory {
    fun value(variable: String, defaultValue: Double): Double
}