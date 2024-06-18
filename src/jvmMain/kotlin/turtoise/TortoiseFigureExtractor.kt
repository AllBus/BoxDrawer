package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.FigurePolygon
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import turtoise.memory.TortoiseMemory
import turtoise.parser.TortoiseParserStackItem

class TortoiseFigureExtractor(
    val ds: DrawerSettings,
    val maxStackSize: Int,
    val memory: TortoiseMemory,
    val runner: TortoiseRunner,
) {

    fun figure(
        block: TortoiseParserStackItem?,
    ): IFigure? {
        val state = TortoiseState()
        return block?.let {
            runner.figure(
                block = block,
                ds = ds,
                state = state,
                maxStackSize = maxStackSize,
                memory = memory,
            )
        }
    }

    fun tortoiseDraw(
        block:TortoiseBlock,
        state: TortoiseState,
    ): List<IFigure>{
        return runner.tortoise.draw(
            commands = block,
            state = state,
            figureExtractor = this
        )
    }

    fun isStackOverflow(): Boolean {
        return (maxStackSize <= 0)
    }

    fun collectPolygons(f: IFigure) : List<FigurePolygon> =
        f.list().filterIsInstance(FigurePolygon::class.java)

    fun collectPaths(f: IFigure) : List<IFigurePath> {
        return IFigure.path(f)
    }

    companion object {
        fun pathAtIndex(paths: List<IFigurePath>, edge: Int): IFigurePath {
            var e = edge
            for (p in paths) {
                if (p.edgeCount() > e) {
                    return p.path(e)
                } else {
                    e -= p.edgeCount()
                }
            }
            return FigureEmpty
        }

        fun positionInPath(paths: List<IFigurePath>, edge: Int, delta: Double): PointWithNormal? {
            var e = edge
            for ( p in paths){
                if (p.edgeCount()> e){
                    return when {
                        delta in 0.0..1.0 -> {
                            p.positionInPath(e, delta)
                        }
                        delta > 1.0 -> {
                            p.positionInPathAtMM(e, delta)
                        }
                        else -> {
                            p.positionInPathAtMM(e, p.pathLength(e) -delta)
                        }
                    }
                } else {
                    e -= p.edgeCount()
                }
            }
            return null
        }
    }

    fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: Int,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }
}