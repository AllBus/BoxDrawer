package vectors

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@kotlin.jvm.JvmInline
value class Matrix(
    val values: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
) {
    inline operator fun get(row: Int, column: Int) = values[(row * 4) + column]

    inline operator fun set(row: Int, column: Int, v: Float) {
        values[(row * 4) + column] = v
    }

    fun rotateX(degrees: Float) {
        val c = cos(degrees * PI / 180.0).toFloat()
        val s = sin(degrees * PI / 180.0).toFloat()

        val a01 = this[0, 1]
        val a02 = this[0, 2]
        val v01 = a01 * c - a02 * s
        val v02 = a01 * s + a02 * c

        val a11 = this[1, 1]
        val a12 = this[1, 2]
        val v11 = a11 * c - a12 * s
        val v12 = a11 * s + a12 * c

        val a21 = this[2, 1]
        val a22 = this[2, 2]
        val v21 = a21 * c - a22 * s
        val v22 = a21 * s + a22 * c

        val a31 = this[3, 1]
        val a32 = this[3, 2]
        val v31 = a31 * c - a32 * s
        val v32 = a31 * s + a32 * c

        this[0, 1] = v01
        this[0, 2] = v02
        this[1, 1] = v11
        this[1, 2] = v12
        this[2, 1] = v21
        this[2, 2] = v22
        this[3, 1] = v31
        this[3, 2] = v32
    }

    /**
     * Applies a [degrees] rotation around Y to `this`.
     */
    fun rotateY(degrees: Float) {
        val c = cos(degrees * PI / 180.0).toFloat()
        val s = sin(degrees * PI / 180.0).toFloat()

        val a00 = this[0, 0]
        val a02 = this[0, 2]
        val v00 = a00 * c + a02 * s
        val v02 = -a00 * s + a02 * c

        val a10 = this[1, 0]
        val a12 = this[1, 2]
        val v10 = a10 * c + a12 * s
        val v12 = -a10 * s + a12 * c

        val a20 = this[2, 0]
        val a22 = this[2, 2]
        val v20 = a20 * c + a22 * s
        val v22 = -a20 * s + a22 * c

        val a30 = this[3, 0]
        val a32 = this[3, 2]
        val v30 = a30 * c + a32 * s
        val v32 = -a30 * s + a32 * c

        this[0, 0] = v00
        this[0, 2] = v02
        this[1, 0] = v10
        this[1, 2] = v12
        this[2, 0] = v20
        this[2, 2] = v22
        this[3, 0] = v30
        this[3, 2] = v32
    }

    /**
     * Applies a [degrees] rotation around Z to `this`.
     */
    fun rotateZ(degrees: Float) {
        val c = cos(degrees * PI / 180.0).toFloat()
        val s = sin(degrees * PI / 180.0).toFloat()

        val a00 = this[0, 0]
        val a10 = this[1, 0]
        val v00 = c * a00 + s * a10
        val v10 = -s * a00 + c * a10

        val a01 = this[0, 1]
        val a11 = this[1, 1]
        val v01 = c * a01 + s * a11
        val v11 = -s * a01 + c * a11

        val a02 = this[0, 2]
        val a12 = this[1, 2]
        val v02 = c * a02 + s * a12
        val v12 = -s * a02 + c * a12

        val a03 = this[0, 3]
        val a13 = this[1, 3]
        val v03 = c * a03 + s * a13
        val v13 = -s * a03 + c * a13

        this[0, 0] = v00
        this[0, 1] = v01
        this[0, 2] = v02
        this[0, 3] = v03
        this[1, 0] = v10
        this[1, 1] = v11
        this[1, 2] = v12
        this[1, 3] = v13
    }

    /** Scale this matrix by [x], [y], [z] */
    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f) {
        this[0, 0] *= x
        this[0, 1] *= x
        this[0, 2] *= x
        this[0, 3] *= x
        this[1, 0] *= y
        this[1, 1] *= y
        this[1, 2] *= y
        this[1, 3] *= y
        this[2, 0] *= z
        this[2, 1] *= z
        this[2, 2] *= z
        this[2, 3] *= z
    }

    /** Translate this matrix by [x], [y], [z] */
    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        val t1 = this[0, 0] * x +
                this[1, 0] * y +
                this[2, 0] * z +
                this[3, 0]
        val t2 = this[0, 1] * x +
                this[1, 1] * y +
                this[2, 1] * z +
                this[3, 1]
        val t3 = this[0, 2] * x +
                this[1, 2] * y +
                this[2, 2] * z +
                this[3, 2]
        val t4 = this[0, 3] * x +
                this[1, 3] * y +
                this[2, 3] * z +
                this[3, 3]
        this[3, 0] = t1
        this[3, 1] = t2
        this[3, 2] = t3
        this[3, 3] = t4
    }


    operator fun times(vector: Vec2): Vec2 {
        return Vec2(
            get(0, 0) * vector.x +
                    get(0, 1) * vector.y +
                    get(0, 2) * 1 +
                    get(0, 3) * 1,
            get(1, 0) * vector.x +
                    get(1, 1) * vector.y +
                    get(1, 2) * 1 +
                    get(1, 3) * 1,
        )
    }


    private fun dot(m1: Matrix, row: Int, m2: Matrix, column: Int): Float {
        return m1[row, 0] * m2[0, column] +
                m1[row, 1] * m2[1, column] +
                m1[row, 2] * m2[2, column] +
                m1[row, 3] * m2[3, column]
    }

    companion object {
        val identity = Matrix()

        /**
         *       [   1    0    tx  ]
         *       [   0    1    ty  ]
         *       [   0    0    1   ]
         */
        fun translate(tx: Double, ty: Double): Matrix = Matrix(
            floatArrayOf(
                1f, 0f, 0f, tx.toFloat(),
                0f, 1f, 0f, ty.toFloat(),
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        )

        /**
         *        [   sx   0    0   ]
         *        [   0    sy   0   ]
         *        [   0    0    1   ]
         */
        fun scale(s: Double): Matrix = Matrix(
            floatArrayOf(
                s.toFloat(), 0f, 0f, 0f,
                0f, s.toFloat(), 0f, 0f,
                0f, 0f, s.toFloat(), 0f,
                0f, 0f, 0f, 1f
            )
        )

        fun scale(sx: Double, sy: Double): Matrix = Matrix(
            floatArrayOf(
                sx.toFloat(), 0f, 0f, 0f,
                0f, sy.toFloat(), 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        )
    }


}