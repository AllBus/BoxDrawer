package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.collections.FigureList
import com.kos.figure.IFigure
import turtoise.memory.BlockTortoiseMemory
import turtoise.memory.SimpleTortoiseMemory
import turtoise.parser.TortoiseParserStackItem

class TortoiseRunner(
    var program : TortoiseProgram,
) {

    val tortoise = Tortoise()
    val lineIndex = 10



    fun clear() {

      //  resetMemory()
    }

    fun draw(state: TortoiseState, ds: DrawerSettings): IFigure {
        clear()

        val figures = program.commands.flatMap { a ->
            a.names.map { n -> a.draw(n, ds, state, SimpleTortoiseMemory(), this, lineIndex) }
        }

        return FigureList(figures)
    }

    fun findAlgorithm(a: String): TortoiseAlgorithm?{
        return program.algorithms[a]
    }

    fun figure(
        algName: String,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        arguments: TortoiseParserStackItem? = null
    ): IFigure {
        return findAlgorithm(algName)?.let { alg ->
            alg.names.firstOrNull()?.let { n ->
                alg.draw(
                    name = n,
                    ds = ds,
                    state = state,
                    memory = arguments?.let { BlockTortoiseMemory(it)}?:SimpleTortoiseMemory(),
                    runner = this,
                    maxStackSize = maxStackSize,
                )
            }
        }?: FigureEmpty
    }
}