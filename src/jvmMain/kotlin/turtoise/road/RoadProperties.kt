package turtoise.road

import com.kos.boxdrawer.detal.box.BoxCad
import turtoise.ZigzagInfo

class RoadProperties(
    val width: Double,
    val startHeight: Double,
    val count: Int,
    val outStyle: BoxCad.EOutVariant,
    val zigzagInfo: ZigzagInfo,
    val connectStyle: EBoardConnectStyle,
)

