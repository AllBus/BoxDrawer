package turtoise

/**
 *
 *  ----|_____|----|____|----
 *      ^width^  delta  ^
 *  @param width ширина одного паза
 *  @param delta расстояние между началом паза до начала следующего паза
 *  @param height высота паза
 *  @param enable Риосваание паза включено
 */
data class ZigzagInfo(
    val width: Double,
    val delta: Double,
    val height: Double = 0.0,
    val enable: Boolean = true,
) {
    fun commandLine(): String {
        return "$width $delta $height ${if (!enable) "false" else ""}"
    }
}