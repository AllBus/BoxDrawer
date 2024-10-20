package turtoise.svg

import androidx.compose.ui.graphics.vector.PathParser
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseFigureExtractor
import turtoise.TortoiseState
import vectors.Vec2

class SvgAlgorithm(val paths: List<String>): TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    private val _names = listOf("_")

    override val names: List<String>
        get() = _names

    override fun draw(
        name: String,
        state: TortoiseState,
        figureExtractor: TortoiseFigureExtractor
    ): IFigure {
        val parser= PathParser()
        val f = paths.map{ p ->
            try {
                val nodes = parser.parsePathString(p).toNodes()
                val figure = NodeParser.convertPathToFigure(nodes, Vec2.Zero)
                figure
            }catch (e: Exception){
                FigureEmpty
            }
        }
        return f.toFigure()
    }
}