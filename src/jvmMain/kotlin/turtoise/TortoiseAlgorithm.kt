package turtoise

import com.kos.figure.FigureList
import com.kos.figure.IFigure


data class TortoiseBlock(val commands: List<TortoiseCommand>){
    val size get() = commands.size

    operator fun get(index:Int) = commands[index]
}

interface TortoiseAlgorithm {
    fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock>

    val names: List<String>

    fun draw(
        name: String,
        ds: DrawerSettings,
        state: TortoiseState,
        runner: TortoiseRunner
    ): IFigure {
        return FigureList(
            commands(name, ds).flatMap { block ->
                runner.tortoise.draw(
                    commands = block,
                    state = state,
                    ds = ds,
                    maxStackSize = runner.lineIndex,
                    memory = runner.memory,
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

class TortoiseFigureAlgorithm(
    val name:String,
    val line:TurtoiseParserStackItem
) : TortoiseAlgorithm {
    private val _names = listOf(name)


    override val names: List<String>
        get() = _names

    private var command : TortoiseAlgorithm? = null
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        if (command == null) {
            command = TortoiseParser.parseSimpleLine(line)
        }
        return command?.let{ c ->
            c.commands("_" , ds)
        }?: emptyList()
    }
}