package turtoise.parser

import turtoise.memory.keys.MemoryKey

object TPArg {
    val MULTI = MemoryKey("@*")
    val ONE = MemoryKey("@?")
    val SOME = MemoryKey("@+")
    val VARIANT = MemoryKey("@*?")
    val ONE_VARIANT = MemoryKey("@??")
    val LINE = MemoryKey("@?-")
    val UNION = MemoryKey("@^")

    private val actions = setOf(MULTI, ONE, SOME, VARIANT, ONE_VARIANT, LINE, UNION)

    fun isAction(block :TortoiseParserStackBlock): Boolean{
        return block.name in actions
    }

    operator fun invoke(name: String) = TortoiseParserStackArgument(MemoryKey("@$name"))

    fun create(name: String, vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock(' ', name, args.toList())
    }

    fun item(name: String, vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(', name, args.toList())
    }

    fun block(args: List<TortoiseParserStackItem>): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(').apply { this.addItems(args) }
    }

    fun block(vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(').apply { this.addItems(args.toList()) }
    }

    fun union(vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(',  UNION).apply { this.addItems(args.toList()) }
    }

    fun figure(name: String): TortoiseParserStackBlock {
        return TortoiseParserStackBlock(' ', "@$name")
    }

    fun text(text: String): TortoiseParserStackArgument {
        return TortoiseParserStackArgument(MemoryKey(text))
    }

    /** Ноль или больше раз */
    fun multi(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', MULTI)
        .apply { this.addItems(args.toList()) }

    /**  Каждый из вариантов может быть использован сколько угодно раз */
    fun multiVariant(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', VARIANT)
        .apply { this.addItems(args.toList()) }

    /** Ноль или один раз */
    fun noneOrOne(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', ONE)
        .apply { this.addItems(args.toList()) }

    /** элементы следуют в указанном порядке но они не обязательны */
    fun noneOrLine(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', LINE)
        .apply { this.addItems(args.toList()) }

    /** Один или больше раз */
    fun oneOrMore(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', SOME)
        .apply { this.addItems(args.toList()) }

    /**  Каждый из вариантов может быть использован один раз */
    fun oneVariant(vararg args: TortoiseParserStackItem) = TortoiseParserStackBlock(
        ' ', ONE_VARIANT)
        .apply { this.addItems(args.toList()) }
}