package com.kos.boxdrawer.detal.box

import turtoise.Orientation
import turtoise.ZigzagInfo
import kotlin.math.max
import kotlin.math.min

class Polka {

    var orientation: Orientation= Orientation.Horizontal
    var width = 0.0
    var height = DoubleArray(0)
    var order = 0
    var startCell = 0
    var cellCount = 0
    var visible: Boolean = true

    var calc = PolkaInfo(0.0, 0.0, 0.0, 0.0, 0, 0)


    val endCell : Int get() { return startCell + cellCount }

    fun maxHeight(h: Double):Double
    {
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
}

class PolkaInfo(
    var sX: Double,
    var sY: Double,
    var eX: Double,
    var eY: Double,
    var index : Int,
    var id:Int
){
    fun Setup(sX: Double, sY: Double, eX:Double, eY:Double)
    {
        this.sX = sX;
        this.sY = sY;
        this.eX = eX;
        this.eY = eY;
    }
}

class PolkaSort
{
    var hList : List<Polka>  = listOf<Polka>();
    var vList = listOf<Polka>();
    var calcList = listOf<Polka>();

   // var pazDelta = 0.0;
   // var pazWidth = 0.0

    var zigPolkaH: ZigzagInfo =ZigzagInfo(15.0, 35.0)
    var zigPolkaPol: ZigzagInfo =ZigzagInfo(15.0, 35.0)

    /**  получить список перпендикулярных полок к полке p*/
    private fun ortoList(p: Polka) = if (p.orientation == Orientation.Vertical) hList else vList

    fun findStart(p :Polka): Polka?
    {
        return find(p.startCell, ortoList(p))
    }
    fun findEnd(p: Polka) : Polka?
    {
        if (p.cellCount <= 0) return null;
        return find(p.startCell+p.cellCount, ortoList(p))
    }

    fun find(index: Int, start: List<Polka> ):Polka?
    {
        if (index <= 0)
            return null;

        if (index <= start.size)
        {
            return start[index - 1];
        }
        return null
    }

    fun intersectList(p: Polka ):List<Polka>
    {
        val or = if (p.orientation == Orientation.Vertical) Orientation.Horizontal else Orientation.Vertical

        val l = calcList.filter { c ->
            c.orientation == or
        }.filter { c -> c.intersect(p)
        }.filter{ c -> c.startCell != p.calc.index && c.endCell != p.calc.index
        }.sortedWith { a, b->
            if (or == Orientation.Vertical) a.calc.sX.compareTo(b.calc.sX) else a.calc.sY.compareTo(b.calc.sY)
        }

        val start = findStart(p);
        val end = findEnd(p);

        return l - setOfNotNull(start, end)
    }

    fun horizontals(): List<Double>
    {
        return hList.map{c -> c.calc.sY}.toList();
    }

    fun  verticals() : List<Double>
    {
        return vList.map{c -> c.calc.sX}.toList();
    }
}