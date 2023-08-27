package vectors

import kotlin.math.*

data class Vec2(@JvmField val x: Double,@JvmField val y: Double) {

    operator fun plus(other: Vec2): Vec2 {
        return Vec2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(x - other.x, y - other.y)
    }

    operator fun times(k: Double): Vec2 {
        return Vec2(x * k, y * k)
    }

    operator fun div(k: Double): Vec2 {
        return Vec2(x / k, y / k)
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-x, -y)
    }

    fun rotate(angle: Double): Vec2 = Vec2(
        cos(angle) * x - sin(angle) * y,
        cos(angle) * y + sin(angle) * x
    )

    override fun toString(): String {
        return "($x $y)"
    }

    companion object {

        val Zero get() = Vec2(0.0, 0.0)

        fun lerp(a: Vec2, b: Vec2, t: Double): Vec2 {
            return Vec2(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
        }

        fun calcYPosition(a: Vec2, b: Vec2, k: Double): Double {
            return a.y + (k - a.x) * (b.y - a.y) / (b.x - a.x)
        }

        fun calcXPosition(a: Vec2, b: Vec2, k: Double): Double {
            return a.x + (k - a.y) * (b.x - a.x) / (b.y - a.y)
        }

        fun coordForX(a: Vec2, b: Vec2, x: Double): Vec2 {
            return Vec2(x, calcYPosition(a, b, x))
        }

        fun coordForY(a: Vec2, b: Vec2, y: Double): Vec2 {
            return Vec2(calcXPosition(a, b, y), y)
        }

        fun rotate(v: Vec2, angle: Double): Vec2 {
            return Vec2(
                cos(angle) * v.x - sin(angle) * v.y,
                cos(angle) * v.y + sin(angle) * v.x
            )
        }

        fun casteljau(p: List<Vec2>, t: Double): Vec2 {
            val A = p[0]
            val B = p[1]
            val C = p[2]
            val D = p[3]
            val E = lerp(A, B, t)
            val F = lerp(B, C, t)
            val G = lerp(C, D, t)
            val H = lerp(E, F, t)
            val J = lerp(F, G, t)
            val K = lerp(H, J, t)
            return K
        }

        fun casteljauLine(p: List<Vec2>, t: Double): Pair<List<Vec2>, List<Vec2>> {
            val A = p[0]
            val B = p[1]
            val C = p[2]
            val D = p[3]
            val E = lerp(A, B, t)
            val F = lerp(B, C, t)
            val G = lerp(C, D, t)
            val H = lerp(E, F, t)
            val J = lerp(F, G, t)
            val K = lerp(H, J, t)
            return Pair(listOf(A, E, H, K), listOf(K, J, G, D))
        }

        fun casteljauLine(p: List<Vec2>, t: List<Double>): List<List<Vec2>> {
            var pp = p
            val result = mutableListOf<List<Vec2>>()
            for (tt in t.sorted()) {
                val l2 = casteljauLine(pp, tt)
                result.add(l2.first)
                pp = l2.second
            }
            result.add(pp)
            return result.toList()
        }

        fun accept(t: Double) = t in 0.0..1.0

        fun cuberoot(v: Double): Double {
            if (v < 0)
                return -(-v).pow(1.0 / 3.0)
            return v.pow(1.0 / 3.0)
        }

        fun approximately(a: Double, b: Double) = abs(a - b) < 0.001

        fun getCubicRoots(p: List<Double>): List<Double> {
            return getCubicRoots(p[0], p[1], p[2], p[3])
        }

        fun getCubicRoots(pa: Double, pb: Double, pc: Double, pd: Double): List<Double> {
            var a = (3 * pa - 6 * pb + 3 * pc)
            var b = (-3 * pa + 3 * pb)
            var c = pa
            val d = (-pa + 3 * pb - 3 * pc + pd)

            return (if (approximately(d, 0.0)) {
                // this is not a cubic curve.
                if (approximately(a, 0.0)) {

                    if (approximately(b, 0.0)) {
                        emptyList<Double>()
                    } else {
                        // linear solution
                        listOf(-c / b)
                    }
                } else {
                    // quadratic solution
                    val q = sqrt(b * b - 4 * a * c)
                    val a2 = 2 * a
                    listOf((q - b) / a2, (-b - q) / a2)
                }
            } else {
                //  at this point, we know we need a cubic solution.
                a /= d
                b /= d
                c /= d

                val p = (3 * b - a * a) / 3
                val p3 = p / 3
                val q = (2 * a * a * a - 9 * a * b + 27 * c) / 27.0
                val q2 = q / 2
                val discriminant = q2 * q2 + p3 * p3 * p3

                //and some variables we 're going to use later on:


                if (discriminant < 0) {
                    //  three possible real roots :
                    val mp3 = -p / 3
                    val mp33 = mp3 * mp3 * mp3
                    val r = sqrt(mp33)
                    val t = -q / (2 * r)
                    val cosphi = if (t < -1) -1.0 else if (t > 1) 1.0 else t
                    val phi = acos(cosphi)
                    val crtr = cuberoot(r)
                    val t1 = 2 * crtr;
                    val root1 = t1 * cos(phi / 3) - a / 3
                    val root2 = t1 * cos((phi + 2 * PI) / 3) - a / 3
                    val root3 = t1 * cos((phi + 4 * PI) / 3) - a / 3
                    listOf(root1, root2, root3)
                } else
                    if (discriminant == 0.0) {
                        // three real roots, but two of them are equal:
                        val u1 = if (q2 < 0) cuberoot(-q2) else -cuberoot(q2)
                        val root1 = 2 * u1 - a / 3
                        val root2 = -u1 - a / 3
                        listOf(root1, root2)
                    } else {
                        // one real root, two complex roots
                        val sd = sqrt(discriminant)
                        val u1 = cuberoot(sd - q2)
                        val v1 = cuberoot(sd + q2)
                        val root1 = u1 - v1 - a / 3
                        listOf(root1)
                    }
            }).filter(::accept).sorted()
        }
    }
}