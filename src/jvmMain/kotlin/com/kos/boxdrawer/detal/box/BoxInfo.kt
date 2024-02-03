package com.kos.boxdrawer.detal.box

data class BoxInfo(
    var width : Double,
    var height: Double ,
    var weight: Double ,
) {

    fun commandLine():String{
        return "$width $weight $height"
    }
}