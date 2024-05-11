package com.kos.boxdrawe.presentation

import com.kos.figure.Figure
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import turtoise.Tortoise
import turtoise.rect.EStorona
import turtoise.rect.RectBlock
import turtoise.rect.RectBlockEdges
import turtoise.rect.RectBlockParent
import vectors.Vec2

class RectToolsData(val tools: ITools) {


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

    val rectBlocks = MutableStateFlow(
        RectBlock(
            width = 10.0,
            height = 10.0,
            edges = RectBlockEdges.default(),
            parentInfo = RectBlockParent(
                storona = EStorona.LEFT,
                inside = false,
                padding = 0.0,
                bias = 0.5,
            )
        )
    )

    private val redrawEvent = MutableStateFlow(0)
    private val selectPos = MutableStateFlow<IFigure>(Figure.Empty)
    private val points = MutableStateFlow<List<Vec2>>(emptyList())


    val figures = combine(redrawEvent, rectBlocks, selectPos) { e, block, s ->
        FigureList(listOf(createFigure(block, Vec2.Zero), s))
    }

    fun redraw() {
        redrawEvent.value += 1
    }

    private var current = RectBlockPosition(
        rect = rectBlocks.value,
        storona = EStorona.TOP
    )
    private var currentY: Int = 0

    fun selectPosition(x: Int, y: Int) {
        val (a, b) = when (Pair(x, y)) {
            -1 to 0 -> {
                EStorona.LEFT to EStorona.RIGHT
            }

            1 to 0 -> {
                EStorona.RIGHT to EStorona.LEFT

            }

            0 to 1 -> {
                EStorona.BOTTOM to EStorona.TOP
            }

            0 to -1 -> {
                EStorona.TOP to EStorona.BOTTOM

            }

            else -> {
                return
            }
        }
        if (current.storona != a) {
            current = RectBlockPosition(
                current.rect,
                a
            )
        } else {
            if (current.rect.parentInfo.storona == b) {
                current.rect.parent?.let { p ->
                    current = RectBlockPosition(
                        p,
                        a
                    )
                }
            } else {
                current.rect.children.find {
                    it.parentInfo.storona == a
                }?.let { p ->
                    current = RectBlockPosition(
                        p,
                        a
                    )
                }
            }
        }

        val rr = current.rect
        val pos = calculatePosition(rr, Vec2.Zero)
        selectPos.value = FigureColor(
            0xFF00ff,

            FigurePolyline(
                when (current.storona) {
                    EStorona.LEFT -> listOf(
                        pos + Vec2(-rr.width / 2.0, -rr.height / 2.0),
                        pos + Vec2(-rr.width / 2.0, rr.height / 2.0)
                    )

                    EStorona.RIGHT -> listOf(
                        pos + Vec2(rr.width / 2.0, -rr.height / 2.0),
                        pos + Vec2(rr.width / 2.0, rr.height / 2.0)
                    )

                    EStorona.TOP -> listOf(
                        pos + Vec2(-rr.width / 2.0, -rr.height / 2.0),
                        pos + Vec2(rr.width / 2.0, -rr.height / 2.0)
                    )

                    EStorona.BOTTOM -> listOf(
                        pos + Vec2(-rr.width / 2.0, rr.height / 2.0),
                        pos + Vec2(rr.width / 2.0, rr.height / 2.0)
                    )
                }
            )
        )

    }

    fun createBox() {
        val pt = points.value
        if (pt.isNotEmpty()) {

            val b = RectBlock(
                width = pt[0].x,
                height = pt[0].y,
                edges = RectBlockEdges.default(),
                parentInfo = RectBlockParent(
                    storona = current.storona,
                    inside = false,
                    padding = 0.0,
                    bias = 0.5,
                )
            )
            b.parent = current.rect

            redraw()
        }
    }

    fun setPoints(text: String) {
        points.value = text.split(",").map { it.trim() }.windowed(2, 2) { l ->
            Vec2(l[0].toDoubleOrNull() ?: 0.0, l[1].toDoubleOrNull() ?: 0.0)
        }
    }
}

data class RectBlockPosition(
    val rect: RectBlock,
    val storona: EStorona,
)