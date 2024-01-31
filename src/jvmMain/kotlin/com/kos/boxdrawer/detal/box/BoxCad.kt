package com.kos.boxdrawer.detal.box

import figure.FigureList
import figure.FigurePolygon
import figure.FigurePolyline
import figure.IFigure
import turtoise.*
import turtoise.Tortoise.Companion.holes
import turtoise.Tortoise.Companion.zigzag
import vectors.Vec2
import kotlin.math.min


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

    /**
     * @param startOrigin Координаты где нарисована полка начала
     * @param endOrigin Координаты где нарисована полка конца
     * @param polOrigin: Координаты где нарисован пол
     */
    fun polkaWald(
    origin: Vec2,
     po: Polka,  start: Polka?,  end: Polka?,  inter:List<Polka>,
     height:Double, zigzag: ZigzagInfo,
    hasPolPaz: Boolean,
    polOrigin: Vec2,
    startOrigin: Vec2,
    endOrigin: Vec2,
    drawerSettings: DrawerSettings,
    boardWeight:Double,
    ):PolkaResult{
        val dwe2 = drawerSettings.holeWeight / 2
        val or = po.orientation

        var originForPol: Vec2 = polOrigin
        var startOrigin = startOrigin
        var endOrigin = endOrigin


        if (or == Orientation.Horizontal) {
            originForPol += Vec2(po.calc.sX, po.calc.sY + dwe2)
            startOrigin += Vec2(po.calc.sY + dwe2, 0.0)
            endOrigin += Vec2(po.calc.sY + dwe2, 0.0)
        } else {
            originForPol += Vec2(po.calc.sX + dwe2, po.calc.sY)
            startOrigin += Vec2(po.calc.sX + dwe2, 0.0)
            endOrigin += Vec2(po.calc.sX + dwe2, 0.0)
        }

        val result = PolkaResult()

        val points = mutableListOf<Vec2>()

        val heStart = po.heightForLine(0, height)
        var he0 = height
        var csx = 0.0
        var ssx = 0.0
        if (start != null)
        {
            he0 = start.heightForLine(po.calc.index - start.startCell, height);
            ssx += drawerSettings.boardWeight / 2;
        }
        else
        {
            ssx = 0.0;//= drawerSettings.boardWeight;
        }
        he0 = min(heStart, he0);

        if (or == Orientation.Horizontal)
        {
            csx = po.calc.sX;
        }
        else
        {
            csx = po.calc.sY;
        }

        //Левый край
        points.add(origin + Vec2(ssx, 0.0));
        var currentPoint = startOrigin;
        zigzag(points, points.last(), he0, zigzag, 0.0, DrawingParam().apply {
            this.orientation = Orientation.Vertical
            reverse = true
            back = false
        },
            boardWeight
        );
        result.startHole.addAll(holes(currentPoint, he0, zigzag, 0.0,
            DrawingParam().apply {
                orientation = Orientation.Vertical
                reverse = true
                back = false
            },
            boardWeight

        ));
        //Левый верхний угол
        points.add(origin + Vec2(ssx, 0.0) + Vec2(0.0, heStart));

        // Верхний край
        // Верхний край
        var cx: Double
        for (i in 0 until inter.size) {
            val c = inter[i]
            val he = po.heightForLine(i + 1, height)
            val che = c.heightForLine(po.calc.index - c.startCell, height)
            var needSig = false
            if (or == Orientation.Horizontal) {
                cx = c.calc.sX - csx
            } else {
                cx = c.calc.sY - csx
                if (c.visible) {
                    if (eps(che, he)) needSig = true
                }
            }
            if (needSig) {
                points.add(origin + Vec2(cx - dwe2, he))
                points.add(origin + Vec2(cx - dwe2, he / 2))
                points.add(origin + Vec2(cx + dwe2, he / 2))
                points.add(origin + Vec2(cx + dwe2, he))
            } else {
                points.add(origin + Vec2(cx, he))
            }
        }

        //Правый верхний угол
        //Правый верхний угол
        val endhe = po.heightForLine(inter.size + 1, height)
        var esx = 0.0
        he0 = height
        if (end != null) {
            he0 = end.heightForLine(po.calc.index - end.startCell, height)
            esx += drawerSettings.boardWeight / 2
        } else {
            esx = 0.0 // 2*drawerSettings.boardWeight;
        }
        he0 = min(endhe, he0)

        cx = if (or == Orientation.Horizontal) {
            po.calc.eX - csx - esx
        } else {
            po.calc.eY - csx - esx
        }

        points.add(origin + Vec2(cx, endhe))

        // Правый край
        points.add(origin + Vec2(cx, he0));

        currentPoint = Vec2(0.0, points.last().y - origin.y) + endOrigin
        zigzag(
            points, points.last(), he0, zigzag, 0.0,
            DrawingParam().apply {
                orientation = Orientation.Vertical
                reverse = false
                back = true
            },
            boardWeight,
        );
        result.endHole.addAll(holes(currentPoint, he0, zigzag, 0.0, DrawingParam().apply {
            orientation = Orientation.Vertical
            reverse = true
            back = true
        },
            boardWeight));
        //Правый нижний угол
        //Правый нижний угол
        points.add(origin + Vec2(cx, 0.0))

        //Нижний край
        //Нижний край
        var pred = cx
        for (i in inter.size - 1 downTo 0) {
            val c = inter[i]
            var he = po.heightForLine(i + 1, height)
            var needZig = false
            var che = c.heightForLine(po.calc.index - c.startCell, height)
            var df = he / 2

            if (or == Orientation.Horizontal)
            {
                cx = c.calc.sX - csx;
                if (c.visible)
                {
                    if (che > he)
                    {
                        needZig = false;
                    }
                    else
                    {
                        needZig = true;
                        if (che < he)
                        {
                            df = che;
                        }
                        else
                            df = he / 2;
                    }
                }
            }else
            {
                if (c.visible)
                {
                    if (!eps(che, he))
                    {
                        if (che < he)
                        {
                            needZig = true;
                            df = che;
                        }
                    }
                }

                cx = c.calc.sY - csx;
            }
            if (needZig)
            {


                if (hasPolPaz)
                {
                    val d = pred - (cx + dwe2);
                    currentPoint = getCoord(po, points.last() - origin) + originForPol;
                    zigzag(points, points.last(), d, zigzag, 0.0, DrawingParam().apply { orientation = Orientation.Horizontal; reverse = true; back = true }, boardWeight);
                    result.polHole.addAll(holes(currentPoint, d, zigzag, 0.0, DrawingParam().apply { orientation = or; reverse = true; back = true }, boardWeight));
                }

                points.add(origin + Vec2(cx + dwe2, 0.0));
                points.add(origin + Vec2(cx + dwe2, df));
                points.add(origin + Vec2(cx - dwe2, df));
                points.add(origin + Vec2(cx - dwe2, 0.0));
                pred = cx - dwe2;
            }
        } // for

        if (hasPolPaz)
        {
            val d = pred - ssx;
            currentPoint = getCoord(po, points.last() - origin) + originForPol;
            zigzag(points, points.last(), d, zigzag, 0.0,  DrawingParam().apply{ orientation = Orientation.Horizontal; reverse = true;back = true },boardWeight);
            result.polHole.addAll(holes(currentPoint, d, zigzag, 0.0,  DrawingParam().apply { orientation = or; reverse = true; back = true },boardWeight));
        }
        result.polka = FigurePolyline(points, true)
        return result
    }

    private fun getCoord(polka: Polka, point: Vec2): Vec2 {
        return if (polka.orientation == Orientation.Horizontal) Vec2(point.x, 0.0) else Vec2(0.0, point.x)
    }
    private fun eps(a: Double, b: Double): Boolean {
        return Math.abs(a - b) < 0.01
    }

    fun box(
        startPoint: Vec2,
        boxInfo: BoxInfo,
        zigW: ZigzagInfo,
        zigH: ZigzagInfo,
        zigWe: ZigzagInfo,
        drawerSettings: DrawerSettings,
        waldParams: WaldParam,
        polki: PolkaSort,
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
        val bottomOffset = wald.holeOffset //Todo:
        val polkaVerticalOffset = wald.holeOffset //Todo:
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

        list.addAll(faceWald(
            origin = pFL,
            width = width - ap,
            height = height,
            zigzag = zigH,
            hole = holeW,
            param = p,
            boardWeight = bw,
            wald = wald
        ));
        list.addAll(faceWald(
            origin = pFR,
            width = width - ap,
            height = height,
            zigzag = zigH,
            hole = holeW,
            param = p,
            boardWeight = bw,
            wald = wald
        ));

        p.reverse = true

        list.addAll(faceWald(
            origin = pFT,
            width = weight,
            height = height,
            zigzag = holeH,
            hole = holeWe,
            param = p,
            boardWeight = bw,
            wald = wald
        ));
        list.addAll(faceWald(
            origin = pFB,
            width = weight,
            height = height,
            zigzag = holeH,
            hole = holeWe,
            param = p,
            boardWeight = bw,
            wald = wald
        ));

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


        val oby = Vec2(0.0, bottomOffset)
        val hbx = Vec2(.0, 0.0)
        val bx = Vec2(drawerSettings.boardWeight, 0.0)
        for ( po in polki.calcList){
            if (po.visible){
                val inter: List<Polka> = polki.intersectList(po)
                val start = polki.findStart(po)
                val end= polki.findEnd(po)

                var startOrigin: Vec2
                var endOrigin: Vec2
                val pPo: Vec2 = calculateDrawPosition(po, pPolka, polki, height, drawerSettings)

                if (start == null) {
                    startOrigin = oby + if (po.orientation === Orientation.Vertical) pFL + hbx else pFT + bx
                } else {
                    startOrigin = calculateDrawPosition(start, pPolka, polki, height, drawerSettings)
                }

                if (end == null) {
                    endOrigin = oby + if (po.orientation === Orientation.Vertical) pFR + hbx else pFB + bx
                } else {
                    endOrigin = calculateDrawPosition(end, pPolka, polki, height, drawerSettings)
                }

                val result = polkaWald(
                    origin = pPo,
                    po = po,
                    start = start,
                    end = end,
                    inter = inter,
                    height = height - polkaVerticalOffset,
                    zigzag = ZigzagInfo(
                        width = polki.pazWidth,
                        delta = polki.pazDelta,
                    ),
                    hasPolPaz = wald.bottomForm != PazForm.None,
                    polOrigin = pPol,
                    startOrigin = startOrigin,
                    endOrigin = endOrigin,
                    drawerSettings = drawerSettings,
                    boardWeight = bw
                )

                result.polka?.let {
                    list.add(it)
                }
                list.addAll(result.polHole);
                list.addAll(result.startHole);
                list.addAll(result.endHole);
            }

        }


        return FigureList(list.toList())
    }

    fun calculateDrawPosition(
        polka: Polka ,
        origin: Vec2 ,
        polki: PolkaSort ,
        height: Double ,
        drawerSettings:DrawerSettings
    ): Vec2
    {
        var y = 0.0;
        for (po in polki.calcList)
        {
            y += po.maxHeight(height) + drawerSettings.boardWeight + 1;
            if (po == polka)
                break
        }

        return Vec2(origin.x, origin.y - y);
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