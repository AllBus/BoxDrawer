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
        Instruments.INSTRUMENT_MULTI -> {
            Icon(
                imageVector = InstrumentIcon.rememberLine(),
                contentDescription = "Multi"
            )
        }

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
        Instruments.INSTRUMENT_ELLIPSE -> {
            Icon(
                imageVector = InstrumentIcon.rememberEllipse(),
                contentDescription = "Ellipse"
            )
        }
        Instruments.INSTRUMENT_TRIANGLE -> {
            Icon(
                imageVector = InstrumentIcon.rememberTriangle(),
                contentDescription = "Triangle"
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
        Instruments.INSTRUMENT_BEZIER_TREE_POINT -> {
            Icon(
                imageVector = InstrumentIcon.rememberCurve(),
                contentDescription = "Bezier on three point"
            )
        }
        Instruments.INSTRUMENT_POINTER -> {
            Icon(
                imageVector = InstrumentIcon.rememberPointerArrow(),
                contentDescription = "Pointer"
            )
        }
    }
}