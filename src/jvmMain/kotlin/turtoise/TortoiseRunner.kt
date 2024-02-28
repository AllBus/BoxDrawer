package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import turtoise.memory.TortoiseMemory
import vectors.Vec2

class TortoiseRunner(
    val memory: TortoiseMemory,
    var program : TortoiseProgram,
) {

    val tortoise = Tortoise()
    val lineIndex = 10

    fun resetMemory() {
        memory.reset()
    }

    fun clear() {

        resetMemory()
    }

    fun draw(state: TortoiseState, ds: DrawerSettings): IFigure {
        clear()

        val figures = program.commands.flatMap { a ->
            a.names.map { n -> a.draw(n, ds, state, this) }
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
        arguments: TurtoiseParserStackItem? = null
    ): IFigure {
        return findAlgorithm(algName)?.let { alg ->
            alg.names.firstOrNull()?.let { n ->
                alg.draw(n, ds, state, this)
            }
        }?: FigureEmpty
    }
}