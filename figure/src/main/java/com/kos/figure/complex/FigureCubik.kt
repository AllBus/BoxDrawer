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
    val countX: Int,
    val countY: Int,
    val zigInfo: ZigzagInfo,
    val cornerRadius:Double,
    val enableDrop: Boolean,
    val reverseX: Boolean,
    val reverseY: Boolean,
) : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2.Zero, Vec2(size*countX, size*countY))
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

        val he = abs(zigInfo.height)

        val hex = (enableDrop && reverseX)
        val hey = (enableDrop && reverseY)


        val hee = if (enableDrop) abs(he) else 0.0

        val s2 = (size-zigInfo.width)/2
        g.save()
        drawX(g, s2, countX, zigX,hee, hex, hey, hey)
        g.rotate(90.0, Vec2.Zero)
        drawX(g, s2, countY, zigY, hee, hey, hex , hex)
        g.rotate(90.0, Vec2.Zero)
        drawX(g, s2, countX, zigX, hee,  hex, hey, hey)
        g.rotate(90.0, Vec2.Zero)
        drawX(g, s2, countY, zigY, hee, hey, hex , hex)

        g.restore()
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

    private fun drawX(g: IFigureGraphics, s2: Double, count: Int, zig:IFigure, boardHeight: Double, isDrop:Boolean, isPredDrop:Boolean, isNextDrop: Boolean) {

        if (isDrop && boardHeight>0.0){
            g.translate( 0.0, boardHeight)
        }
        for (x in 0 until count) {
            val st = if (x == 0) {
                val p =  if (isPredDrop) {
                        boardHeight+cornerRadius
                    }else {
                        cornerRadius
                    }

                    if (cornerRadius > 0.0) {
                        g.drawArc(
                            Vec2(p, cornerRadius),
                            cornerRadius,
                            cornerRadius,
                            PI,
                            PI / 2
                        )
                    }
                    p

            } else {
                0.0
            }

            val en = if (x == count - 1) {
                if (isNextDrop) {
                    size - cornerRadius-boardHeight
                } else {
                    size - cornerRadius
                }
            } else {
                size
            }

            drawZig(g, st, s2, en, zig)
            g.translate(size, 0.0)
        }
        if (isDrop && boardHeight>0.0){
            g.translate( 0.0, -boardHeight)
        }

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
        return "/cube $size $countX $countY (${zigInfo.commandLine()})"
    }

    override fun collection(): List<IFigure> {
        return listOf(this)
    }

    override fun name(): String {
        return "cube $size $countX $countY"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

}