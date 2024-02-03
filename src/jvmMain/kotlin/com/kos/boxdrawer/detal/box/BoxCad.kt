package com.kos.boxdrawer.detal.box

import androidx.compose.ui.graphics.Matrix
import figure.*
import figure.composition.FigureColor
import figure.composition.FigureRotate
import figure.composition.FigureTranslate
import figure.matrix.Figure3dTransform
import turtoise.*
import turtoise.Tortoise.Companion.holes
import turtoise.Tortoise.Companion.zigzag
import vectors.Vec2
import kotlin.math.min


object BoxCad {

    enum class EOutVariant{
        COLUMN,
        ALTERNATIVE,
        VOLUME
    }
    fun faceWald(
        origin: Vec2, width: Double, height: Double,
        zigzag: ZigzagInfo, hole: ZigzagInfo,
        param: DrawingParam,
        boardWeight: Double,
        wald: WaldParam,
    ): List<IFigure> {
        val result = mutableListOf<IFigure>()

        val points = mutableListOf<Vec2>()

        var p = DrawingParam(
            orientation = Orientation.Vertical,
            reverse = !param.reverse,
            back = false
        )

        points.add(origin)
        points.add(origin + Vec2(0.0, wald.bottomOffset))
        zigzag(points, points.last(), height - wald.verticalOffset, zigzag, 0.0, p, boardWeight)
        points.add(origin + Vec2(0.0, height))

        if (wald.topForm != PazForm.None) {
            val ph = DrawingParam(
                orientation = Orientation.Horizontal,
                reverse = true,
                back = false,
            )

            val dwe = if (param.reverse) boardWeight else 0.0
            val sp = points.last() + Vec2(dwe, 0.0);
            val le = width - 2 * dwe;

            when (wald.topForm) {
                PazForm.Paz ->
                    zigzag(points, sp, le, hole, 0.0, ph, boardWeight);
                PazForm.Hole ->
                    result.addAll(
                        holes(
                            sp - Vec2(0.0, wald.holeTopOffset),
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

        p = DrawingParam(
            orientation = Orientation.Vertical,
            reverse = param.reverse,
            back = true,
        )

        points.add(origin + Vec2(width, height))

        points.add(origin + Vec2(width, height - wald.topOffset))
        zigzag(points, points.last(), height - wald.verticalOffset, zigzag, 0.0, p, boardWeight)
        points.add(origin + Vec2(width, 0.0))

        if (wald.bottomForm != PazForm.None) {
            val ph = DrawingParam(
                orientation = Orientation.Horizontal,
                reverse = false,
                back = true,
            )

            val dwe = if (param.reverse) boardWeight else 0.0
            val sp = points.last() - Vec2(dwe, 0.0);
            val le = width - 2.0 * dwe;

            when (wald.bottomForm) {
                PazForm.Paz ->
                    zigzag(points, sp, le, hole, 0.0, ph, boardWeight);

                PazForm.Hole ->
                    result.addAll(
                        holes(
                            sp + Vec2(0.0, wald.holeBottomOffset),
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
            DrawingParam(
                orientation = Orientation.Vertical,
                reverse = false,
                back = false,
            ),
            boardWeight
        );
        zigzag(
            points,
            points.last(),
            width,
            zigzagW,
            0.0,
            DrawingParam(
                orientation = Orientation.Horizontal,
                reverse = false,
                back = false,
            ),
            boardWeight
        )
        zigzag(
            points,
            points.last(),
            height,
            zigzagH,
            0.0,
            DrawingParam(
                orientation = Orientation.Vertical,
                reverse = true,
                back = true,
            ),
            boardWeight
        );
        zigzag(
            points,
            points.last(),
            width,
            zigzagW,
            0.0,
            DrawingParam(
                orientation = Orientation.Horizontal,
                reverse = true,
                back = true,
            ),
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
        polka: Polka,
        startPolka: Polka?,
        endPolka: Polka?,
        intersectPolki: List<Polka>,
        height: Double,
        zigzag: ZigzagInfo,
        zigzagPol: ZigzagInfo,
        hasPolPaz: Boolean,
        drawerSettings: DrawerSettings,
        boardWeight: Double,
        wald: WaldParam,
    ): PolkaResult {

        // Половина толщины доски для определения центра
        val dwe2 = drawerSettings.holeWeight / 2
        val or = polka.orientation

        var originForPol: Vec2 = Vec2.Zero
        var startOrigin = Vec2.Zero
        var endOrigin = Vec2.Zero

        val waldBottomOffset = wald.fullBottomOffset(boardWeight)

        val endBottomOffset = if (endPolka == null) waldBottomOffset else 0.0
        val startBottomOffset = if (startPolka == null) waldBottomOffset else 0.0
        val startCXY = if (startPolka == null) Vec2.Zero else Vec2(startPolka.calc.sX, startPolka.calc.sY)

        if (or == Orientation.Horizontal) {
            originForPol += Vec2(polka.calc.sX, polka.calc.sY + dwe2)
            startOrigin += Vec2(polka.calc.sY + dwe2 - startCXY.y, startBottomOffset)
            endOrigin += Vec2(polka.calc.sY + dwe2- startCXY.y, endBottomOffset)
        } else {
            originForPol += Vec2(polka.calc.sX - dwe2, polka.calc.sY)
            startOrigin += Vec2(polka.calc.sX + dwe2- startCXY.x, startBottomOffset)
            endOrigin += Vec2(polka.calc.sX + dwe2- startCXY.x, endBottomOffset)
        }

        val result = PolkaResult()

        val points = mutableListOf<Vec2>()

        val heStart = polka.heightForLine(0, height)
        var he0 = height
        var csx = 0.0
        var ssx = 0.0

        if (startPolka != null) {
            he0 = startPolka.heightForLine(polka.calc.index - startPolka.startCell, height);
            ssx += drawerSettings.boardWeight / 2
        } else {
            ssx = 0.0;//= drawerSettings.boardWeight;
        }
        he0 = min(heStart, he0);

        if (or == Orientation.Horizontal) {
            csx = polka.calc.sX;
        } else {
            csx = polka.calc.sY;
        }

        //Левый край
        points.add(origin + Vec2(ssx, 0.0));
        var currentPoint = startOrigin;

        zigzag(
            points, points.last(), he0, zigzag, 0.0, DrawingParam(
                orientation = Orientation.Vertical,
                reverse = false,
                back = false
            ),
            boardWeight
        );
        result.startHole.addAll(
            holes(
                currentPoint, he0, zigzag, 0.0,
                DrawingParam(
                    orientation = Orientation.Vertical,
                    reverse = false,
                    back = false,
                ),
                wald.holeWeight

            )
        );
        //Левый верхний угол
        points.add(origin + Vec2(ssx, 0.0) + Vec2(0.0, heStart));

        // Верхний край
        var cx: Double
        for (i in intersectPolki.indices) {
            val c = intersectPolki[i]
            val he = polka.heightForLine(i + 1, height)
            val che = c.heightForLine(polka.calc.index - c.startCell, height)
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
        val endhe = polka.heightForLine(intersectPolki.size + 1, height)
        var esx = 0.0
        he0 = height
        if (endPolka != null) {
            he0 = endPolka.heightForLine(polka.calc.index - endPolka.startCell, height)
            esx += drawerSettings.boardWeight / 2
        } else {
            esx = 0.0 // 2*drawerSettings.boardWeight;
        }
        he0 = min(endhe, he0)

        cx = if (or == Orientation.Horizontal) {
            polka.calc.eX - csx - esx
        } else {
            polka.calc.eY - csx - esx
        }

        points.add(origin + Vec2(cx, endhe))

        // Правый край
        points.add(origin + Vec2(cx, he0));

        currentPoint = Vec2(0.0, points.last().y - origin.y) + endOrigin
        zigzag(
            points, points.last(), he0, zigzag, 0.0,
            DrawingParam(
                orientation = Orientation.Vertical,
                reverse = true,
                back = true,
            ),
            boardWeight,
        );
        result.endHole.addAll(
            holes(
                currentPoint, he0, zigzag, 0.0, DrawingParam(
                    orientation = Orientation.Vertical,
                    reverse = true,
                    back = true,
                ),
                wald.holeWeight
            )
        );

        //Правый нижний угол
        points.add(origin + Vec2(cx, 0.0))

        //Нижний край
        var pred = cx
        for (i in intersectPolki.size - 1 downTo 0) {
            val c = intersectPolki[i]
            var he = polka.heightForLine(i + 1, height)
            var needZig = false
            var che = c.heightForLine(polka.calc.index - c.startCell, height)
            var df = he / 2

            if (or == Orientation.Horizontal) {
                cx = c.calc.sX - csx;
                if (c.visible) {
                    if (che > he) {
                        needZig = false;
                    } else {
                        needZig = true;
                        if (che < he) {
                            df = che;
                        } else
                            df = he / 2;
                    }
                }
            } else {
                if (c.visible) {
                    if (!eps(che, he)) {
                        if (che < he) {
                            needZig = true;
                            df = che;
                        }
                    }
                }

                cx = c.calc.sY - csx;
            }
            if (needZig) {
                if (hasPolPaz) {
                    val d = pred - (cx + dwe2);
                    currentPoint = getCoord(polka, points.last() - origin) + originForPol;
                    zigzag(
                        points, points.last(), d, zigzagPol, 0.0, DrawingParam(
                            orientation = Orientation.Horizontal,
                            reverse = true,
                            back = true
                        ), boardWeight
                    );
                    result.polHole.addAll(
                        holes(
                            currentPoint, d, zigzagPol, 0.0, DrawingParam(
                                orientation = or,
                                reverse = true,
                                back = true
                            ), wald.holeWeight
                        )
                    );
                }

                points.add(origin + Vec2(cx + dwe2, 0.0));
                points.add(origin + Vec2(cx + dwe2, df));
                points.add(origin + Vec2(cx - dwe2, df));
                points.add(origin + Vec2(cx - dwe2, 0.0));
                pred = cx - dwe2;
            }
        } // for

        if (hasPolPaz) {
            val d = pred - ssx;
            currentPoint = getCoord(polka, points.last() - origin) + originForPol;
            zigzag(
                points, points.last(), d, zigzagPol, 0.0, DrawingParam(
                    orientation = Orientation.Horizontal,
                    reverse = true,
                    back = true
                ),
                boardWeight
            );
            result.polHole.addAll(
                holes(
                    currentPoint, d, zigzagPol, 0.0, DrawingParam(
                        orientation = or,
                        reverse = true,
                        back = true
                    ),
                    wald.holeWeight
                )
            );
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

    private const val F_BOTTOM = -1
    private const val F_TOP = -2
    private const val F_LEFT = -3
    private const val F_RIGHT = -4
    private const val F_FACE = -5
    private const val F_BACK = -6

    fun box(
        startPoint: Vec2,
        boxInfo: BoxInfo,
        zigW: ZigzagInfo,
        zigH: ZigzagInfo,
        zigWe: ZigzagInfo,
        drawerSettings: DrawerSettings,
        waldParams: WaldParam,
        polki: PolkaSort,
        outVariant : EOutVariant
    ): IFigure {

        // две толщины доски
        val ap = drawerSettings.boardWeight * 2
        val p = DrawingParam(
            orientation = Orientation.Horizontal,
            reverse = true,
            back = false
        )

        val wald = waldParams.copy(
            holeWeight = if (waldParams.holeWeight == 0.0) drawerSettings.holeWeight else waldParams.holeWeight,
          //  holeOffset = if (waldParams.holeOffset == 0.0) drawerSettings.holeOffset else waldParams.holeOffset,
        )

        val weight = boxInfo.weight;
        val height = boxInfo.height;
        val width = boxInfo.width;
        val bw = drawerSettings.boardWeight

        val polkaVerticalOffset = wald.fullBottomOffset(bw) + wald.fullTopOffset(bw)

        val holeW = zigW.copy(
            width = zigW.width - drawerSettings.holeDrop,
        )

        val holeH = zigH.copy(
            width = zigH.width - drawerSettings.holeDrop,
            delta = zigH.delta
        )

        val holeWe = zigWe.copy(
            width = zigWe.width - drawerSettings.holeDrop,
            delta = zigWe.delta
        )

        val resultMap = mutableMapOf<Int, MutableList<IFigure>>()

        //  val list = mutableListOf<IFigure>()

        resultMap.getOrPut(F_LEFT) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = width - ap,
                height = height,
                zigzag = zigH,
                hole = holeW,
                param = p,
                boardWeight = bw,
                wald = wald
            )
        );

        resultMap.getOrPut(F_RIGHT) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = width - ap,
                height = height,
                zigzag = zigH,
                hole = holeW,
                param = p,
                boardWeight = bw,
                wald = wald
            )
        );

        val p2 = p.copy(reverse = false)

        resultMap.getOrPut(F_FACE) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = weight,
                height = height,
                zigzag = holeH,
                hole = holeWe,
                param = p2,
                boardWeight = bw,
                wald = wald
            )
        );
        resultMap.getOrPut(F_BACK) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = weight,
                height = height,
                zigzag = holeH,
                hole = holeWe,
                param = p2,
                boardWeight = bw,
                wald = wald
            )
        );

        if (wald.bottomForm != PazForm.None) {
            resultMap.getOrPut(F_BOTTOM) { mutableListOf() }.add(
                pol(
                    width = width - ap,
                    height = weight - ap,
                    origin = Vec2.Zero,
                    zigzagW = zigW,
                    zigzagH = zigWe,
                    boardWeight = bw
                )
            );
        }
        if (wald.topForm != PazForm.None) {
            resultMap.getOrPut(F_TOP) { mutableListOf() }.add(
                pol(
                    width = width - ap,
                    height = weight - ap,
                    origin = Vec2.Zero,
                    zigzagW = zigW,
                    zigzagH = zigWe,
                    boardWeight = bw
                )
            );
        }


        polki.calcList.forEachIndexed { index, polka ->
            polka.calc.id = index + 1
        }
        resultMap.getOrPut(F_BOTTOM) { mutableListOf() }.add(

            FigureColor(0xFF0000,
                FigureList(
                    polki.calcList.map { po ->
                        val start = polki.findStart(po)
                        val end = polki.findEnd(po)
                        val inter: List<Polka> = polki.intersectList(po)
                        FigureLine(
                            Vec2(po.calc.sX, po.calc.sY),
                            Vec2(po.calc.eX, po.calc.eY)
                        )
                    }
                )
            )
        )


        for (po in polki.calcList) {
            if (po.visible) {
                val inter: List<Polka> = polki.intersectList(po)
                val start = polki.findStart(po)
                val end = polki.findEnd(po)

                val result = polkaWald(
                    origin = Vec2.Zero,
                    polka = po,
                    startPolka = start,
                    endPolka = end,
                    intersectPolki = inter,
                    height = height - polkaVerticalOffset,
                    zigzag = polki.zigPolkaH,
                    zigzagPol = polki.zigPolkaPol,
                    wald = wald,
                    hasPolPaz = wald.bottomForm != PazForm.None,
                    drawerSettings = drawerSettings,
                    boardWeight = bw
                )


                result.polka?.let {
                    resultMap.getOrPut(po.calc.id) { mutableListOf() }.add(it)
                }

                val startId = start?.calc?.id ?: if (po.orientation === Orientation.Vertical) F_LEFT else F_FACE
                val endId = end?.calc?.id ?: if (po.orientation === Orientation.Vertical) F_RIGHT else F_BACK
                val polId = F_BOTTOM

                resultMap.getOrPut(polId) { mutableListOf() }.addAll(result.polHole)
                resultMap.getOrPut(startId) { mutableListOf() }.addAll(result.startHole)
                resultMap.getOrPut(endId) { mutableListOf() }.addAll(result.endHole)
            }

        }

        val rm = resultMap.mapValues { (index, value) ->
            FigureList(value)
        }

        return when (outVariant){
            EOutVariant.COLUMN -> mainPosition(rm, startPoint, bw)
            EOutVariant.ALTERNATIVE -> alternativePosition(rm, bw, polki)
            EOutVariant.VOLUME ->  figure3dTransform(rm, boxInfo, wald, polki, bw)
        }

    }

    private fun figure3dTransform(
        rm: Map<Int, FigureList>,
        boxInfo: BoxInfo,
        wald: WaldParam,
        polki: PolkaSort,
        boardWeight:Double,
    ): Figure3dTransform {
        val sList = mutableListOf<IFigure>()
        rm.forEach { (index, f) ->
            val r = f.rect()
            val mf = Matrix()
            when (index) {
                F_FACE -> {
                    mf.translate( y = -boardWeight.toFloat(), z = -boardWeight.toFloat(), )
                    mf.rotateY(90f)
                    mf.rotateZ(90f)

                }

                F_BACK -> {
                    mf.translate(y = -boardWeight.toFloat(), z = boxInfo.width.toFloat()-boardWeight.toFloat())
                    mf.rotateY(90f)
                    mf.rotateZ(90f)
                }

                F_LEFT -> {
                    mf.translate(z = +boardWeight.toFloat())
                    mf.rotateX(90f)
                }

                F_RIGHT -> {
                    mf.translate( z = -boxInfo.weight.toFloat()+boardWeight.toFloat())
                    mf.rotateX(90f)
                }

                F_TOP -> {
                    mf.translate(z = boxInfo.height.toFloat() - wald.fullTopOffset(boardWeight).toFloat())
                 //   mf.rotateY(-90f)
                }

                F_BOTTOM -> {
                    mf.translate(z = wald.fullBottomOffset(boardWeight).toFloat())
              //      mf.rotateY(-90f)
                }

                else -> {
                    polki.calcList.find { p -> p.calc.id == index }?.let { po ->

                        when (po.orientation) {
                            Orientation.Vertical -> {
                                mf.translate(x = -wald.fullBottomOffset(boardWeight).toFloat(), y = po.calc.sY.toFloat(), z = po.calc.sX.toFloat())
                                mf.rotateY(90f)
                                mf.rotateZ(90f)
                            }

                            Orientation.Horizontal -> {
                                mf.translate(y = wald.fullBottomOffset(boardWeight).toFloat(), x = po.calc.sX.toFloat(), z = -po.calc.sY.toFloat())
                            //    mf.rotateY(180f)
                                mf.rotateX(90f)
                            }

                            else -> {

                            }
                        }
                    }
                }
            }

            sList += Figure3dTransform(mf, f)
        }
        val mf = Matrix()
        mf.translate((-boxInfo.width/2f).toFloat(), (-boxInfo.weight/2f).toFloat(), (-boxInfo.height/2f).toFloat())
        mf.rotateX(59f)
        mf.rotateY( -45f)
        mf.rotateZ(5f)//-22.5f-22f)



        return Figure3dTransform(mf, FigureList(sList.toList()))
    }

    private fun mainPosition(
        list: Map<Int, IFigure>,
        startPoint: Vec2,
        bw: Double
    ): IFigure {
        val outList = mutableListOf<IFigure>()
        var position = startPoint

        for (f in list.values) {
            val r = f.rect()
            position += Vec2(0.0, -r.min.y)

            outList += FigureTranslate(f, position)

            position += Vec2(0.0, r.height + bw)
        }
        return FigureList(outList.toList())
    }

    private fun alternativePosition(
        rm: Map<Int, IFigure>,
        bw: Double,
        polki: PolkaSort
    ):IFigure {

        val outList = mutableListOf<IFigure>()
        var pLeft = Vec2.Zero
        var pRight = Vec2.Zero
        var pTop = Vec2.Zero
        var pBottom = Vec2.Zero

        rm[F_BOTTOM]?.let { f ->
            val r = f.rect()
            outList += f
            pBottom += Vec2(0.0, r.max.y + 2 * bw)
            pRight += Vec2(r.max.x + bw, 0.0)
            pLeft -= Vec2(-r.min.x + bw, 0.0)
            pTop -= Vec2(0.0, -r.min.y + bw)
        }
        rm.forEach { (index, f) ->
            when (index) {
                F_FACE, F_BACK -> {
                    val r = f.rect()

                    outList += FigureTranslate(
                        FigureRotate(f, 90.0, Vec2.Zero), pLeft
                    )
                    pLeft -= Vec2(r.height + bw, 0.0)

                }

                F_LEFT, F_RIGHT, F_TOP -> {
                    val r = f.rect()
                    pTop -= Vec2(0.0, r.height + bw)
                    outList += FigureTranslate(f, pTop)
                }

                F_BOTTOM -> {}
                else -> {
                    val po = polki.calcList.find { p -> p.calc.id == index }
                    when (po?.orientation) {
                        Orientation.Vertical -> {
                            val r = f.rect()
                            pRight += Vec2(r.height + bw, 0.0)
                            outList += FigureTranslate(
                                FigureRotate(f, 90.0, Vec2.Zero), pRight + Vec2(0.0, po.calc.sY)
                            )
                        }

                        Orientation.Horizontal -> {
                            val r = f.rect()
                            outList += FigureTranslate(f, pBottom + Vec2(po.calc.sX, 0.0))
                            pBottom += Vec2(0.0, r.height + bw)
                        }

                        else -> {

                        }
                    }
                }
            }
        }
        return FigureList(outList.toList())
    }

    fun calculateDrawPosition(
        polka: Polka,
        origin: Vec2,
        polki: PolkaSort,
        height: Double,
        drawerSettings: DrawerSettings
    ): Vec2 {
        var y = 0.0;
        for (po in polki.calcList) {
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