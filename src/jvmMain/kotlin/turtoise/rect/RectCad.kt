package turtoise.rect

import com.kos.figure.collections.FigureList
import com.kos.figure.IFigure
import turtoise.FigureCreator

@Deprecated("")
object RectCad {
    fun createFigureFor(block: RectBlock, position: vectors.Vec2): IFigure {
        return FigureCreator.rectangle(position, block.width, block.height)
    }

    fun calculatePosition(block: RectBlock, c: RectBlock, position: vectors.Vec2): vectors.Vec2 {
        val pif = c.parentInfo
        val inverse = if (pif.inside) -1.0 else 1.0
        return position + when (c.parentInfo.storona) {
            EStorona.LEFT -> vectors.Vec2(
                -block.width / 2.0 - c.width / 2.0 * inverse,
                -block.height / 2.0 + pif.bias * block.height + pif.padding
            )

            EStorona.RIGHT -> vectors.Vec2(
                block.width / 2.0 + c.width / 2.0 * inverse,
                -block.height / 2.0 + pif.bias * block.height + pif.padding
            )

            EStorona.TOP -> vectors.Vec2(
                -block.width / 2.0 + pif.bias * block.width + pif.padding,
                -block.height / 2.0 - c.height / 2.0 * inverse
            )

            EStorona.BOTTOM -> vectors.Vec2(
                -block.width / 2.0 + pif.bias * block.width + pif.padding,
                block.height / 2.0 + c.height / 2.0 * inverse
            )
        }
    }

    fun createFigure(block: RectBlock, position: vectors.Vec2): IFigure {
        return FigureList(
            listOf(
                createFigureFor(block, position)
            ) +
                    block.children.map { c ->
                        val pos = calculatePosition(block, c, position)
                        createFigure(c, pos)

                    }
        )
    }

    fun calculatePosition(block: RectBlock, center: vectors.Vec2): vectors.Vec2 {
        var p: RectBlock? = block.parent
        var r = block
        var pos = center

        while (p != null) {
            pos = calculatePosition(p, r, pos)
            r = p
            p = p.parent
        }
        return pos
    }
}