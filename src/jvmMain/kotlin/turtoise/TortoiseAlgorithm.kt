package turtoise

import com.kos.figure.collections.FigureList
import com.kos.figure.IFigure
import turtoise.memory.TortoiseMemory
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem


data class TortoiseBlock(val commands: List<TortoiseCommand>) {
    val size get() = commands.size

    operator fun get(index: Int) = commands[index]
}

interface TortoiseAlgorithm {
    fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock>

    val names: List<String>

    fun draw(
        name: String,
        ds: DrawerSettings,
        state: TortoiseState,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
        maxStackSize: Int,
    ): IFigure {
        return FigureList(
            commands(name, ds).flatMap { block ->
                runner.tortoise.draw(
                    commands = block,
                    state = state,
                    ds = ds,
                    maxStackSize = maxStackSize - 1,
                    memory = memory,
                    runner = runner
                )
            }
        )
    }
}

class TortoiseSimpleAlgorithm(
    name: String,
    commands: List<TortoiseCommand>
) : TortoiseAlgorithm {
    private val _names = listOf(name)
    override val names: List<String>
        get() = _names
    private val turtoiseCommands = listOf(TortoiseBlock(commands))

    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return turtoiseCommands
    }
}

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
