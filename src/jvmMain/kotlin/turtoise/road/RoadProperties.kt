package turtoise.road

import com.kos.boxdrawer.detal.box.BoxCad
import com.kos.figure.Figure
import turtoise.ZigzagInfo
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

class RoadProperties(
    val width: Double,
    val startHeight: Double,
    val count: Int,
    val outStyle: BoxCad.EOutVariant,
    val zigzagInfo: ZigzagInfo,
    val connectStyle: EBoardConnectStyle,
    val isHoleLine: Boolean,
    val style: ERoadStyle,
    val zigazagModel : TortoiseParserStackItem?,
    val holeModel : TortoiseParserStackItem?,
    val ups: RoadUps?,
)

class RoadUps(
    val left: RoadUp?,
    val right: RoadUp?,
    val top:RoadUp?
)

class RoadUp(
    val height:Double,
    val radius:Double,
    val figure: TortoiseParserStackItem?,
)