package com.kos.boxdrawe.presentation.model

interface BlockModifier

data class ColorBlockModifier(
    val dxfColor:Int,
) :  BlockModifier{

}