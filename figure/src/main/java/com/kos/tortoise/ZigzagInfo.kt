package com.kos.tortoise

/**
 *
 *  ----|_____|----|____|----
 *      ^width^  delta  ^
 *  @param width ширина одного паза
 *  @param delta расстояние между началом паза до начала следующего паза
 *  @param height высота паза
 *  @param enable Риосваание паза включено
 *  @param drop Уменьшение ширины паза. (Только для выичлений. Полная ширина в width)
 */
data class ZigzagInfo(
    val width: Double,
    val delta: Double,
    val height: Double = 0.0,
    val enable: Boolean = true,
    val drop: Double = 0.0,
    val fromCorner: Boolean = false,
) {
    val dropedWidth = width - drop

    fun commandLine(): String {
        return "$width $delta $height ${if (!enable) "false" else ""}"
    }
}