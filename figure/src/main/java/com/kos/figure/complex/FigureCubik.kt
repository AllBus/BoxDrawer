package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.tortoise.ZigzagInfo
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs

class FigureCubik(
    val size: Double,
    sides: List<Int>,
    val zigInfo: ZigzagInfo,
    val cornerRadius:Double,
    val enableDrop: Boolean,
    val reverseX: Boolean,
    val reverseY: Boolean,
    val zigFirstIndex: Int,
    val zigDistance: Int,
) : IFigure {

    val bound : BoundingRectangle
    val lastX : Int
    val lastY: Int
    val sideCounter : List<Int>

    init {
        var minX = 0
        var maxX =0
        var minY = 0
        var maxY = 0
        var x = 0
        var y = 0
        var isX = true
        val preSides = if (sides.size == 1) listOf(sides[0], sides[0]) else sides
        preSides.forEach { p ->
            if (isX){
                x+=p
                if (x< minX)
                    minX = x

                if (x> maxX)
                    maxX = x
            }else{
                y+=p
                if (y<minY)
                    minY = y

                if (y>maxY)
                    maxY = y
            }
            isX = !isX
        }

        bound = BoundingRectangle(
            Vec2(minX*size, minY*size),
            Vec2(maxX*size, maxY*size)
        )
        lastX = -x
        lastY = -y
        if (preSides.size %2 == 1) {
            sideCounter = preSides.dropLast(1) + listOf(lastX+preSides.last(), lastY)
        }else {
            sideCounter = preSides + listOf(lastX, lastY)
        }
    }

    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return listOf()
    }

    override fun rect(): BoundingRectangle {
        return bound
    }

    private val zigX : IFigure by lazy {
        if (reverseX) creteFigureReverse() else createFigure()
    }

    private val zigY : IFigure by lazy {
        if (reverseY) creteFigureReverse() else createFigure()
    }

    fun creteFigureReverse():IFigure{
        val s2 = (size-zigInfo.width)/2
        return FigurePolyline(listOf(
            Vec2( s2,0.0),
            Vec2( s2, -zigInfo.height),
            Vec2(  size - s2, -zigInfo.height),
            Vec2( size - s2, 0.0),
        ))
    }

    fun createFigure():Figure{
        val s2 = (size-zigInfo.dropedWidth)/2
        val s1 = (size-zigInfo.width)/2
        return FigurePolyline(listOf(
            Vec2(s1, 0.0),
            Vec2(s2, 0.0),
            Vec2(s2, zigInfo.height),
            Vec2(size - s2, zigInfo.height),
            Vec2(size - s2, 0.0),
            Vec2(size- s1, 0.0),
        ))
    }

    override fun draw(g: IFigureGraphics) {
        if (sideCounter.size<2) return

        if (g.isSimple()){
            drawSimple(g)
            return
        }


        val he = abs(zigInfo.height)

        val hex = (enableDrop && reverseX)
        val hey = (enableDrop && reverseY)


        val hee = if (enableDrop) abs(he) else 0.0

        val s2 = (size-zigInfo.width)/2
        g.save()

        var isX: Boolean = true

        for (si in sideCounter.indices) {
            val s = sideCounter[si]
            val next = sideCounter[(si+1)%sideCounter.size]

            val pred = if (si==0) lastY else sideCounter[si-1 ]

            if (s<0) {
                g.rotate(180.0, Vec2.Zero)
            }

            val nextReverse = getNextReverse(isX, s, next)
            val predReverse = getNextReverse(!isX, pred, s)

            val count = abs(s)
            if (isX) {
                drawX(g, s2, count, zigX, hee, hex, hey, hey,nextReverse,predReverse)
                g.rotate(90.0, Vec2.Zero)
            } else {
                drawX(g, s2, count, zigY, hee, hey, hex, hex,nextReverse,predReverse)
                g.rotate(-90.0, Vec2.Zero)
            }
            if (s<0) {
                g.rotate(-180.0, Vec2.Zero)
            }
            isX = !isX
        }

        g.restore()
    }

    private fun drawSimple(g: IFigureGraphics) {
        var x = 0
        var y = 0
        var isX = true

        val res = mutableListOf<Vec2>(Vec2.Zero)
        for(s in sideCounter){
            if (isX){
                x+=s
            } else{
                y+=s
            }
            res.add(Vec2(x*size, y*size))
            isX = !isX
        }
        g.drawPolyline(res)
    }

    private fun getNextReverse(isX: Boolean, s: Int, next: Int) = if (isX) {
        when {
            s < 0 -> next > 0
            else -> next < 0
        }
    } else {
        when {
            s < 0 -> next < 0
            else -> next > 0
        }
    }

//    private fun drawY(g: IFigureGraphics, s2: Double, count: Int, boardHeight: Double) {
//        for (y in 0 until count) {
//
//            val st = if (y == 0 && cornerRadius > 0.0) {
//                cornerRadius
//            } else {
//                0.0
//            }
//            val en = if (y == count - 1 && cornerRadius > 0.0) {
//                size - cornerRadius
//            } else {
//                size
//            }
//            drawZig(g, st, s2, en, zigY)
//            g.translate(size, 0.0)
//        }
//    }

    private fun drawX(g: IFigureGraphics, s2: Double, count: Int, zig:IFigure, boardHeight: Double, isDrop:Boolean, isPredDrop:Boolean, isNextDrop: Boolean, nextReverse: Boolean, predReverse:Boolean) {

        if (isDrop && boardHeight>0.0){
            g.translate( 0.0, boardHeight)
        }
        for (x in 0 until count) {
            val st = if (x == 0) {
                if (predReverse){
              //      g.drawCircle(Vec2.Zero, 3.0)
                    if (isPredDrop) {
                        -boardHeight
                    }
                    else 0.0
                }else {

                    val p = if (isPredDrop) {
                        boardHeight + cornerRadius
                    } else {
                        cornerRadius
                    }
                    p
                }
            } else {
                0.0
            }

            val en = if (x == count - 1) {
                if (nextReverse){
                  //  g.drawCircle(Vec2(size, 0.0), 5.0)
                    if (isNextDrop) {
                        size + boardHeight
                    } else {
                        size
                    }
                }else {
                    val p = if (isNextDrop) {
                        size - cornerRadius - boardHeight
                    } else {
                        size - cornerRadius
                    }

                    if (cornerRadius > 0.0) {
                        g.drawArc(
                            Vec2(p, cornerRadius),
                            cornerRadius,
                            cornerRadius,
                            -PI / 2,
                            PI / 2
                        )
                    }
                    p
                }


            } else {
                size
            }

            if (isLine(x, count)) {
                g.drawLine(Vec2(st, 0.0), Vec2(en, 0.0))
            } else {
                drawZig(g, st, s2, en, zig)
            }
            g.translate(size, 0.0)
        }
        if (isDrop && boardHeight>0.0){
            g.translate( 0.0, -boardHeight)
        }

    }

    private fun isLine(x: Int, count: Int): Boolean {
        if (zigFirstIndex> count/2){
            return !(if(count %2 == 0) {
                (x == count / 2 || x == count/2-1)
            }else{
                (x == count/2)
            })
        }
        return !(if (x == zigFirstIndex || x == count - zigFirstIndex-1)
            true
        else {
            if (zigDistance<=0)
                true
            else
                (x> zigFirstIndex && x<count - zigFirstIndex-1 ) && (x - zigFirstIndex) % zigDistance == 0
        })
    }

    private fun drawZig(
        g: IFigureGraphics,
        st: Double,
        s2: Double,
        en: Double,
        zig:IFigure
    ) {
        g.drawLine(Vec2(st, 0.0), Vec2(s2, 0.0))
        zig.draw(g)
        g.drawLine(Vec2(size - s2, 0.0), Vec2(en, 0.0))
    }

    override fun print(): String {
        return "/cube $size (${sideCounter.joinToString(" ")}) (${zigInfo.commandLine()})"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "кубик $size ${sideCounter.size}"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

}