package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import turtoise.memory.FigureTortoiseMemory
import turtoise.memory.SimpleTortoiseMemory
import turtoise.memory.TortoiseMemory

import turtoise.parser.TortoiseParserStackItem

abstract class TortoiseRunner(
    var program: TortoiseProgram,
) {

    val lineIndex = 10


    fun clear() {

        //  resetMemory()
    }

    abstract fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        figureExtractor: TortoiseFigureExtractor,
    ): List<IFigure>

    fun draw(state: TortoiseState, ds: DrawerSettings): IFigure {
        clear()

        val figures = program.commands.flatMap { a ->
            a.names.map { n -> a.draw(
                name = n,
                state = state,
                figureExtractor = TortoiseFigureExtractor(
                    ds = ds,
                    maxStackSize = lineIndex,
                    memory = SimpleTortoiseMemory(),
                    runner = this,
                )
            )
            }
        }

        return FigureList(figures)
    }

    fun findAlgorithm(a: String): TortoiseAlgorithm? {
        return program.algorithms[a]
    }

    fun figure(
        algName: String,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
        arguments: TortoiseParserStackItem?,
    ): IFigure {
        return findAlgorithm(algName)?.let { alg ->
            alg.names.firstOrNull()?.let { n ->
                alg.draw(
                    name = n,
                    state = state,
                    figureExtractor = TortoiseFigureExtractor(
                        ds = ds,
                        maxStackSize = maxStackSize,
                        memory = arguments?.let { FigureTortoiseMemory(it, memory) }
                            ?: SimpleTortoiseMemory(),
                        runner = this,
                    )
                )
            }
        } ?: FigureEmpty
    }

    open fun figure(
        block: TortoiseParserStackItem,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
    ): IFigure {
        val n = block.name.name

        return if (n.startsWith("@")) {
            figure(
                algName = n.drop(1),
                ds = ds,
                state = state,
                maxStackSize = maxStackSize,
                memory = memory,
                arguments = block
            )
        } else {
            FigureEmpty
        }
    }
}