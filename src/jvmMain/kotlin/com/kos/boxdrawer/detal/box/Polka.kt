package com.kos.boxdrawer.detal.box

import turtoise.Orientation

class Polka {

    var orientation: Orientation= Orientation.Horizontal
    var width = 0.0
    var height = DoubleArray(0)
    var order = 0
    var startCell = 0
    var cellCount = 0
    var visible: Boolean = true
}

class PolkaInfo(
    val sX: Double,
    val sY: Double,
    val eX: Double,
    val eY: Double,
    val index : Double
){

}

class WaldParam(
    val topOffset: Double,
    val bottomOffset: Double,

    val holeOffset: Double,
    val holeDrop: Double,
    val holeWeight: Double,

    val topForm: PazForm,
    val bottomForm: PazForm,
){
    val verticalOffset: Double get() = topOffset + bottomOffset
}