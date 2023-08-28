package turtoise

class TurtoiseParserStackItem(
    val skobka: Char = ' ',
    val argument: String = "",
) {

    private val inner = mutableListOf<TurtoiseParserStackItem>()
    val blocks = mutableListOf<TurtoiseParserStackItem>()

    companion object {
        const val ARGUMENT_NAME = '~'
    }

    fun add(argument: String)
    {
        inner.add(
            TurtoiseParserStackItem(
                skobka = ARGUMENT_NAME,
                argument = argument
            )
        )
    }

    fun add(arguments: List<String>)
    {
        arguments.forEach { add(it) }
    }

    fun add(argument: TurtoiseParserStackItem) {
        inner.add(argument)
        blocks.add(argument)
    }

    fun get(index: Int): String? {
        return if (index < 0 || index >= inner.size) null else inner[index].argument
    }

    fun stringValue(index: Int): String? {
        if (index < 0 || index >= inner.size) return null
        var c = 0
        for (i in 0 until inner.size) {
            if (inner[i].skobka == ARGUMENT_NAME) {
                if (c == index) return inner[i].argument
                c++
            }
        }
        return null
    }

    fun doubleValue(index : Int, defaultValue: Double) : Double
    {
        val sv = stringValue(index)
        return sv?.toDoubleOrNull()?:defaultValue
    }

}