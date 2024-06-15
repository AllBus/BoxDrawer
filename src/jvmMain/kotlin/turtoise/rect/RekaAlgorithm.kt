package turtoise.rect

import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.parser.TortoiseParserStackBlock
import turtoise.memory.TortoiseMemory

class RekaAlgorithm(
    val rekaBlock: TortoiseParserStackBlock,
    useAlgorithms: Array<String>?
) : TortoiseAlgorithm {

    val reka = RekaCad.newReka(rekaBlock)

    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String> = listOf("reka")

    override fun draw(
        name: String,
        ds: DrawerSettings,
        state: TortoiseState,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
        maxStackSize: Int
    ): IFigure {
        return reka?.let{ r ->
            val result = RekaCad.createFigure(r, state.xy, state.angle, memory)
            FigurePolyline(result.points, close = true)
        } ?: FigureEmpty
    }
}