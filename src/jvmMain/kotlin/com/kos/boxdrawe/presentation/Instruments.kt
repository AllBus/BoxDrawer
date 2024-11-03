package com.kos.boxdrawe.presentation

object Instruments {
    const val INSTRUMENT_LINE = 1
    const val INSTRUMENT_RECTANGLE = 2
    const val INSTRUMENT_POLYGON = 3
    const val INSTRUMENT_CIRCLE = 4
    const val INSTRUMENT_ELLIPSE = 5
    const val INSTRUMENT_POLYLINE = 6
    const val INSTRUMENT_BEZIER = 7
    const val INSTRUMENT_TRIANGLE = 8
    const val INSTRUMENT_BEZIER_TREE_POINT = 9
    const val INSTRUMENT_POINTER = 100
    const val INSTRUMENT_MULTI = 11
    const val INSTRUMENT_NONE = 0

    val NONE = InstrumentState(INSTRUMENT_NONE)

    const val POINTER_LEFT = 1
    const val POINTER_RIGHT = 2
    const val POINTER_MIDDLE = 4
    const val POINTER_NONE = 0
    const val MODIFIER_ALT = 0x100
    const val MODIFIER_CTRL = 0x200
    const val MODIFIER_SHIFT = 0x400

    fun button(value:Int): Int = value and 0xf
}

data class InstrumentState(
    val selectedInstrument: Int
)