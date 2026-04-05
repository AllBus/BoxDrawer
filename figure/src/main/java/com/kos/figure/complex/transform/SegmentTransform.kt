package com.kos.figure.complex.transform

import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigurePath
import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.Segment
import vectors.Vec2
//
//fun Arc.toFigure(): FigureCircle {
//    return FigureCircle(
//        center = center,
//        radius = radius,
//        outSide = outSide,
//        segmentStartAngle = startAngle,
//        segmentSweepAngle = sweepAngle,
//    )
//}
//
//fun Arc.toPath(): IFigurePath = toFigure()
//
//fun Arc.take(startMM: Double, endMM: Double): Figure {
//    val pe = perimeter()
//    if (pe <= 0 || endMM <= startMM)
//        return FigureEmpty
//    val st = startMM / pe
//    val en = endMM / pe
//
//    val ste = st.coerceIn(0.0, 1.0)
//    val end = en.coerceIn(0.0, 1.0)
//    return FigureCircle(
//        center = center,
//        radius = radius,
//        outSide = outSide,
//        segmentStartAngle = Math.toRadians(startAngle + sweepAngle * ste),
//        segmentSweepAngle = Math.toRadians(sweepAngle * (end - ste))
//    )
//}
//
//fun Curve.toFigure(): FigureBezier {
//    val points = listOf(p0, p1, p2, p3)
//    return FigureBezier(points)
//}
//
//fun Curve.toPath(): IFigurePath = toFigure()
//
//
//fun Curve.take(startMM: Double, endMM: Double): Figure {
//    val points = listOf(p0, p1, p2, p3)
//    val pe = perimeter()
//    if (pe <= 0.0)
//        return FigureEmpty
//
//    val a = (startMM / pe).coerceIn(0.0, 1.0)
//    val b = (endMM / pe).coerceIn(0.0, 1.0)
//
//    val sec = Vec2.casteljauLine(points, a, b)
//    return FigureBezier(sec)
//}
//
//
//fun Ellipse.toFigure(): FigureEllipse {
//    return FigureEllipse(
//        center = center,
//        radius = radiusX,
//        radiusMinor = radiusY,
//        rotation = rotation,
//        outSide = outSide,
//        segmentStartAngle = startAngle,
//        segmentSweepAngle = sweepAngle,
//    )
//}
//
//fun Ellipse.toPath(): IFigurePath = toFigure()
//
//
//fun Ellipse.take(startMM: Double, endMM: Double): Figure {
//    val st = startMM / length
//    val end = endMM / length
//    //Todo: Вычислить правильный сегмент
//    return FigureEllipse(
//        center = center,
//        radius = radiusX,
//        radiusMinor = radiusY,
//        rotation = rotation,
//        outSide = outSide,
//        segmentStartAngle = startAngle + sweepAngle * st,
//        segmentSweepAngle = sweepAngle * (end - st),
//    )
//}
//
//fun Segment.toFigure(): FigureLine {
//    return FigureLine(start, end)
//}
//
//fun Segment.toPath(): IFigurePath = toFigure()
//
//
//fun Segment.take(startMM: Double, endMM: Double): Figure {
//    val d = Vec2.distance(start, end)
//
//    if (d <= 0.0 || endMM <= startMM)
//        return FigureEmpty
//
//    val sm = startMM.coerceIn(0.0, d)
//    val em = endMM.coerceIn(0.0, d)
//
//    return FigureLine(
//        Vec2.lerp(start, end, sm / d),
//        Vec2.lerp(start, end, em / d)
//    )
//}