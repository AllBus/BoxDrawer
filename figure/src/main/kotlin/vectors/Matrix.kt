package vectors

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
}