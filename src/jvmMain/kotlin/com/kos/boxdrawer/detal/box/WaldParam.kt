package com.kos.boxdrawer.detal.box

data class WaldParam(
    val topOffset: Double,
    val bottomOffset: Double,

    val holeBottomOffset: Double,
    val holeTopOffset: Double,
    val holeWeight: Double,

    val topForm: PazForm,
    val bottomForm: PazForm,
){
    val verticalOffset: Double get() = topOffset + bottomOffset

    fun fullBottomOffset(boardWeight:Double) :Double = offsetValue(bottomForm, holeBottomOffset, boardWeight)

    fun fullTopOffset(boardWeight:Double) :Double = offsetValue(topForm, holeTopOffset, boardWeight)

    fun offsetValue(form: PazForm, offset:Double, boardWeight:Double): Double{
        return when(form){
            PazForm.None -> 0.0+offset
            PazForm.Paz -> boardWeight
            PazForm.Hole -> holeWeight+offset
            PazForm.BackPaz -> boardWeight
            PazForm.Flat -> boardWeight+offset
        }
    }
}