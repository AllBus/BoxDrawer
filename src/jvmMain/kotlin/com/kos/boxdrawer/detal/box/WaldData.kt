package com.kos.boxdrawer.detal.box

class WaldData(
    val hasTopHole: Boolean,
    val hasBottomHole: Boolean,
    val hasZig: Boolean,
    val upHeight: Double,
) {
}

class WaldList(
    val walds: Map<Int, List<WaldData>>
) {
    operator fun get(side: Int): List<WaldData> = walds.getOrElse(side) { emptyList<WaldData>() }
}