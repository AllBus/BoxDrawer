package com.kos.boxdrawe.presentation.model

data class BindingType(
    val intersection: Boolean,
    val grid: Boolean,
    val nearest: Boolean,
    val points: Boolean,
) {
}