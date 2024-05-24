package com.kos.boxdrawer.detal.grid

import androidx.compose.ui.graphics.toArgb
import com.kos.figure.FigureBezierList
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import turtoise.DrawerSettings
import turtoise.FigureCreator
import turtoise.FigureCreator.next
import turtoise.Tortoise
import vectors.Vec2
import java.util.Stack
import kotlin.math.abs
import kotlin.math.min

class CadGrid {
    var width = 40
    var height = 30

    private var cells: Array<Array<GridCell>> = Array(40) { Array(30) { GridCell() } }

    private val outBoardColor = Int.MIN_VALUE;
    public val MAX_SIZE = 1000;

    var currentColor = 1


    var currentX = 0
    var currentY = 0

    var scale = 1f
    var shiftX = 0
    var shiftY = 0

    fun recreate(width: Int, height: Int) {
        val newGrid: Array<Array<GridCell>> = Array(width) { iy ->
            val s = cells.getOrNull(iy)
            Array(height) { ix -> s?.getOrNull(ix) ?: GridCell() }
        }
        cells = newGrid
        this.width = width
        this.height = height
    }


    private val shifts: List<Pair<Int, Int>> = listOf(
        (-1 to -1),
        (0 to -1),
        (0 to 0),
        (-1 to 0),
    )



    fun actual(sX: Int, sY: Int): Boolean {
        return !(sX < 0 || sY < 0 || sX >= width || sY >= height)
    }

    fun fillColor(startX: Int, startY: Int, newColor: Int) {
        if (!actual(startX, startY))
            return;

        val c = cells[startX][startY].color;
        if (c == newColor)
            return;


        val stack = Stack<Pair<Int, Int>>();
        stack.push(startX to startY)

        while (stack.size > 0) {
            val p = stack.pop();
            val sX = p.first;
            val sY = p.second;

            if (actual(sX, sY)) {
                if (cells[sX][sY].color == c) {
                    cells[sX][sY].color = newColor
                    stack.push(sX + 1 to sY)
                    stack.push(sX to sY + 1)
                    stack.push(sX - 1 to sY)
                    stack.push(sX to sY - 1)
                }
            }
        }
    }


    fun colorAt(x: Int, y: Int): Int {
        return if (actual(x, y)) cells[x][y].color else outBoardColor
    }

    private fun equalColor(x: Int, y: Int, color: Int): Boolean {
        return colorAt(x, y) == color
    }

    private fun useAt(x: Int, y: Int): Int {
        return if (actual(x, y)) cells[x][y].use else outBoardColor
    }

    private fun equalUse(x: Int, y: Int, useIndex: Int): Boolean {
        return useAt(x, y) == useIndex
    }

    fun fill(startX: Int, startY: Int, useIndex: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        if (!actual(startX, startY))
            return result;

        val c = cells[startX][startY].color;


        val stack = Stack<Pair<Int, Int>>()
        stack.push(startX to startY)

        while (stack.size > 0) {
            val p = stack.pop()
            val sX = p.first
            val sY = p.second

            if (actual(sX, sY)) {
                if (cells[sX][sY].color == c && cells[sX][sY].use != useIndex) {
                    result.add(p);
                    cells[sX][sY].use = useIndex
                    stack.push(sX + 1 to sY)
                    stack.push(sX to sY + 1)
                    stack.push(sX - 1 to sY)
                    stack.push(sX to sY - 1)
                }
            }
        }
        return result.toList()
    }

    private fun figurePoints(r: Int, startX: Int, startY: Int): List<GridPoint> {

        var g = 0
        var x = startX + 1
        var y = startY
        val gp = mutableListOf<GridPoint>(GridPoint(startX + 1, startY, 2))

        do {
            var sX = 0
            var sY = 0
            var p = shifts[g]
            sX = x + p.first
            sY = y + p.second
            if (equalUse(sX, sY, r)) {
                p = next[g]
                x = x + p.first
                y = y + p.second
                gp.add(GridPoint(x, y, g))
                g = (g + 3) % 4
            } else {
                g = (g + 1) % 4
            }
        } while (x != startX || y != startY)

        return gp.toList()
    }

    private fun filterLinePoints(gp: List<GridPoint>): List<GridPoint> {
        val r = mutableListOf<GridPoint>()
        if (gp.size > 1) {
            for (w in 0 until gp.size) {
                val c = gp[w]
                val n = gp[(w + 1) % gp.size]
                if (n.g != c.g) {
                    r.add(c)
                }
            }
            r.add(gp.last())
        }
        return r.dropLast(1).toList()
    }

    fun setColor(sX: Int, sY: Int, color: Int) {
        if (actual(sX, sY)) {
            cells[sX][sY].color = color;
        }
    }

    fun setCurrentCoord(sX: Int, sY: Int) {
        if (actual(sX, sY)) {
            currentX = sX
            currentY = sY
        }
    }



    fun distance(a: GridPoint, b:GridPoint):Int {
        return abs(a.x-b.x) + abs(a.y-b.y)
    }

    fun calculateMaxRadius(a:Int, b:Int): Double{
        return min(a, b)/2.0
    }


    fun rectangle(
        left: Double,
        top: Double,
        right: Double,
        bottom: Double,
        enableSmooth: Boolean,
        smoothSize: Double,
        color: Int
    ): IFigure {
        return FigureColor(color, FigureCreator.rectangle(left, top, right, bottom, enableSmooth, smoothSize))
    }

    fun createEntities(gridSize: GridOption, innerInfo: GridOption, frameSize: Double, drawerSettings: DrawerSettings) : IFigure
    {
        val smooth = gridSize.enable;
        val smoothSize = gridSize.smooth;
        val size = gridSize.size;
        val unionSize = gridSize.roundCell;

        val figureColor = androidx.compose.ui.graphics.Color.Companion.Blue.toArgb();
        val innerColor = androidx.compose.ui.graphics.Color.Companion.Red.toArgb();
        val frameColor = androidx.compose.ui.graphics.Color.Companion.DarkGray.toArgb();

        val result = mutableListOf<IFigure>()

        val frameGroup = FigureList(
            listOf(
                rectangle(-frameSize, -frameSize, frameSize + width * size, (frameSize + height * size),
                    false, 0.0, frameColor),
                rectangle(0.0, 0.0, width * size, (height * size), gridSize.enable, gridSize.smooth, frameColor)
            )
        )
        result.add(frameGroup);

        cells.forEach { it.forEach { c -> c.use = -1 } }

        var r = 0;
        val gp = mutableListOf<GridPoint>()

        val innerSmooth = innerInfo.smooth != 0.0;


        for (i in 0 until width)
        {
            for (j in 0 until height)
            {
                if (cells[i][ j].use == -1)
                {
                    r++;
                    val p = fill(i, j, r);

                    val group = mutableListOf<IFigure>()

                    if (innerInfo.enable && p.size>0)
                    {
                        for (pi in p) {
                            val px = pi.first * size + 0.5 * size;
                            val py = (pi.second * size + 0.5 * size);
                            val si = innerInfo.size * 0.5;

                            group.add(
                                rectangle(px - si, py-si, px+si, py+si, innerSmooth, innerInfo.smooth, innerColor)
                            );
                        }
                    }

                    val gp = filterLinePoints(figurePoints(r, i, j));

                    if (gp.size > 2)
                    {

                        if (smooth)
                        {
                            val bz = mutableListOf<FigureBezierList>()

                            for (w in gp.indices)
                            {
                                val c = gp[w];
                                val n = gp[(w + 1) % gp.size];
                                val n2 = gp[(w + 2) % gp.size];
                             ///   val n3 = gp[(w + 3) % gp.size];
                                val nb = gp[(w -1 + gp.size) % gp.size];
                             //   val na = gp[(w -2 + gp.size) % gp.size];

                                if (n.g != c.g)
                                {
                                    /* Нарисовать скругление угла и линию после него */

                                    var radius = smoothSize
                                    var radius2 = smoothSize


                                    if(unionSize>1){

                                        val db = distance(nb, c)
                                        val dc = distance(n, c)
                                        val dn = distance(n2, n)

                                        val rc = min(unionSize.toDouble(),calculateMaxRadius(dc, db))
                                        val rn = min(unionSize.toDouble(),calculateMaxRadius(dc, dn))

                                        radius = rc*smoothSize
                                        radius2 = rn*smoothSize

                                    }else {
                                        radius = smoothSize
                                    }

                                    bz.add(FigureCreator.bezierQuartir(
                                        v = Vec2(c.x * size, c.y * size),
                                        smoothSize = radius,
                                        g1 = c.g,
                                        g2 = n.g
                                    ));
                                    bz.add(
                                        FigureCreator.bezierLine(
                                            v = Vec2(c.x * size, c.y * size),
                                            v2 = Vec2(n.x * size, n.y * size),
                                            smoothSizeStart = radius,
                                            smoothSizeEnd = radius2,
                                            g1 = n.g + 2,
                                            g2 = n.g + 2
                                        )
                                    );
                                }
                            }

                            group.add(
                                FigureColor(
                                figureColor,
                                FigureBezierList.simple( bz.toList()).toFigure()
                            )
                            )
                        }
                        else
                        {
                            val points = mutableListOf<Vec2>();


                            for (w in gp.indices)
                            {
                                val c = gp[w];
                                points.add(Vec2(c.x * size, c.y * size));
                            }

                            group.add(FigureColor(figureColor, FigurePolyline(points.toList(), true) ));
                        }
                    }
                    if (group.size > 0)
                    {
                        result.add(FigureList(group));
                    }
                }
            }
        }

        return FigureList(result.toList())
    }
}

class GridOption(
    val size: Double,
    val smooth: Double,
    val enable: Boolean,
    val roundCell: Int = 1
)


class GridCell {
    var color = 0
    var visible: Boolean = true
    var use = -1
}


class GridPoint(val x: Int, val y: Int, val g: Int)

