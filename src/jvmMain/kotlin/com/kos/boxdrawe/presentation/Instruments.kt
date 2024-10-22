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
    const val INSTRUMENT_NONE = 0

    val NONE = InstrumentState(INSTRUMENT_NONE)

    const val POINTER_LEFT = 1
    const val POINTER_RIGHT = 2
    const val POINTER_MIDDLE = 4
    const val POINTER_NONE = 0
}

data class InstrumentState(
    val selectedInstrument: Int
)