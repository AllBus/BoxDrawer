package turtoise

import figure.FigureList
import figure.IFigure
import vectors.Vec2

class TortoiseRunner(val memory: TortoiseMemory) {

    val tortoise = Tortoise()
    val state = TortoiseState()
    val lineIndex = 10

    fun resetState() {
        state.clear()
    }

    fun resetMemory() {
        memory.reset()
    }

    fun clear() {
        resetState()
        resetMemory()
    }

    fun draw(program: TortoiseProgram, startPoint: Vec2, ds: DrawerSettings): IFigure {

        clear()
        state.moveTo(startPoint)

        val figures = program.commands.flatMap { a ->
            a.names.map { n -> a.draw(n, ds, this) }
        }

        return FigureList(figures)
    }
}