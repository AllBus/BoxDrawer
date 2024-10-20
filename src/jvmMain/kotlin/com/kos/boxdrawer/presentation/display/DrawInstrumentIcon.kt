package com.kos.boxdrawer.presentation.display

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import com.kos.boxdrawe.icons.InstrumentIcon
import com.kos.boxdrawe.presentation.Instruments

@Composable
fun DrawInstrumentIcon(instrument:Int){
    when (instrument){
        Instruments.INSTRUMENT_NONE -> {}
        Instruments.INSTRUMENT_LINE -> {
            Icon(
                imageVector = InstrumentIcon.rememberLine(),
                contentDescription = "Line"
            )
        }
        Instruments.INSTRUMENT_RECTANGLE -> {
            Icon(
                imageVector = InstrumentIcon.rememberRectangle(),
                contentDescription = "Rectangle"
            )
        }
        Instruments.INSTRUMENT_CIRCLE -> {
            Icon(
                imageVector = InstrumentIcon.rememberCircle(),
                contentDescription = "Circle"
            )
        }
        Instruments.INSTRUMENT_POLYGON ->{
            Icon(
                imageVector = InstrumentIcon.rememberPolygon(),
                contentDescription = "Polygon"
            )
        }
        Instruments.INSTRUMENT_POLYLINE ->{
            Icon(
                imageVector = InstrumentIcon.rememberPolyLine(),
                contentDescription = "Polyline"
            )
        }
        Instruments.INSTRUMENT_BEZIER -> {
            Icon(
                imageVector = InstrumentIcon.rememberBezier(),
                contentDescription = "Bezier"
            )

        }
    }
}