package turtoise

abstract class TurtoiseParserStackItem(
//    /** Тип скобок '(','[','{'*/
//    val skobka: Char = ' ',
//    val argument: String = "",
) {

    companion object {
        const val ARGUMENT_NAME = '~'
    }

    abstract fun isArgument() : Boolean
    open val argument:String = ""
    abstract val name :String
    abstract val line:String

    abstract val blocks :List<TurtoiseParserStackItem>

    abstract fun get(index: Int): String?

    open fun doubleValue(index : Int, defaultValue: Double) : Double
    {
        val sv = stringValue(index)
        return sv?.toDoubleOrNull()?:defaultValue
    }

    abstract fun stringValue(index: Int): String?
    fun asDouble(text:String?): Double{
        return text?.toDoubleOrNull()?:0.0
    }

    fun asDouble(text:String?, defaultValue: Double): Double{
        return text?.toDoubleOrNull()?:defaultValue
    }

    abstract fun arguments(): List<String>

    abstract val size: Int
}

class TurtoiseParserStackArgument(
    override val argument: String,
) : TurtoiseParserStackItem() {
    override fun isArgument() : Boolean = true

    override val name: String = argument

    override val blocks: List<TurtoiseParserStackItem>
        get() = emptyList()

    override fun get(index: Int): String? {
        return if (index == 0) argument else null
    }

    override fun stringValue(index: Int): String? {
        return if (index == 0) argument else null
    }

    override fun arguments(): List<String> {
        return listOf(argument)
    }

    override val size: Int
        get() = 1

    override val line: String get() = argument
}

class TurtoiseParserStackBlock(
    /** Тип скобок '(','[','{'*/
    val skobka: Char = ' ',
) : TurtoiseParserStackItem(){
    override fun isArgument() : Boolean = false

    val inner = mutableListOf<TurtoiseParserStackItem>()
    override val blocks = mutableListOf<TurtoiseParserStackItem>()

    override val name get() = inner.firstOrNull()?.argument?:"%%" //?.takeIf { it.isArgument() }?.argument?:""
    override val line: String
        get() = inner.joinToString(" ", "$skobka", "${closeBrace()}"){it.line}

    fun closeBrace(): Char{
        return TortoiseParser.closeBrace(skobka)
    }
    fun add(argument: String)
    {
        inner.add(
            TurtoiseParserStackArgument(
                argument = argument
            )
        )
    }

    fun add(arguments: List<String>)
    {
        arguments.forEach { add(it) }
    }

    fun add(argument: TurtoiseParserStackBlock) {
        inner.add(argument)
        blocks.add(argument)
    }

    override fun arguments(): List<String> {
        return inner.filter { it.isArgument() }.map { it.argument }
    }

    override val size: Int
        get() = inner.size

    override fun get(index: Int): String? {
        return if (index < 0 || index >= inner.size) null else inner[index].argument
    }

    override fun stringValue(index: Int): String? {
        if (index < 0 || index >= inner.size) return null
        var c = 0
        for (i in 0 until inner.size) {
            if (inner[i].isArgument()) {
                if (c == index) return inner[i].argument
                c++
            }
        }
        return null
    }

}