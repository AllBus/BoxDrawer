package turtoise

import figure.FigureList
import figure.IFigure


data class TortoiseBlock(val commands: List<TortoiseCommand>){
    val size get() = commands.size

    operator fun get(index:Int) = commands[index]
}

interface TortoiseAlgorithm {
    fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock>

    val names: List<String>
    fun draw(name: String, ds: DrawerSettings, runner: TortoiseRunner): IFigure {
        return FigureList(
            commands(name, ds).flatMap { block ->
                runner.tortoise.draw(block, runner.state, ds, runner.lineIndex, runner.memory)
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
