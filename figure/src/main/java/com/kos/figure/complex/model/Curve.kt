package com.kos.figure.complex.model

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import vectors.Vec2

private const val DEFAULT_STEP_SIZE = 1000

interface Curve : PathElement {
    val p0: Vec2
    val p1: Vec2
    val p2: Vec2
    val p3: Vec2

    override val start: Vec2
        get() = p0
    override val end: Vec2
        get() = p3

    override fun translate(xy: Vec2): Curve {
        return CurveImpl(
            p0 + xy,
            p1 + xy,
            p2 + xy,
            p3 + xy
        )
    }

    /**
     * Creates a sub-Bezier curve from the current curve based on start and end offsets as percentages of the curve's length.
     *
     * @param startOffset The starting offset as a percentage (0.0 to 1.0).
     * @param endOffset The ending offset as a percentage (0.0 to 1.0).
     * @return A new BezierCurve representing the sub-curve.
     */
    fun subBezier(startOffset: Double, endOffset: Double): Curve {
        require(startOffset in 0.0..1.0) { "Start offset must be between 0.0 and 1.0" }
        require(endOffset in 0.0..1.0) { "End offset must be between 0.0 and 1.0" }
        require(startOffset <= endOffset) { "Start offset must be less than or equal to end offset" }

        // Calculate intermediate points using De Casteljau's algorithm
        val t1 = startOffset
        val t2 = endOffset

        val q0 = p0
        val q1 = p0 * (1 - t1) + p1 * t1
        val q2 = p1 * (1 - t1) + p2 * t1
        val q3 = p2 * (1 - t1) + p3 * t1

        val r0 = q0 * (1 - t2) + q1 * t2
        val r1 = q1 * (1 - t2) + q2 * t2
        val r2 = q2 * (1 - t2) + q3 * t2

        val s0 = r0 * (1 - t2) + r1 * t2
        val s1 = r1 * (1 - t2) + r2 * t2

        val newP0 = s0
        val newP1 = s0 * (1 - t2) + s1 * t2
        val newP2 = s1
        val newP3 = r2

        return CurveImpl(newP0, newP1, newP2, newP3)
    }

    /**
     * Calculates the normal vector to the Bezier curve at the given parameter t (0.0 to 1.0).
     *
     * @param t The parameter value at which to calculate the normal (0.0 to 1.0).
     * @return The normal vector at the specified point.
     */
    fun normalAt(t: Double): Vec2 {
        val tangent = tangentAt(t)
        return Vec2(-tangent.y, tangent.x) // Rotate tangent by 90 degrees
    }

    /**
     * Calculates the tangent vector to the Bezier curve at the given parameter t (0.0 to 1.0).
     *
     * @param t The parameter value at which to calculate the tangent (0.0 to 1.0).
     * @return The tangent vector at the specified point.
     */
    private fun tangentAt(t: Double): Vec2 {
        val tSquared = t * t
        val oneMinusT = 1 - t
        val oneMinusTSquared = oneMinusT * oneMinusT

        val tangent =
            (p0 * -3.0 * oneMinusTSquared +
                    p1 * 3.0 * (oneMinusTSquared - 2 * t * oneMinusT) +
                    p2 * 3.0 * (2 * t * oneMinusT - tSquared) +
                    p3 * 3.0 * tSquared)

        return tangent.normalize() // Normalize the tangent vector
    }

    /**
     * Creates a parallel BezierCurve offset from the original curve by a given distance.*
     * @param offsetDistance The distance to offset the curve. Positive values offset to the "right" (following the curve's direction),
     * negative values offset to the "left."
     * @param numSamples The number of points to sample along the curve to calculate offsets (higher values provide more accuracy).
     * @return A new BezierCurve parallel to the original, offset by the given distance.
     */
    fun parallelCurve(offsetDistance: Double, numSamples: Int = 100): Curve {
        if (isStraightLine()) {
            // Optimized for straight lines
            val direction = (p3 - p0).normalize()
            val normal = Vec2(-direction.y, direction.x)
            val offset = normal * offsetDistance
            return Curve(
                p0 + offset,
                p1 + offset,
                p2 + offset,
                p3 + offset
            )
        } else {
            val newPoints = mutableListOf<Vec2>()
            for (i in 0..numSamples) {
                val t = i.toDouble() / numSamples
                val point = pointAt(t)
                val normal = normalAt(t)
                newPoints.add(point + normal * offsetDistance)
            }

            // Fit a new Bezier curve to the offset points (you'll need to implement this)
            return fitBezierCurveToPoints(newPoints, 0)
        }
    }

    private fun isStraightLine(): Boolean {
        val direction1 = (p1 - p0).normalize()
        val direction2 = (p2 - p1).normalize()
        val direction3 = (p3 - p2).normalize()
        return direction1 == direction2 && direction2 == direction3
    }

    /**
     * Calculates the point on the Bezier curve at the given parameter t (0.0 to 1.0).
     *
     * @param t The parameter value (0.0 to 1.0).
     * @return The point on the curve at the specified parameter.
     */
    fun pointAt(t: Double): Vec2 {
        val oneMinusT = 1 - t
        val tSquared = t * t
        val oneMinusTSquared = oneMinusT * oneMinusT
        return (p0 * oneMinusTSquared * oneMinusT +
                p1 * 3.0 * oneMinusTSquared * t +
                p2 * 3.0 * oneMinusT * tSquared +
                p3 * tSquared * t)
    }


    companion object {
        operator fun invoke(p0: Vec2, p1: Vec2, p2: Vec2, p3: Vec2): Curve {
            return CurveImpl(p0, p1, p2, p3)
        }

        fun fitBezierCurveToPoints(points: List<Vec2>, startIndex:Int): Curve {
            val size = 4
            // Calculate parameter values for each point (assuming uniform distribution)
            val tValues = (0..points.size - 1).map { it.toDouble() / (points.size - 1) }

            // Build the matrix A and vector b for the least-squares system
            val A = Array(size) { DoubleArray(4) }
            val b = Array(size) { DoubleArray(2) }
            for (i in startIndex until startIndex + size) {
                val t = tValues[i]
                val oneMinusT = 1 - t
                val tSquared = t * t
                val oneMinusTSquared = oneMinusT * oneMinusT
                A[i][0] = oneMinusTSquared * oneMinusT
                A[i][1] = 3 * oneMinusTSquared * t
                A[i][2] = 3 * oneMinusT * tSquared
                A[i][3] = tSquared * t
                b[i][0] = points[i].x
                b[i][1] = points[i].y
            }

            // Solve the least-squares system to find the control points
            val controlPoints = solveLeastSquares(A, b)

            return CurveImpl(
                Vec2(controlPoints[0][0], controlPoints[0][1]),
                Vec2(controlPoints[1][0], controlPoints[1][1]),
                Vec2(controlPoints[2][0], controlPoints[2][1]),
                Vec2(controlPoints[3][0], controlPoints[3][1])
            )
        }

        private fun solveLeastSquares(
            A: Array<DoubleArray>,
            b: Array<DoubleArray>
        ): Array<DoubleArray> {
            val numPoints = A.size
            val numControlPoints = A[0].size

            // Create a RealMatrix from the matrix A
            val matrixA = Array2DRowRealMatrix(A)

            // Create a RealVector for each dimension of b (x and y)
            val vectorBx = ArrayRealVector(DoubleArray(numPoints) { b[it][0] })
            val vectorBy = ArrayRealVector(DoubleArray(numPoints) { b[it][1] })

            // Solve the least-squares problem for each dimension
            val regressionX = OLSMultipleLinearRegression()
            regressionX.newSampleData(vectorBx.toArray(), matrixA.data)
            val coefficientsX = regressionX.estimateRegressionParameters()

            val regressionY = OLSMultipleLinearRegression()
            regressionY.newSampleData(vectorBy.toArray(), matrixA.data)
            val coefficientsY = regressionY.estimateRegressionParameters()

            // Combine the coefficients into a single array
            return Array(numControlPoints) { i ->
                doubleArrayOf(coefficientsX[i], coefficientsY[i])
            }
        }
    }
}


class CurveIter(private val points: List<Vec2>,var index:Int): Curve {
    // Todo Need optimize get point list
    override val p0: Vec2 get() = points[index+0]
    override val p1: Vec2 get() = points[index+1]
    override val p2: Vec2 get() = points[index+2]
    override val p3: Vec2 get() = points[index+3]

    private val length: Double by lazy { Vec2.bezierLength(points, index) }

    override fun toFigure(): FigureBezier {
        return FigureBezier(points.subList(index, index+4))
    }

    override fun toPath(): IFigurePath = toFigure()

    override fun perimeter(): Double {
        return length
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return Vec2.bezierPosition(points.subList(index, index+4), delta, DEFAULT_STEP_SIZE, length)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val pe = length
        if (pe <= 0.0)
            return FigureEmpty

        val a = (startMM / pe).coerceIn(0.0, 1.0)
        val b = (endMM / pe).coerceIn(0.0, 1.0)

        val sec = Vec2.casteljauLine(points.subList(index, index+4), a, b)
        return FigureBezier(sec)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawBezier(points.subList(index, index+4))
    }
}

data class CurveImpl(
    override val p0: Vec2,
    override val p1: Vec2,
    override val p2: Vec2,
    override val p3: Vec2
) : Curve {
    val points = listOf(p0, p1, p2, p3)

    private val length: Double by lazy { Vec2.bezierLength(points) }

    override fun toFigure(): FigureBezier {
        return FigureBezier(points)
    }

    override fun toPath(): IFigurePath = toFigure()

    override fun perimeter(): Double {
        return length
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return Vec2.bezierPosition(points, delta, DEFAULT_STEP_SIZE, length)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val pe = length
        if (pe <= 0.0)
            return FigureEmpty

        val a = (startMM / pe).coerceIn(0.0, 1.0)
        val b = (endMM / pe).coerceIn(0.0, 1.0)

        val sec = Vec2.casteljauLine(points, a, b)
        return FigureBezier(sec)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawBezier(points)
    }
}