package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.complex.model.CubikDirection
import com.kos.tortoise.ZigzagInfo
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs

class FigureDirCubik(
    val size: Double,
    val sides: List<CubikDirection>,
    val zigInfo: ZigzagInfo,
    val cornerRadius:Double,
    val enableDrop: Boolean,
) : IFigure {

    private val bound : BoundingRectangle
    private var minX = 0
    private var minY = 0
    init {
        var minX = 0
        var maxX =0
        var minY = 0
        var maxY = 0
        var x = 0
        var y = 0

        val preSides = sides
        preSides.forEach { p ->
            x+=p.xSize
            if (x< minX)
                minX = x

            if (x> maxX)
                maxX = x

            y+=p.ySize
            if (y<minY)
                minY = y

            if (y>maxY)
                maxY = y
        }

        val bw = if (enableDrop) zigInfo.height else zigInfo.height*2
        bound = BoundingRectangle(
            Vec2(minX*size-bw, minY*size-bw),
            Vec2(maxX*size+bw, maxY*size+bw)
        )
        this.minX = minX
        this.minY = minY
       // println("> $minX $minY $maxX $maxY $sides")
    }

    override val count: Int
        get() = 1

    override fun rect(): BoundingRectangle {
        return bound
    }

    private val zigReverse =creteFigureReverse()

    private val zig = createFigure()

    fun createFigure():IFigure{
        val s2 = (size-zigInfo.width)/2
        return FigurePolyline(listOf(
            Vec2( s2,0.0),
            Vec2( s2, -zigInfo.height),
            Vec2(  size - s2, -zigInfo.height),
            Vec2( size - s2, 0.0),
        ))
    }

    fun creteFigureReverse(): Figure {
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
        if (sides.size<2) return

        if (g.isSimple()){
            drawSimple(g)
            return
        }

        val he = abs(zigInfo.height)
        val s2 = (size-zigInfo.width)/2
        val hee = if (enableDrop) abs(he) else 0.0

        g.save()
        var currentRotation = 0.0
        for (i in sides.indices){
            val tek = sides[i]
            val pred = if (i==0) sides.last() else sides[i-1]
            val next = if (i==sides.size-1) sides.first() else sides[i+1]
            val nt = next.direction - tek.direction

            val nextOut = (nt == 1 || nt == -3)
            val nextArc = nextOut

            val pt = tek.direction - pred.direction

            val predVirez = (pt == 0 && tek.isInnerCorner && !pred.isInnerCorner)
            val nextVirez = (nt == 0 && tek.isInnerCorner && !next.isInnerCorner)

            // enm
            val enm = calculateOffsetEnd(next,hee, nt, nextVirez)

            val tp = if (enableDrop && !tek.isFlat && !tek.isReverse){
                hee
            } else 0.0

            // stn
            val stn = calculateOffsetStart(pred,  hee, pt, predVirez)

            // eline

            val line = if (nt == 0){
                var eline = 0.0
                if (tek.isInnerCorner)
                    eline+=hee
                if (enableDrop && !tek.isFlat && !tek.isReverse)
                    eline-=hee
                if (next.isInnerCorner)
                    eline-=hee
                if (enableDrop && !next.isFlat && !next.isReverse){
                    eline+=hee
                }
                if (nextVirez)
                    eline-=hee
                if (!tek.isInnerCorner && next.isInnerCorner)
                    eline+=hee

                eline
            } else 0.0





            // -----------------
            val newRotation = when(tek.direction){
                CubikDirection.DIRECTION_LEFT -> 180.0
                CubikDirection.DIRECTION_DOWN -> 90.0
                CubikDirection.DIRECTION_RIGHT -> 0.0
                CubikDirection.DIRECTION_UP -> -90.0
                else -> 0.0
            }
            if (currentRotation != newRotation){
                g.rotate(newRotation-currentRotation, Vec2.Zero)
                currentRotation = newRotation
            }

            val count = abs(tek.count)

            if (tp!= 0.0){
                g.translate(0.0, tp)
            }

            if (tek.isInnerCorner){
                g.translate(0.0, -hee)
            }

            if (predVirez){
                g.drawLine(Vec2( hee, 0.0), Vec2(hee, hee))
                g.drawLine(Vec2( hee, hee), Vec2(0.0, hee))
            }

            if (tek.isFlat){
                val st = stn
                val en = (count-1)*size + enm


                g.drawLine(Vec2(st, 0.0), Vec2(en, 0.0))
                g.translate(size*count, 0.0)
            } else {

                val sigForm = if (tek.isReverse) zigReverse else zig

                for (j in 0 until count) {
                    val st = if (j == 0) stn else 0.0
                    val en = if (j < count - 1) {
                        size
                    } else {
                        enm
                    }

                    drawZig(g, st, s2, size - s2, en, sigForm)
                    g.translate(size, 0.0)
                }
            }

            if (nextVirez){
                g.drawLine(Vec2( -hee, 0.0), Vec2(-hee, hee))
                g.drawLine(Vec2( -hee, hee), Vec2(0.0, hee))
                if (line!=0.0){
                    g.drawLine(Vec2(0.0, hee), Vec2(0.0, hee+line))
                }
            } else{
                if (line!=0.0){
                    g.drawLine(Vec2(0.0, 0.0), Vec2(0.0, line))
                }
            }

            if (nextArc){
                val p = enm-size
                g.drawArc(
                    Vec2(p, cornerRadius),
                    cornerRadius,
                    cornerRadius,
                    -PI / 2,
                    PI / 2
                )
            }


            if (tek.isInnerCorner) {

                g.translate(0.0, hee)
            }

            if (tp!= 0.0){
                g.translate(0.0, -tp)
            }

        } //end for sides
        g.restore()
//        val tekColor = g.getColor()
//        g.setColor(0xF00FF00)
//        g.drawRect(bound.min, Vec2(bound.width, bound.height))
//        g.setColor(tekColor)
    }

    private fun calculateOffsetStart(
        pred: CubikDirection,
        hee: Double,
        pt: Int,
        isVirez: Boolean,
    ): Double {
        val predOut = (pt == 1 || pt == -3)
        val predIn = (pt == -1 || pt == 3)
        val predArc = predOut

        var stn = 0.0

        if (predArc)
            stn += cornerRadius


        if (pred.isInnerCorner) {
            if (predOut)
                stn -= hee
            if (predIn)
                stn += hee
        }

        if (enableDrop && !pred.isFlat && !pred.isReverse) {
            if (predOut)
                stn += hee
            if (predIn)
                stn -= hee
        }

        if (isVirez)
            stn+=hee
        return stn
    }

    private fun calculateOffsetEnd(
        next: CubikDirection,
        hee: Double,
        nt: Int,
        isVirez: Boolean,
    ): Double {
        val nextOut = (nt == 1 || nt == -3)
        val nextIn = (nt == -1 || nt == 3)
        val nextArc = nextOut

        var enm = size
        if (nextArc)
            enm -= cornerRadius

        if (next.isInnerCorner) {
            if (nextOut)
                enm += hee
            if (nextIn)
                enm -= hee
        }

        if (!next.isFlat && !next.isReverse && enableDrop) {
            if (nextOut)
                enm -= hee
            if (nextIn)
                enm += hee
        }

        if (isVirez)
            enm-=hee
        return enm
    }

    private fun drawSimple(g: IFigureGraphics) {
        var x = 0
        var y = 0
        var isX = true

        val res = mutableListOf<Vec2>(Vec2.Zero)
        for(s in sides){
            if (isX){
                x+=s.xSize
            } else{
                y+=s.ySize
            }
            res.add(Vec2(x*size, y*size))
            isX = !isX
        }
        g.drawPolyline(res)
    }

    override fun print(): String {
        return "/cube2 $size (${sides.joinToString(" ")}) (${zigInfo.commandLine()})"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "кубик2 $size ${sides.size}"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

    override fun removeInner(inner: IFigure): IFigure  = this

    override fun replaceInner(newCollection: List<IFigure>): IFigure = this

    private fun drawZig(
        g: IFigureGraphics,
        st: Double,
        s2: Double,
        se:Double,
        en: Double,
        zig:IFigure
    ) {
        g.drawLine(Vec2(st, 0.0), Vec2(s2, 0.0))
        zig.draw(g)
        g.drawLine(Vec2(se, 0.0), Vec2(en, 0.0))
    }


}

