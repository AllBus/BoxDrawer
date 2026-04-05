package turtoise

import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

open class TortoiseFigureAlgorithm(
    val name: String,
    val line: TortoiseParserStackItem,
) : TortoiseAlgorithm {
    private val _names = listOf(name)

    override val names: List<String>
        get() = _names

    private var command: TortoiseAlgorithm? = null
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        if (command == null) {
            command = TortoiseParser.parseSimpleLine(line)
        }
        return command?.let { c ->
            c.commands("_", ds)
        } ?: emptyList()
    }
}