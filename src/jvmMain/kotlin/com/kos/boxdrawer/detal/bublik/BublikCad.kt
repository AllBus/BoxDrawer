package com.kos.boxdrawer.detal.bublik

import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawe.presentation.BublikPaz
import com.kos.figure.FigureCircle
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Matrix44
import turtoise.DrawerSettings
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin


class BublikCad {
    fun torus(
        radius: Double, torRadius: Double, ringPart: Int, stenaPart: Int,
        bublikPaz: BublikPaz,
        drawerSettings: DrawerSettings,
    ): IFigure {
        val list: MutableList<IFigure> = mutableListOf<IFigure>()
        val xcount = stenaPart
        val ycount = ringPart

        val teta = Math.PI * 2 / xcount;
        val alpha = Math.PI * 2 / ycount;

        val a = 2 * torRadius * sin(teta / 2);


        var predB = 0.0;
        var b = 0.0;

        for (i in 0..xcount) {

            val ca = torRadius * cos(teta * i)
            val ua = radius - ca

            predB = b
            b = 2 * ua * sin(alpha / 2)
            if (i > 0) {
                list.addAll(trapecija(Vec2(0.0, -i * a), b, predB, a, bublikPaz, drawerSettings))
            }
        }

        if (bublikPaz.center) {
            list.add(polygon(Vec2(torRadius + predB, 3 * torRadius + 1), stenaPart, torRadius))
        }
        list.addAll(torusCircle(Vec2(torRadius + predB, torRadius), torRadius, stenaPart, a, drawerSettings))


        return FigureList(list.toList())
    }

    private fun polygon(offset: Vec2, sideCount: Int, radius: Double): IFigure {
        val zig = 8
        val zihe = 3.0
        val zig2 = zig.toDouble() / 2

        val r2 = radius * cos(Math.PI / sideCount)
        val list = mutableListOf<Vec2>()
        for (i in 0..< sideCount) {
            val alpha = Math.PI * 2 * i / sideCount
            val delta = Math.PI * 2 * (i + 0.5) / sideCount
            val sy = sin(alpha)
            val sx = cos(alpha)

            list.add(Vec2(sx * radius, sy * radius) + offset);

            val m3 = Matrix()
                m3.rotateZ(((delta - Math.PI / 2)*180/ Math.PI).toFloat());

            list.addAll(
                listOf(
                    m3 * Vec3(-zig2, r2, 1.0),
                    m3 * Vec3(-zig2, r2 + zihe, 1.0),
                    m3 * Vec3(zig2, r2 + zihe, 1.0),
                    m3 * Vec3(zig2, r2, 1.0),
                ).map { v3 -> Vec2(-v3.x, v3.y) + offset }
            )
        }
        return FigurePolyline(list, true)
    }

    private fun torusCircle(
        offset: Vec2,
        radius: Double,
        ringPart: Int,
        edge: Double,
        drawerSettings: DrawerSettings,
    ): Collection<IFigure> {
        val list = mutableListOf<IFigure>()
        val dist = 5;
        val zig = 8;
        val zihe = drawerSettings.holeWeight;

        list.add(FigureCircle(offset, radius + zihe + drawerSettings.holeOffset))
        var dizig = dist + zig;


        for (i in 0 until ringPart) {
            val alpha = Math.PI * 2 * i / ringPart
            val beta = Math.PI * 2 * (i + 1) / ringPart
            val delta = Math.PI * 2 * (i + 0.5) / ringPart
            val rad = radius
            val sy = Math.sin(alpha)
            val sx = Math.cos(alpha)
            val uy = Math.sin(beta)
            val ux = Math.cos(beta)
            val oy = Math.sin(delta)
            val ox = Math.cos(delta)
            val cxy = Vec2(sx * rad, sy * rad)
            val exy = Vec2(ux * rad, uy * rad)
            val zig2 = zig.toDouble() / 2
            val zag2 = zihe / 2
            var oxy: Vec2 = (exy - cxy) * (dist + zig2).toDouble() / edge + offset + cxy

            val m3 = Matrix()
                m3.rotateZ(((delta + Math.PI / 2)*180/ Math.PI).toFloat());

            list.add(FigurePolyline(listOf(
                m3 * Vec3(-zig2, 0.0, 1.0),
                m3 * Vec3(-zig2, -zihe, 1.0),
                m3 * Vec3(zig2, -zihe, 1.0),
                m3 * Vec3(zig2, 0.0, 1.0),
            ).map{v3 -> Vec2(-v3.x, v3.y) + oxy}, true))

            oxy = (cxy - exy) * (dist + zig2) / edge + offset + exy;

            list.add(FigurePolyline(listOf(
                m3 * Vec3(-zig2, 0.0, 1.0),
                m3 * Vec3(-zig2, -zihe, 1.0),
                m3 * Vec3(zig2, -zihe, 1.0),
                m3 * Vec3(zig2, 0.0, 1.0),
            ).map{v3 -> Vec2(-v3.x, v3.y) + oxy}, true))
        }

        list.add(FigureCircle(offset, radius - zihe - drawerSettings.holeOffset))
        return list
    }

    private fun trapecija(
        offset: Vec2,
        top: Double,
        bottom: Double,
        edge: Double,
        bublikPaz: BublikPaz,
        drawerSettings: DrawerSettings,
    ): List<IFigure> {
        val d = (bottom - top) / 2
        val h = Math.sqrt(edge * edge - d * d)

        val dista = 5.0
        val zig = 8.0
        val zihe = 3.0


        val dx = d / edge
        val dy = h / edge
        val list = mutableListOf<Vec2>()
        list.add(Vec2(0.0, 0.0) + offset)
        list.add(Vec2(top, 0.0) + offset)

        var dist = dista
        var dizig = dista + zig

        if (bublikPaz.rightTop) {
            dist = dista
            dizig = dist + zig
            list.add(Vec2(top + dist * dx, 0 + dist * dy) + offset)
            list.add(Vec2(top + dist * dx + zihe * dy, 0 + dist * dy - zihe * dx) + offset)
            list.add(Vec2(top + dizig * dx + zihe * dy, 0 + dizig * dy - zihe * dx) + offset)
            list.add(Vec2(top + dizig * dx, 0 + dizig * dy) + offset)
        }
        if (bublikPaz.rightBottom) {
            dizig = edge - dista
            dist = dizig - zig
            list.add(Vec2(top + dist * dx, 0 + dist * dy) + offset)
            list.add(Vec2(top + dist * dx + zihe * dy, 0 + dist * dy - zihe * dx) + offset)
            list.add(Vec2(top + dizig * dx + zihe * dy, 0 + dizig * dy - zihe * dx) + offset)
            list.add(Vec2(top + dizig * dx, 0 + dizig * dy) + offset)
        }

        list.add(Vec2(top + d, h) + offset)
        list.add(Vec2(-d, h) + offset)

        if (bublikPaz.leftBottom) {
            dist = dista
            dizig = dist + zig
            list.add(Vec2(-d + dist * dx, h - dist * dy) + offset)
            list.add(Vec2(-d + (dist * dx - zihe * dy), h - (dist * dy + zihe * dx)) + offset)
            list.add(Vec2(-d + (dizig * dx - zihe * dy), h - (dizig * dy + zihe * dx)) + offset)
            list.add(Vec2(-d + dizig * dx, h - dizig * dy) + offset)
        }

        if (bublikPaz.leftTop) {
            dizig = edge - dista
            dist = dizig - zig
            list.add(Vec2(-d + dist * dx, h - dist * dy) + offset)
            list.add(Vec2(-d + (dist * dx - zihe * dy), h - (dist * dy + zihe * dx)) + offset)
            list.add(Vec2(-d + (dizig * dx - zihe * dy), h - (dizig * dy + zihe * dx)) + offset)
            list.add(Vec2(-d + dizig * dx, h - dizig * dy) + offset)
        }


        return if (bublikPaz.center) {

            val zi2 = drawerSettings.holeWeight / 2;
            val zig2 = (zig - drawerSettings.holeDrop) / 2;
            listOf(
                FigurePolyline(list.toList(), true),
                FigurePolyline(
                    listOf(
                        Vec2(top / 2 - zi2, h / 2 - zig2) + offset,
                        Vec2(top / 2 + zi2, h / 2 - zig2) + offset,
                        Vec2(top / 2 + zi2, h / 2 + zig2) + offset,
                        Vec2(top / 2 - zi2, h / 2 + zig2) + offset,
                    ), true
                )
            )
        } else {
            listOf(FigurePolyline(list.toList(), true))
        }
    }
}