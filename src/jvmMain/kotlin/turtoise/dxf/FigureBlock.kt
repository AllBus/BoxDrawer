package turtoise.dxf

import androidx.compose.ui.platform.PlatformContext
import com.kos.drawer.IFigureGraphics
import com.kos.figure.FigureEmpty
import vectors.Matrix
import com.kos.figure.IFigure


/**
 * Класс Block, объединяющий фигуру, её трансформацию и модификаторы.
 */
data class FigureBlock(
    val figure: IFigure,
    val matrix: Matrix = Matrix.identity,
    val modifiers: List<IFigure> = emptyList() // Здесь могут быть специфичные модификаторы
) : IFigure by figure {

    override val transform: Matrix
        get() = matrix

    override val hasTransform: Boolean
        get() = !matrix.isIdentity()

    override fun draw(g: IFigureGraphics) {
        // Если графика поддерживает матрицы, можно пушить состояние
        // Но обычно в вашей системе за это отвечает IFigure.path(matrix)
        g.transform(matrix) {
            figure.draw(g)
        }
    }

    // Переопределяем методы, чтобы учитывать вложенность, если это необходимо
    override fun collection(): List<IFigure> = listOf(figure) + modifiers

    companion object {
        val Empty = FigureBlock(FigureEmpty)
    }
}
