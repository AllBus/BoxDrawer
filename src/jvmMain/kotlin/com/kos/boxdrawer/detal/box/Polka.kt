package com.kos.boxdrawer.detal.box

import com.kos.tortoise.ZigzagInfo
import turtoise.Orientation
import kotlin.math.max
import kotlin.math.min

class PolkaGrid(
    val xCount: Int,
    val yCount: Int,
)

class Polka(
    val orientation: Orientation = Orientation.Horizontal,
    private val width: Double = 0.0,
    val widthInPercentage: Boolean = false,
    val height: DoubleArray = DoubleArray(0),
    val order: Int = 0,
    val startCell: Int = 0,
    val cellCount: Int = 0,
    val visible: Boolean = true,
    val programs: List<PolkaProgram>? = null
) {

    fun polkaDistance(cellWidth: Double):Double {
        return if (widthInPercentage) {
            width * cellWidth/ 100.0
        } else
            width
    }

    fun polkaDistance(cellWidth: Double, cellHeight: Double):Double {
        return if (widthInPercentage) {
            if (orientation == Orientation.Vertical)
                width * cellWidth/ 100.0
            else
                width * cellHeight/ 100.0
        } else
            width
    }

    var calc = PolkaInfo(0.0, 0.0, 0.0, 0.0, 0, 0)


    val endCell: Int
        get() {
            return startCell + cellCount
        }

    fun maxHeight(h: Double): Double {
        return if (height.isEmpty())
            h;
        else {
            val m = height.max();
            max(m, h);
        }
    }

    fun heightForLine(index: Int, h: Double): Double {
        var index = index

        if (height.isEmpty()) return h
        if (height.size == 1) {
            val m1: Double = height.first()
            if (m1 == 0.0) return h
        }
        if (index <= 0) index = 0
        if (index >= height.size) index = height.size - 1
        val m = height[index]
        return if (m == 0.0) h else m
    }

    fun intersect(p: Polka): Boolean {
        return (intersect_1(calc.sX, calc.eX, p.calc.sX, p.calc.eX)
                && intersect_1(calc.sY, calc.eY, p.calc.sY, p.calc.eY))
    }

    private fun intersect_1(a: Double, b: Double, c: Double, d: Double): Boolean {
        var a = a
        var b = b
        var c = c
        var d = d
        var q: Double
        if (a > b) {
            q = a
            a = b
            b = q
        }
        if (c > d) {
            q = c
            c = d
            d = q
        }
        return max(a, c) <= min(b, d)
    }

    fun commandLine(): String {
        val r = "(${width} ${if (orientation == Orientation.Horizontal) "h" else "v"}" +
                " ${orderToText(order)} ${if (!visible) "n" else ""}" +
                " ${startCell} ${cellCount} ${height.joinToString(" ", "( ", " )")}" +
                (programs?.joinToString(" ", "(", ")") { it.commandLine() } ?: "") + ")"

        return r
    }

    fun orderToText(order: Int): String {
        return when (order) {
            1 -> ""
            2 -> "c"
            -1 -> "e"
            -2 -> "c e"
            else -> ""
        }
    }
}

class PolkaInfo(
    var sX: Double,
    var sY: Double,
    var eX: Double,
    var eY: Double,
    var index: Int,
    var id: Int
) {
    fun Setup(sX: Double, sY: Double, eX: Double, eY: Double) {
        this.sX = sX;
        this.sY = sY;
        this.eX = eX;
        this.eY = eY;
    }
}

class PolkaProgram(
    /**Привязка рисунка к краю */
    //val align: TortoiseAlign,
    /** Программа для рисования рисунка */
    val algorithm: String,
    /** Номер левой полки рисования*/
    val startCell: Int,
    /** Номер строны 0 - текущая полка */
    val sideIndex: List<Int>,
) {
    fun commandLine(): String {
        return "s${sideIndex.firstOrNull() ?: 0} $startCell ${
            sideIndex.drop(2).joinToString(" ") { "s$it" }
        } ($algorithm)"
    }

    companion object {
        const val SIDE_BOTTOM = -1
        const val SIDE_TOP = -2
        const val SIDE_NONE = -3
        const val SIDE_LEFT = 2
        const val SIDE_RIGHT = 3
        const val SIDE_FACE = 1
        const val SIDE_BACK = 4
    }
}


enum class TortoiseAlign {
    TOP_LEFT,
    TOP_CNTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}

class PolkaSort {
    var hList: List<Polka> = listOf<Polka>();
    var vList = listOf<Polka>();
    var calcList = listOf<Polka>();

    // var pazDelta = 0.0;
    // var pazWidth = 0.0

    var zigPolkaH: ZigzagInfo = ZigzagInfo(15.0, 35.0)
    var zigPolkaPol: ZigzagInfo = ZigzagInfo(15.0, 35.0)

    /**  получить список перпендикулярных полок к полке p*/
    private fun ortoList(p: Polka) = if (p.orientation == Orientation.Vertical) hList else vList

    fun findStart(p: Polka): Polka? {
        return find(p.startCell, ortoList(p))
    }

    fun findEnd(p: Polka): Polka? {
        if (p.cellCount <= 0) return null;
        return find(p.startCell + p.cellCount, ortoList(p))
    }

    fun find(index: Int, start: List<Polka>): Polka? {
        if (index <= 0)
            return null;

        if (index <= start.size) {
            return start[index - 1];
        }
        return null
    }

    fun intersectList(p: Polka): List<Polka> {
        val or =
            if (p.orientation == Orientation.Vertical) Orientation.Horizontal else Orientation.Vertical

        val l = calcList.filter { c ->
            c.orientation == or
        }.filter { c ->
            c.intersect(p)
        }.filter { c ->
            c.startCell != p.calc.index && c.endCell != p.calc.index
        }.sortedWith { a, b ->
            if (or == Orientation.Vertical) a.calc.sX.compareTo(b.calc.sX) else a.calc.sY.compareTo(
                b.calc.sY
            )
        }

        val start = findStart(p);
        val end = findEnd(p);

        return l - setOfNotNull(start, end)
    }

    fun horizontals(): List<Double> {
        return hList.map { c -> c.calc.sY }.toList();
    }

    fun verticals(): List<Double> {
        return vList.map { c -> c.calc.sX }.toList();
    }
}