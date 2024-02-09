package com.kos.boxdrawer.detal.box

data class BoxInfo(
    val width : Double,
    val height: Double ,
    val weight: Double ,
    val heights: List<Double> = emptyList()
) {

    fun commandLine():String{
        return "$width $weight $height"
    }
}