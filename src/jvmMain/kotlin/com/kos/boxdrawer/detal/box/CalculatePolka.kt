package com.kos.boxdrawer.detal.box

import turtoise.DrawerSettings
import turtoise.Orientation

object CalculatePolka {

    private val sep = charArrayOf(' ', '\t', ';')
    private val horizontNames: Array<String> = arrayOf("h", "horizont", "horizontal", "hor")
    private val verticalNames: Array<String> = arrayOf("v", "vertical", "vert")
    private val endNames: Array<String> = arrayOf("e", "end", "finish", "f")
    private val centerNames: Array<String> = arrayOf("c", "cen", "center")
    private val noDraw: Array<String> = arrayOf("n", "no")


    fun createPolki(line: String): List<Polka> {

        return line.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                if (it.startsWith("(") && it.endsWith(")"))
                    it.drop(1).dropLast(1)
                else
                    it
            }.map {
                polka(it.split(*sep).filter { it.isNotEmpty() })
            }
    }

    fun calculatePolki(polki: List<Polka>, boxWidth: Double, boxWeight: Double, polkaWeight: Double): PolkaSort {
        val bwi = boxWidth
        val bwe = boxWeight

        val upw2 = polkaWeight / 2

        var curx = -upw2
        var cury = -upw2
        var curex = bwi + upw2
        var curey = bwe + upw2

        var curcx = curex / 2
        var curcy = curey / 2
        var curcex = curex / 2
        var curcey = curey / 2

        val ps = PolkaSort()

        /** Вычислить координаты полок если они по всей ширине коробки */
        for (p in polki) {
            val pup = p.width + polkaWeight;
            if (p.orientation == Orientation.Vertical) {
                val ya = 0.0;
                val yb = bwe;
                when (p.order) {
                    1 -> {
                        curx += pup
                        p.calc.Setup(curx, ya, curx, yb)
                    }

                    -1 -> {
                        curex -= pup
                        p.calc.Setup(curex, ya, curex, yb)
                    }

                    2 -> {
                        curcx += pup
                        p.calc.Setup(curcx, ya, curcx, yb)
                    }

                    -2 -> {
                        curcex -= pup
                        p.calc.Setup(curcex, ya, curcex, yb)
                    }
                }
                ps.vList += p
            } else {
                val ya = 0.0
                val yb = bwi

                when (p.order) {
                    1 -> {
                        cury += pup
                        p.calc.Setup(ya, cury, yb, cury)
                    }

                    -1 -> {
                        curey -= pup
                        p.calc.Setup(ya, curey, yb, curey)
                    }

                    2 -> {
                        curcy += pup
                        p.calc.Setup(ya, curcy, yb, curcy)
                    }

                    -2 -> {
                        curcey -= pup
                        p.calc.Setup(ya, curcey, yb, curcey)
                    }
                }
                ps.hList += p
            }

            ps.calcList += p;
        } // end for

        ps.vList = ps.vList.sortedBy { a -> a.calc.sX }
        ps.hList = ps.hList.sortedBy { a -> a.calc.sY }

        val dv = mutableListOf<Polka>();
        var pred = Double.MIN_VALUE;
        for (p in ps.vList) {
            if (p.calc.sX != pred) {
                pred = p.calc.sX;
                dv += p
            }
            p.calc.index = dv.size;
        }


        val dh = mutableListOf<Polka>();
        pred = Double.MIN_VALUE;
        for (p in ps.hList) {
            if (p.calc.sY != pred) {
                pred = p.calc.sY;
                dh += (p);
            }
            p.calc.index = dh.size
        }

        ps.vList = dv;
        ps.hList = dh

        calculateLength(bwi, bwe, ps);

        return ps
    }

    fun calculateLength(w: Double, we: Double, ps: PolkaSort) {
        for (p in ps.calcList) {
            if (p.orientation === Orientation.Vertical) {
                var ya = 0.0
                var yb = we
                val sp = ps.findStart(p)
                val ep = ps.findEnd(p)
                if (sp != null) {
                    ya = sp.calc.sY
                }
                if (ep != null) {
                    yb = ep.calc.sY
                }
                p.calc.sY = ya
                p.calc.eY = yb
            } else {
                var ya = 0.0
                var yb = w
                val sp = ps.findStart(p)
                val ep = ps.findEnd(p)
                if (sp != null) {
                    ya = sp.calc.sX
                }
                if (ep != null) {
                    yb = ep.calc.sX
                }
                p.calc.sX = ya
                p.calc.eX = yb
            }
        }
    }

    fun polka(a: List<String>): Polka {

        var cs = 0;
        var ce = 0;
        var hasE = false;
        var hasC = false;
        var visible = true;
        var orientation = Orientation.Vertical;
        var heights = doubleArrayOf();

        var index = 0;

        var i = 1

        val w = a.firstOrNull()?.toDoubleOrNull() ?: 0.0

        fun readHeights(start: Int): Int {
            val h = mutableListOf<Double>();
            var j = start
            while (j < a.size) {
                val c = a[j]
                val d = c.toDoubleOrNull()
                if (d != null) {
                    h += d
                } else {
                    heights = h.toDoubleArray()
                    return j
                }
                j++
            }
            return a.size
        }

        while (i < a.size) {
            val c = a[i]
            i++
            when {
                isInt(c) -> {
                    when (index) {
                        0 -> cs = c.toIntOrNull() ?: 0
                        1 -> ce = c.toIntOrNull() ?: 0
                    }
                    index++
                }

                c == "(" -> {
                    i = readHeights(i)
                }

                endNames.contains(c) -> {
                    hasE = true
                }

                centerNames.contains(c) -> hasC = true
                horizontNames.contains(c) -> orientation = Orientation.Horizontal
                verticalNames.contains(c) -> orientation = Orientation.Vertical
                noDraw.contains(c) -> visible = false
            }
        }


        return Polka().apply {
            this.orientation = orientation
            this.width = w
            this.height = heights
            this.order = (if (hasE) -1 else 1) * (if (hasC) 2 else 1)
            this.startCell = cs
            this.cellCount = ce
            this.visible = visible
        }
    }

    fun isInt(text: String): Boolean {
        return text.all { c -> c in '0'..'9' }
    }
}