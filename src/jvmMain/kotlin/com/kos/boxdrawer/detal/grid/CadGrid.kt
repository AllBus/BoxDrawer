package com.kos.boxdrawer.detal.grid

import androidx.compose.ui.graphics.BlendMode.Companion.Color
import com.kos.boxdrawer.detal.polka.PolkaLine
import figure.*
import figure.composition.FigureColor
import turtoise.DrawerSettings
import vectors.Vec2
import java.util.*

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

    private val next: List<Pair<Int, Int>> = listOf(
        (-1 to 0),
        (0 to -1),
        (1 to 0),
        (0 to 1),
    )

    private val tan = 0.552284749831

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
        return r.toList()
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

    fun bezierQuartir(v: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
        val p1 = next[g1 % 4]
        val p2 = next[g2 % 4]
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSize, v.y + p1.second * smoothSize),
            Vec2(v.x - p1.first * smoothSize * (1 - tan), v.y + p1.second * smoothSize * (1 - tan)),
            Vec2(v.x + p2.first * smoothSize * (1 - tan), v.y - p2.second * smoothSize * (1 - tan)),
            Vec2(v.x + p2.first * smoothSize, v.y - p2.second * smoothSize)
        )
    }

    fun bezierLine(v: Vec2, v2: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
        val p1 = next[g1 % 4]
        val p2 = next[g2 % 4]
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSize, v.y + p1.second * smoothSize),
            Vec2(v.x - p1.first * smoothSize, v.y + p1.second * smoothSize),
            Vec2(v2.x + p2.first * smoothSize, v2.y - p2.second * smoothSize),
            Vec2(v2.x + p2.first * smoothSize, v2.y - p2.second * smoothSize)
        )
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
        if (enableSmooth) {
            val lt = Vec2(left, top);
            val rt = Vec2(right, top);
            val lb = Vec2(left, bottom);
            val rb = Vec2(right, bottom);

            val bz = FigureBezierList.simple(
                listOf(
                    bezierQuartir(lt, smoothSize, 1, 2),
                    bezierLine(lt, rt, smoothSize, 0, 0),
                    bezierQuartir(rt, smoothSize, 2, 3),
                    bezierLine(rt, rb, smoothSize, 1, 1),
                    bezierQuartir(rb, smoothSize, 3, 0),
                    bezierLine(rb, lb, smoothSize, 2, 2),
                    bezierQuartir(lb, smoothSize, 0, 1),
                    bezierLine(lb, lt, smoothSize, 3, 3),
                )
            )

            return FigureColor(color, bz)
        } else {
            val bz = FigurePolyline(
                listOf(
                    Vec2(left, top),
                    Vec2(right, top),
                    Vec2(right, bottom),
                    Vec2(left, bottom),
                )
            )
            return FigureColor(color, bz)
        }
    }

    fun createEntities( gridSize: GridOption,  innerInfo: GridOption, frameSize: Double, drawerSettings: DrawerSettings) : IFigure
    {
        val smooth = gridSize.enable;
        val smoothSize = gridSize.smooth;
        val size = gridSize.size;

        val figureColor = androidx.compose.ui.graphics.Color.Companion.Blue.value.toInt();
        val innerColor = androidx.compose.ui.graphics.Color.Companion.Red.value.toInt();
        val frameColor = androidx.compose.ui.graphics.Color.Companion.DarkGray.value.toInt();

        val result = mutableListOf<IFigure>()

        val frameGroup = FigureList(
            listOf(
                rectangle(-frameSize, frameSize, frameSize + width * size, -(frameSize + height * size),
                    false, 0.0, frameColor),
                rectangle(0.0, 0.0, width * size, -(height * size), gridSize.enable, gridSize.smooth, frameColor)
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
                            val py = -(pi.second * size + 0.5 * size);
                            val si = innerInfo.size * 0.5;

                            group.add(
                                rectangle(px - si, py+si, px+si, py-si, innerSmooth, innerInfo.smooth, innerColor)
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

                                if (n.g != c.g)
                                {
                                    bz.add(bezierQuartir(Vec2(c.x * size, -c.y * size), smoothSize, c.g, n.g));
                                    bz.add(bezierLine(Vec2(c.x * size, -c.y * size), Vec2(n.x * size, -n.y * size), smoothSize, n.g+2, n.g+2));
                                }
                            }

                            group.add(FigureColor(
                                figureColor,
                                FigureBezierList.simple( bz.toList())
                            ))
                        }
                        else
                        {
                            val points = mutableListOf<Vec2>();


                            for (w in gp.indices)
                            {
                                val c = gp[w];
                                points.add(Vec2(c.x * size, -c.y * size));
                            }

                            group.add(FigureColor(figureColor, FigurePolyline(points.toList()) ));
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
)


class GridCell {
    var color = 0
    var visible: Boolean = true
    var use = -1
}


class GridPoint(val x: Int, val y: Int, val g: Int)

