package turtoise.rect

import com.kos.figure.FigureList
import com.kos.figure.IFigure
import turtoise.Tortoise
import vectors.Vec2

@Deprecated("")
object RectCad {
    fun createFigureFor(block: RectBlock, position: Vec2): IFigure {
        return Tortoise.rectangle(position, block.width, block.height)
    }

    fun calculatePosition(block: RectBlock, c: RectBlock, position: Vec2): Vec2 {
        val pif = c.parentInfo
        val inverse = if (pif.inside) -1.0 else 1.0
        return position + when (c.parentInfo.storona) {
            EStorona.LEFT -> Vec2(
                -block.width / 2.0 - c.width / 2.0 * inverse,
                -block.height / 2.0 + pif.bias * block.height + pif.padding
            )

            EStorona.RIGHT -> Vec2(
                block.width / 2.0 + c.width / 2.0 * inverse,
                -block.height / 2.0 + pif.bias * block.height + pif.padding
            )

            EStorona.TOP -> Vec2(
                -block.width / 2.0 + pif.bias * block.width + pif.padding,
                -block.height / 2.0 - c.height / 2.0 * inverse
            )

            EStorona.BOTTOM -> Vec2(
                -block.width / 2.0 + pif.bias * block.width + pif.padding,
                block.height / 2.0 + c.height / 2.0 * inverse
            )
        }
    }

    fun createFigure(block: RectBlock, position: Vec2): IFigure {
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

    fun calculatePosition(block: RectBlock, center: Vec2): Vec2 {
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