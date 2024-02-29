package com.kos.boxdrawer.detal.box

import com.kos.boxdrawer.detal.box.BoxAlgorithm.Companion.pazName
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseFigureAlgorithm
import turtoise.TortoiseParser
import turtoise.TurtoiseParserStackBlock

data class WaldParam(
    val topOffset: Double,
    val bottomOffset: Double,

    val holeBottomOffset: Double,
    val holeTopOffset: Double,
    val holeEdgeOffset: Double,
    val holeWeight: Double,

    val topForm: PazForm,
    val bottomForm: PazForm,
    val edgeForm: PazForm,

    val bottomRoundRadius: Double,
    val topRoundRadius: Double,
    val edgeTopRoundRadius: Double,
    val zigFigure: TortoiseAlgorithm?,
    val zagFigure: TortoiseAlgorithm?,
) {
    val verticalOffset: Double get() = topOffset + bottomOffset

    fun fullBottomOffset(boardWeight: Double): Double =
        offsetValue(bottomForm, holeBottomOffset, boardWeight)

    fun fullTopOffset(boardWeight: Double): Double =
        offsetValue(topForm, holeTopOffset, boardWeight)

    fun offsetValue(form: PazForm, offset: Double, boardWeight: Double): Double {
        return when (form) {
            PazForm.None -> 0.0 + offset
            PazForm.Paz -> boardWeight
            PazForm.Hole -> holeWeight + offset
            PazForm.BackPaz -> boardWeight
            PazForm.Flat -> boardWeight + offset
            PazForm.Paper -> 0.0
            PazForm.Outside -> 0.0
        }
    }

    fun commandLine(): String {
        return "(b ${pazName(bottomForm)} $holeBottomOffset $bottomOffset $bottomRoundRadius)" +
                "(t ${pazName(topForm)} $holeTopOffset $topOffset $topRoundRadius)" +
                "(e ${pazName(edgeForm)} $holeEdgeOffset 0 $edgeTopRoundRadius)" +
                "(h $holeWeight)"

    }

    companion object {
        fun from(block: TurtoiseParserStackBlock): WaldParam {
            val bottom = block.getBlockAtName("b")?:TurtoiseParserStackBlock()
            val top = block.getBlockAtName("t")?:TurtoiseParserStackBlock()
            val edge = block.getBlockAtName("e")?:TurtoiseParserStackBlock()
            val hole = block.getBlockAtName("h")?:TurtoiseParserStackBlock()
            val figure = block.getBlockAtName("z")
            val zagFigure = block.getBlockAtName("Z")

            return WaldParam(
                bottomForm = BoxAlgorithm.parsePazForm(bottom.get(1), PazForm.Paz),
                topForm = BoxAlgorithm.parsePazForm(top.get(1), PazForm.None),
                edgeForm = BoxAlgorithm.parsePazForm(edge.get(1), PazForm.Paz),
                bottomOffset = TortoiseParser.asDouble(bottom.get(3)),
                topOffset = TortoiseParser.asDouble(top.get(3)),
                holeBottomOffset = TortoiseParser.asDouble(bottom.get(2)),
                holeTopOffset = TortoiseParser.asDouble(top.get(2)),
                holeEdgeOffset = TortoiseParser.asDouble(edge.get(2)),
                holeWeight = TortoiseParser.asDouble(hole.get(1)),
                bottomRoundRadius = TortoiseParser.asDouble(bottom.get(4)),
                topRoundRadius = TortoiseParser.asDouble(top.get(4)),
                edgeTopRoundRadius = TortoiseParser.asDouble(edge.get(4)),
                zigFigure = figure?.let{ f -> TortoiseFigureAlgorithm("_", f) },
                zagFigure = zagFigure?.let{ f -> TortoiseFigureAlgorithm("_", f) },
            )
        }
    }
}

data class WaldForm(
    val form: PazForm,
    val roundRadius: Double,
    val holeOffset: Double,
)