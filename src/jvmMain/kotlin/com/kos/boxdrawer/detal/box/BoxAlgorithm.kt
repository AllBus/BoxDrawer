package com.kos.boxdrawer.detal.box

import figure.IFigure
import turtoise.*

class BoxAlgorithm(
    val boxInfo: BoxInfo,
    val zigW: ZigzagInfo,
    val zigH: ZigzagInfo,
    val zigWe: ZigzagInfo,
    val wald: WaldParam,
) : TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String> = listOf("box")

    override fun draw(name: String, ds: DrawerSettings, runner: TortoiseRunner): IFigure {
        return BoxCad.box(
            runner.state.xy,
            boxInfo,
            zigW,
            zigH,
            zigWe,
            ds,
            wald,
            PolkaSort()
        )
    }


}