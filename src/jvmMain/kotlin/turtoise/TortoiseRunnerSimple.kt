package turtoise

import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import turtoise.memory.TortoiseMemory
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

class TortoiseRunnerSimple(
    program: TortoiseProgram,
) : TortoiseRunner(program) {

    val tortoise = Tortoise()

    override fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        figureExtractor: TortoiseFigureExtractor,
    ): List<IFigure> {
        return tortoise.draw(
            commands = commands,
            state = state,
            figureExtractor = figureExtractor
        )
    }

    override fun figure(
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
            val l = TortoiseParser.parseSimpleLine(block)
            FigureList(
                l.commands(l.names.first(), ds).flatMap { c ->
                    tortoise.draw(
                        commands = c,
                        state = state,
                        figureExtractor = TortoiseFigureExtractor(
                            ds = ds,
                            maxStackSize = maxStackSize - 1,
                            memory = memory,
                            runner = this,
                        ),
                    )
                }
            )
        }
    }
}