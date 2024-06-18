package turtoise

import com.kos.figure.IFigure
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


}