package com.kos.boxdrawer.detal.box


enum class PazForm {
    None,
    Paz,
    Hole,
    BackPaz,
    Flat,
}

object PazExt{

    val PAZ_NONE = 0
    val PAZ_PAZ = 1
    val PAZ_HOLE = 2
    val PAZ_BACK = 3
    val PAZ_FLAT = 4
    fun intToPaz(pazId:Int):PazForm {
        return when(pazId){
            PAZ_NONE -> PazForm.None
            PAZ_PAZ -> PazForm.Paz
            PAZ_HOLE -> PazForm.Hole
            PAZ_BACK -> PazForm.BackPaz
            PAZ_FLAT -> PazForm.Flat
            else -> PazForm.None
        }
    }
}