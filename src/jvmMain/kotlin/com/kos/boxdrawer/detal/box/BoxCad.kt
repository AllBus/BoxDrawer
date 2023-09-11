package com.kos.boxdrawer.detal.box

import figure.FigureList
import figure.FigurePolyline
import figure.IFigure
import turtoise.*
import turtoise.Tortoise.Companion.holes
import turtoise.Tortoise.Companion.zigzag
import vectors.Vec2


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
            wald
        )
    }
}

object BoxCad {

    fun faceWald(
        origin: Vec2, width: Double, height: Double,
        zigzag: ZigzagInfo, hole: ZigzagInfo,
        param: DrawingParam,
        boardWeight: Double,
        wald: WaldParam,
    ): List<IFigure> {
        val result = mutableListOf<IFigure>()

        val points = mutableListOf<Vec2>()

        var p = DrawingParam().apply {
            orientation = Orientation.Vertical
            reverse = !param.reverse
            back = false
        };

        points.add(origin)
        points.add(origin + Vec2(0.0, wald.bottomOffset))
        zigzag(points, points.last(), height - wald.verticalOffset, zigzag, 0.0, p, boardWeight)
        points.add(origin + Vec2(0.0, height))

        if (wald.topForm != PazForm.None) {
            val ph = DrawingParam().apply {
                orientation = Orientation.Horizontal
                reverse = true
                back = false
            };

            val dwe = if (param.reverse) boardWeight else 0.0
            val sp = points.last() + Vec2(dwe, 0.0);
            val le = width - 2 * dwe;

            when (wald.topForm) {
                PazForm.Paz ->
                    zigzag(points, sp, le, hole, 0.0, ph, boardWeight);
                PazForm.Hole ->
                    result.addAll(
                        holes(
                            sp - Vec2(0.0, wald.holeOffset),
                            le,
                            hole,
                            0.0,
                            ph,
                            wald.holeWeight
                        )
                    );
                else -> {}
            }
        }

        p = DrawingParam().apply {
            orientation = Orientation.Vertical
            reverse = param.reverse
            back = true
        }

        points.add(origin + Vec2(width, height))

        points.add(origin + Vec2(width, height - wald.topOffset))
        zigzag(points, points.last(), height - wald.verticalOffset, zigzag, 0.0, p, boardWeight)
        points.add(origin + Vec2(width, 0.0))

        if (wald.bottomForm != PazForm.None) {
            val ph = DrawingParam().apply {
                orientation = Orientation.Horizontal
                reverse = false
                back = true
            }

            val dwe = if (param.reverse) boardWeight else 0.0
            val sp = points.last() - Vec2(dwe, 0.0);
            val le = width - 2.0 * dwe;

            when (wald.bottomForm) {
                PazForm.Paz ->
                    zigzag(points, sp, le, hole, 0.0, ph, boardWeight);

                PazForm.Hole ->
                    result.addAll(
                        holes(
                            sp + Vec2(0.0, wald.holeOffset),
                            le,
                            hole,
                            0.0,
                            ph,
                            wald.holeWeight
                        )
                    )

                else -> {}
            }
        }

        points.add(origin)
        result.add(FigurePolyline(points.toList()))
        return result.toList()
    }

    fun pol(
        width: Double,
        height: Double,
        origin: Vec2,
        zigzagW: ZigzagInfo,
        zigzagH: ZigzagInfo,
        boardWeight: Double
    ): IFigure {
        val points = mutableListOf<Vec2>()
        points.add(origin)

        zigzag(
            points,
            points.last(),
            height,
            zigzagH,
            0.0,
            DrawingParam().apply {
                orientation = Orientation.Vertical
                reverse = true
                back = false
            },
            boardWeight
        );
        zigzag(
            points,
            points.last(),
            width,
            zigzagW,
            0.0,
            DrawingParam().apply {
                orientation = Orientation.Horizontal
                reverse = false
                back = false
            },
            boardWeight
        )
        zigzag(
            points,
            points.last(),
            height,
            zigzagH,
            0.0,
            DrawingParam().apply {
                orientation = Orientation.Vertical
                reverse = false
                back = true
            },
            boardWeight
        );
        zigzag(
            points,
            points.last(),
            width,
            zigzagW,
            0.0,
            DrawingParam().apply {
                orientation = Orientation.Horizontal
                reverse = true
                back = true
            },
            boardWeight
        );

        return FigurePolyline(points)
    }

    fun box(
        startPoint: Vec2,
        boxInfo: BoxInfo,
        zigW: ZigzagInfo,
        zigH: ZigzagInfo,
        zigWe: ZigzagInfo,
        drawerSettings: DrawerSettings,
        waldParams: WaldParam,
    ): IFigure {

        val ap = drawerSettings.boardWeight * 2
        val p = DrawingParam().apply {
            orientation = Orientation.Horizontal
            reverse = false
            back = false
        }

        val wald = waldParams.copy(
            holeWeight = if (waldParams.holeWeight == 0.0) drawerSettings.holeWeight else waldParams.holeWeight,
            holeOffset = if (waldParams.holeOffset == 0.0) drawerSettings.holeOffset else waldParams.holeOffset,
        )

        val weight = boxInfo.weight;
        val height = boxInfo.height;
        val width = boxInfo.width;

        val pFL = startPoint + Vec2(0.0, weight + 1)
        val pFR = startPoint + Vec2(0.0, weight + height * 1 + 2)
        val pFT = startPoint + Vec2(0.0, weight + height * 2 + 3)
        val pFB = startPoint + Vec2(0.0, weight + height * 3 + 4)
        val pHole = startPoint + Vec2(0.0, drawerSettings.holeOffset)
        val pTopHole = startPoint + Vec2(0.0, height - drawerSettings.holeOffset)
        val pPol = startPoint + Vec2(0.0, 0.0)
        val pTop = startPoint + Vec2(0.0, -weight - 1.0)
        var pPolka = startPoint + Vec2(0.0, 0.0)

        val bw = drawerSettings.boardWeight

        val holeW = ZigzagInfo(
            width = zigW.width - drawerSettings.holeDrop,
            delta = zigW.delta
        )

        val holeH = ZigzagInfo(
            width = zigH.width - drawerSettings.holeDrop,
            delta = zigH.delta
        )

        val holeWe = ZigzagInfo(
            width = zigWe.width - drawerSettings.holeDrop,
            delta = zigWe.delta
        )

        val list = mutableListOf<IFigure>()

        list.addAll(faceWald(pFL, width - ap, height, zigH, holeW, p, bw, wald));
        list.addAll(faceWald(pFR, width - ap, height, zigH, holeW, p, bw, wald));

        p.reverse = true

        list.addAll(faceWald(pFT, weight, height, holeH, holeWe, p, bw, wald));
        list.addAll(faceWald(pFB, weight, height, holeH, holeWe, p, bw, wald));

        if (wald.bottomForm != PazForm.None) {
            list.add(pol(
                width = width - ap,
                height = weight - ap,
                origin = pPol,
                zigzagW = zigW,
                zigzagH = zigWe,
                boardWeight = bw
            ));
        }
        if (wald.topForm != PazForm.None) {
            list.add(pol(
                width = width - ap,
                height = weight - ap,
                origin = pTop,
                zigzagW = zigW,
                zigzagH = zigWe,
                boardWeight = bw
            ));
            pPolka += pTop;
        }

        return FigureList(list.toList())
    }


    private val sep = charArrayOf(' ', '\t', ';', '\n')
    public fun readBox(line: String, defaultWidth: Double, defaultHeight: Double, defaultWeight: Double): BoxInfo {

        val a = line.split(*sep).filter { it.isNotEmpty() }

        return BoxInfo(
            width = a.getOrNull(1)?.toDoubleOrNull() ?: defaultWidth,
            height = a.getOrNull(2)?.toDoubleOrNull() ?: defaultHeight,
            weight = a.getOrNull(3)?.toDoubleOrNull() ?: defaultWeight,
        )
    }

}