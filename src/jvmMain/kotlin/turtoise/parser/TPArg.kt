package turtoise.parser

import turtoise.memory.keys.MemoryKey

object TPArg {
    operator fun invoke(name: String) = TortoiseParserStackArgument(MemoryKey("@$name"))

    fun create(name: String, vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock(' ', name, args.toList())
    }

    fun item(name: String, vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(', name, args.toList())
    }

    fun block(vararg args: TortoiseParserStackItem): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(').apply { this.addItems(args.toList()) }
    }

    fun figure(name: String): TortoiseParserStackBlock {
        return TortoiseParserStackBlock(' ', "@$name")
    }

    fun text(text: String): TortoiseParserStackArgument {
        return TortoiseParserStackArgument(MemoryKey(text))
    }
}