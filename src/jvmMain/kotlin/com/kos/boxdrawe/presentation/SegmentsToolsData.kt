package com.kos.boxdrawe.presentation

import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import vectors.Vec2

class SegmentsToolsData(val tools: ITools) {

    val figures = MutableStateFlow<IFigure>(FigureEmpty)

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument

    fun changeInstrument(value: Int) {
//        appendFigure()
        _instrument.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val instrumentState = _instrument.mapLatest { v ->
        InstrumentState(v)
    }

    fun clear(){

    }

    fun onPress(point: Vec2, button: Int, scale: Float) {

    }

    fun onMove(point: Vec2, button: Int, scale: Float) {

    }

    fun onRelease(point: Vec2, button: Int, scale: Float) {

    }
}