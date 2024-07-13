package com.kos.boxdrawer.detal.box

import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_BACK
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_BOTTOM
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_FACE
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_LEFT
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_NONE
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_RIGHT
import com.kos.boxdrawer.detal.box.PolkaProgram.Companion.SIDE_TOP
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import turtoise.*
import turtoise.FigureCreator.holes
import turtoise.FigureCreator.rectangle
import turtoise.FigureCreator.zigzag
import turtoise.memory.SimpleTortoiseMemory
import turtoise.parser.TortoiseParser
import vectors.Vec2
import kotlin.math.min


object BoxCad {

    enum class EOutVariant {
        COLUMN,
        ALTERNATIVE,
        VOLUME
    }

    private const val angle90 = Math.PI / 2
    private const val angle0 = 0.0

    private const val F_BOTTOM = -1
    private const val F_TOP = -2
    private const val F_LEFT = -3
    private const val F_RIGHT = -4
    private const val F_FACE = -5
    private const val F_BACK = -6

    fun faceWald(
        origin: Vec2,
        width: Double,
        height: Double,
        zigzag: ZigzagInfo,
        hole: ZigzagInfo,
        param: DrawingParam,
        boardWeight: Double,
        wald: WaldParam,
        zigzagOffset: Double,
        heightEnd: Double = height
    ): List<IFigure> {
        val result = mutableListOf<IFigure>()

        val points = mutableListOf<Vec2>()

        var p = DrawingParam(
            reverse = !param.reverse,
            back = false
        )

        val zigzagLength = width - zigzagOffset


        //Левая сторона
        points.add(origin)
        points.add(origin + Vec2(0.0, wald.bottomOffset))
        FigureCreator.zigzag(
            points,
            points.last(),
            height - wald.verticalOffset,
            zigzag,
            angle90,
            p,
            boardWeight
        )
        points.add(origin + Vec2(0.0, height))

        if (wald.topForm != PazForm.None) {
            val ph = DrawingParam(
                reverse = true,
                back = false,
            )

            val sp = points.last() + Vec2((width - zigzagLength) / 2.0, 0.0);

            waldZigzag(
                points = points,
                sp = sp,
                form = wald.topForm,
                zigzagLength = zigzagLength,
                zigzagInfo = hole,
                ph = ph,
                boardWeight = boardWeight,
                holeWeight = wald.holeWeight,
                holeOffset = -wald.holeTopOffset,
                result = result
            )
        }

        p = DrawingParam(
            reverse = param.reverse,
            back = true,
        )

        // Верхняя сторона
        points.add(origin + Vec2(width, heightEnd))

        // Правая сторона
        points.add(origin + Vec2(width, heightEnd - wald.topOffset))
        FigureCreator.zigzag(
            points,
            points.last(),
            heightEnd - wald.verticalOffset,
            zigzag,
            angle90,
            p,
            boardWeight
        )
        points.add(origin + Vec2(width, 0.0))

        if (wald.bottomForm != PazForm.None) {
            val ph = DrawingParam(
                reverse = false,
                back = true,
            )

            val sp = points.last() - Vec2((width - zigzagLength) / 2.0, 0.0);

            waldZigzag(
                points = points,
                sp = sp,
                form = wald.bottomForm,
                zigzagLength = zigzagLength,
                zigzagInfo = hole,
                ph = ph,
                boardWeight = boardWeight,
                holeWeight = wald.holeWeight,
                holeOffset = wald.holeBottomOffset,
                result = result
            )
        }

        points.add(origin)
        result.add(FigurePolyline(points.toList()))
        return result.toList()
    }

    private fun waldZigzag(
        points: MutableList<Vec2>,
        sp: Vec2,
        form: PazForm,
        zigzagLength: Double,
        zigzagInfo: ZigzagInfo,
        ph: DrawingParam,
        boardWeight: Double,
        holeWeight: Double,
        holeOffset: Double,
        result: MutableList<IFigure>
    ) {
        when (form) {
            PazForm.Paz ->
                zigzag(
                    points = points,
                    origin = sp,
                    width = zigzagLength,
                    zig = zigzagInfo,
                    angle = 0.0,
                    param = ph,
                    boardWeight = boardWeight
                );
            PazForm.Hole ->
                result.addAll(
                    holes(
                        origin = sp + Vec2(0.0, holeOffset),
                        width = zigzagLength,
                        zig = zigzagInfo,
                        angle = 0.0,
                        param = ph,
                        boardWeight = holeWeight
                    )
                )

            PazForm.Outside ->
                zigzag(
                    points = points,
                    origin = sp,
                    width = zigzagLength,
                    zig = zigzagInfo,
                    angle = 0.0,
                    param = ph.copy(reverse = !ph.reverse),
                    boardWeight = boardWeight
                );
            else -> {}
        }
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
            angle90,
            DrawingParam(
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
            angle90,
            DrawingParam(
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
                reverse = true,
                back = true,
            ),
            boardWeight
        );

        return FigurePolyline(points)
    }

    fun polOutside(
        width: Double,
        height: Double,
        origin: Vec2,
        zigzagW: ZigzagInfo,
        zigzagH: ZigzagInfo,
        boardWeight: Double,
        roundRadius: Double,
        holeOffset: Double,
        wald: WaldParam,
    ): IFigure {
        val points = mutableListOf<IFigure>()

        val ap = holeOffset + boardWeight


        points += rectangle(
            origin.x - ap, origin.y - ap,
            origin.x + width + ap, origin.y + height + ap,
            roundRadius > 0.0, roundRadius
        )

        points += holes(
            origin,
            height,
            zigzagH,
            angle90,
            DrawingParam(
                reverse = false,
                back = false,
            ),
            wald.holeWeight
        )
        points += holes(
            origin + Vec2(0.0, height),
            width,
            zigzagW,
            0.0,
            DrawingParam(
                reverse = false,
                back = false,
            ),
            wald.holeWeight
        )
        points += holes(
            origin + Vec2(width, height),
            height,
            zigzagH,
            angle90,
            DrawingParam(
                reverse = true,
                back = true,
            ),
            wald.holeWeight
        );
        points += holes(
            origin + Vec2(width, 0.0),
            width,
            zigzagW,
            0.0,
            DrawingParam(
                reverse = true,
                back = true,
            ),
            wald.holeWeight
        );

        return FigureList(points)
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
        val startCXY =
            if (startPolka == null) Vec2.Zero else Vec2(startPolka.calc.sX, startPolka.calc.sY)
        val endCXY = if (endPolka == null) Vec2.Zero else Vec2(endPolka.calc.sX, endPolka.calc.sY)

        if (or == Orientation.Horizontal) {
            originForPol += Vec2(polka.calc.sX, polka.calc.sY + dwe2)
            startOrigin += Vec2(polka.calc.sY + dwe2 - startCXY.y, startBottomOffset)
            endOrigin += Vec2(polka.calc.sY + dwe2 - endCXY.y, endBottomOffset)
        } else {
            originForPol += Vec2(polka.calc.sX - dwe2, polka.calc.sY)
            startOrigin += Vec2(polka.calc.sX + dwe2 - startCXY.x, startBottomOffset)
            endOrigin += Vec2(polka.calc.sX + dwe2 - endCXY.x, endBottomOffset)
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

        zigzag(
            points, points.last(), he0, zigzag, angle90, DrawingParam(
                reverse = false,
                back = false
            ),
            boardWeight
        );
        result.startHole.addAll(
            holes(
                startOrigin, he0, zigzag, angle90,
                DrawingParam(
                    reverse = false,
                    back = false,
                ),
                wald.holeWeight
            )
        );


        val endheE = polka.heightForLine(intersectPolki.size + 1, height)
        val heE = min(
            endheE,
            endPolka?.heightForLine(polka.calc.index - endPolka.startCell, height) ?: height
        )

        result.endHole.addAll(
            holes(
                endOrigin, heE, zigzag, angle90,
                DrawingParam(
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

        //  currentPoint = Vec2(0.0, points.last().y - origin.y) + endOrigin
        zigzag(
            points, points.last(), he0, zigzag, angle90,
            DrawingParam(
                reverse = true,
                back = true,
            ),
            boardWeight,
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
                    val currentPoint = getCoord(polka, points.last() - origin) + originForPol;
                    zigzag(
                        points, points.last(), d, zigzagPol, 0.0, DrawingParam(
                            reverse = true,
                            back = true
                        ), boardWeight
                    );
                    result.polHole.addAll(
                        holes(
                            currentPoint,
                            d,
                            zigzagPol,
                            if (or == Orientation.Vertical) angle90 else angle0,
                            DrawingParam(
                                reverse = true,
                                back = true
                            ),
                            wald.holeWeight
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
            val currentPoint = getCoord(polka, points.last() - origin) + originForPol;
            zigzag(
                points, points.last(), d, zigzagPol, 0.0, DrawingParam(
                    reverse = true,
                    back = true
                ),
                boardWeight
            );
            result.polHole.addAll(
                holes(
                    currentPoint,
                    d,
                    zigzagPol,
                    if (or == Orientation.Vertical) angle90 else angle0,
                    DrawingParam(
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
        return if (polka.orientation == Orientation.Horizontal) Vec2(point.x, 0.0) else Vec2(
            0.0,
            point.x
        )
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
        outVariant: EOutVariant
    ): IFigure {

        // две толщины доски
        val ap = drawerSettings.boardWeight * 2
        val horizontalWaldParam = DrawingParam(
            reverse = true,
            back = false
        )

        val wald = waldParams.copy(
            holeWeight = if (waldParams.holeWeight == 0.0) drawerSettings.holeWeight else waldParams.holeWeight,
            //  holeOffset = if (waldParams.holeOffset == 0.0) drawerSettings.holeOffset else waldParams.holeOffset,
        )

        val heights = boxInfo.heights.map { if (it < 0.001) boxInfo.height else it }

        val weight = boxInfo.weight
        val height = boxInfo.height
        val width = boxInfo.width
        val bw = drawerSettings.boardWeight

        val polkaVerticalOffset = wald.fullBottomOffset(bw) + wald.fullTopOffset(bw)

        val holeW = zigW.copy(
            width = zigW.width - drawerSettings.holeDrop,
            drop = drawerSettings.holeDrop
        )

        val holeH = zigH.copy(
            width = zigH.width - drawerSettings.holeDrop,
            height = if (zigH.height == 0.0) bw else zigH.height,
            drop = drawerSettings.holeDrop
        )

        val holeWe = zigWe.copy(
            width = zigWe.width - drawerSettings.holeDrop,
            drop = drawerSettings.holeDrop
        )

        val resultMap = mutableMapOf<Int, MutableList<IFigure>>()

        //  val list = mutableListOf<IFigure>()

        resultMap.getOrPut(F_LEFT) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = width - ap,
                height = heights.getOrElse(0) { height },
                zigzag = zigH,
                hole = holeW,
                param = horizontalWaldParam,
                boardWeight = bw,
                wald = wald,
                zigzagOffset = 0.0,
                heightEnd = heights.getOrElse(2) { height }
            )
        )

        resultMap.getOrPut(F_RIGHT) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2.Zero,
                width = width - ap,
                height = heights.getOrElse(1) { height },
                heightEnd = heights.getOrElse(3) { height },
                zigzag = zigH,
                hole = holeW,
                param = horizontalWaldParam,
                boardWeight = bw,
                zigzagOffset = 0.0,
                wald = wald,
            )
        )

        val verticalWaldParam = horizontalWaldParam.copy(reverse = false)

        resultMap.getOrPut(F_FACE) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2(-holeH.height, 0.0),
                width = weight,
                height = heights.getOrElse(0) { height },
                heightEnd = heights.getOrElse(1) { height },
                zigzag = holeH,
                hole = holeWe,
                param = verticalWaldParam,
                boardWeight = bw,
                zigzagOffset = ap,
                wald = wald,
            )
        )

        resultMap.getOrPut(F_BACK) { mutableListOf() }.addAll(
            faceWald(
                origin = Vec2(-holeH.height, 0.0),
                width = weight,
                height = heights.getOrElse(2) { height },
                heightEnd = heights.getOrElse(3) { height },
                zigzag = holeH,
                hole = holeWe,
                param = verticalWaldParam,
                boardWeight = bw,
                zigzagOffset = ap,
                wald = wald
            )
        )

        if (wald.bottomForm != PazForm.None) {
            resultMap.getOrPut(F_BOTTOM) { mutableListOf() }.add(
                polFigure(
                    origin = Vec2.Zero,
                    pazForm = waldParams.bottomForm,
                    roundRadius = waldParams.bottomRoundRadius,
                    holeOffset = waldParams.holeBottomOffset,
                    width = width - ap,
                    weight = weight - ap,
                    zigWidth = zigW,
                    zigWeight = zigWe,
                    boardWeight = bw,
                    wald = wald
                )
            )
        }

        if (wald.topForm != PazForm.None) {
            resultMap.getOrPut(F_TOP) { mutableListOf() }.add(
                polFigure(
                    origin = Vec2.Zero,
                    pazForm = waldParams.topForm,
                    roundRadius = waldParams.topRoundRadius,
                    holeOffset = waldParams.holeTopOffset,
                    width = width - ap,
                    weight = weight - ap,
                    zigWidth = zigW,
                    zigWeight = zigWe,
                    boardWeight = bw,
                    wald = wald
                )
            )
        }

        polki.calcList.forEachIndexed { index, polka ->
            polka.calc.id = index + 1
        }

        resultMap.getOrPut(F_BOTTOM) { mutableListOf() }.add(
            FigureColor(0xFF0000,
                1,
                FigureList(
                    polki.calcList.map { po ->
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

                val startId = start?.calc?.id
                    ?: if (po.orientation === Orientation.Vertical) F_LEFT else F_FACE
                val endId = end?.calc?.id
                    ?: if (po.orientation === Orientation.Vertical) F_RIGHT else F_BACK
                val polId = F_BOTTOM

                resultMap.getOrPut(polId) { mutableListOf() }.addAll(result.polHole)
                resultMap.getOrPut(startId) { mutableListOf() }.addAll(result.startHole)
                resultMap.getOrPut(endId) { mutableListOf() }.addAll(result.endHole)

                appendProgram(po, drawerSettings, inter, resultMap)
            }

        }

        val rm = resultMap.mapValues { (index, value) ->
            FigureList(value)
        }

        return when (outVariant) {
            EOutVariant.COLUMN -> mainPosition(startPoint, rm, bw)
            EOutVariant.ALTERNATIVE -> alternativePosition(startPoint, rm, bw, polki)
            EOutVariant.VOLUME -> figure3dTransform(startPoint, rm, boxInfo, wald, polki, bw)
        }

    }

    private fun polFigure(
        origin: Vec2,
        pazForm: PazForm,
        roundRadius: Double,
        holeOffset: Double,
        width: Double,
        weight: Double,
        zigWidth: ZigzagInfo,
        zigWeight: ZigzagInfo,
        boardWeight: Double,
        wald: WaldParam,
    ) = if (pazForm == PazForm.Outside) {
        polOutside(
            width = width,
            height = weight,
            origin = origin,
            zigzagW = zigWidth,
            zigzagH = zigWeight,
            boardWeight = boardWeight,
            roundRadius = roundRadius,
            holeOffset = holeOffset,
            wald = wald,
        )
    } else {
        pol(
            width = width,
            height = weight,
            origin = origin,
            zigzagW = zigWidth,
            zigzagH = zigWeight,
            boardWeight = boardWeight
        )
    }

    private fun appendProgram(
        polka: Polka,
        drawerSettings: DrawerSettings,
        inter: List<Polka>,
        resultMap: MutableMap<Int, MutableList<IFigure>>
    ) {
        val runner = TortoiseRunner(TortoiseProgram(emptyList(), emptyMap()))
        val state = TortoiseState()
        polka.programs?.forEach { program ->
            val alg = TortoiseParser.extractTortoiseCommands(program.algorithm).second
            alg.names.firstOrNull()?.let { n ->
                alg.draw(
                    n, state,
                    TortoiseFigureExtractor(
                        drawerSettings,
                        10,
                        SimpleTortoiseMemory(), runner,
                    )
                )
            }?.let { figure ->
                if (program.startCell > 0) {
                    inter.getOrNull(program.startCell - 1)?.let { p2 ->
                        if (polka.orientation === Orientation.Vertical)
                            p2.calc.sX
                        else
                            p2.calc.sY
                    }?.let { FigureTranslate(figure, Vec2(it, 0.0)) } ?: figure
                } else figure
            }?.let { figure ->
                program.sideIndex.forEach { side ->
                    val pi = sideToPolkaIndex(side, polka.calc.id)
                    if (pi != 0) {
                        resultMap.getOrPut(pi) { mutableListOf() }.add(figure)
                    }
                }
            }
        }
    }

    private fun sideToPolkaIndex(side: Int, polkaId: Int): Int {
        return when (side) {
            SIDE_BOTTOM -> F_BOTTOM
            SIDE_TOP -> F_TOP
            SIDE_NONE -> 0
            SIDE_LEFT -> F_LEFT
            SIDE_RIGHT -> F_RIGHT
            SIDE_FACE -> F_FACE
            SIDE_BACK -> F_BACK
            0 -> polkaId
            else ->
                if (side > 0) (-(side + 9)) else 0
        }
    }

    private fun figure3dTransform(
        startPoint: Vec2,
        rm: Map<Int, FigureList>,
        boxInfo: BoxInfo,
        wald: WaldParam,
        polki: PolkaSort,
        boardWeight: Double,
    ): Figure3dTransform {
        val sList = mutableListOf<IFigure>()
        rm.forEach { (index, f) ->
            val r = f.rect()
            val mf = Matrix()
            when (index) {
                F_FACE -> {
                    mf.translate(y = 0f, z = -boardWeight.toFloat())
                    mf.rotateY(90f)
                    mf.rotateZ(90f)

                }

                F_BACK -> {
                    mf.translate(y = 0f, z = boxInfo.width.toFloat() - boardWeight.toFloat())
                    mf.rotateY(90f)
                    mf.rotateZ(90f)
                }

                F_LEFT -> {
                    mf.translate(z = +boardWeight.toFloat())
                    mf.rotateX(90f)
                }

                F_RIGHT -> {
                    mf.translate(z = -boxInfo.weight.toFloat() + boardWeight.toFloat())
                    mf.rotateX(90f)
                }

                F_TOP -> {
                    mf.translate(
                        z = boxInfo.height.toFloat() - wald.fullTopOffset(boardWeight).toFloat()
                    )
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
                                mf.translate(
                                    x = -wald.fullBottomOffset(boardWeight).toFloat(),
                                    y = po.calc.sY.toFloat(),
                                    z = po.calc.sX.toFloat()
                                )
                                mf.rotateY(90f)
                                mf.rotateZ(90f)
                            }

                            Orientation.Horizontal -> {
                                mf.translate(
                                    y = wald.fullBottomOffset(boardWeight).toFloat(),
                                    x = po.calc.sX.toFloat(),
                                    z = -po.calc.sY.toFloat()
                                )
                                //    mf.rotateY(180f)
                                mf.rotateX(90f)
                            }

                            else -> {

                            }
                        }
                    }
                }
            }

            sList += Figure3dTransform(vectors.Matrix(mf.values), f)
        }
        val mf = Matrix()
        mf.translate(
            (-boxInfo.width / 2f).toFloat() + startPoint.x.toFloat(),
            (-boxInfo.weight / 2f).toFloat() + startPoint.y.toFloat(),
            (-boxInfo.height / 2f).toFloat()
        )
        mf.rotateX(59f)
        mf.rotateY(-45f)
        mf.rotateZ(5f)//-22.5f-22f)


        return Figure3dTransform(vectors.Matrix(mf.values), FigureList(sList.toList()))
    }

    private fun mainPosition(
        startPoint: Vec2,
        list: Map<Int, IFigure>,
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
        startPoint: Vec2,
        rm: Map<Int, IFigure>,
        bw: Double,
        polki: PolkaSort
    ): IFigure {

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
        return FigureTranslate(
            FigureList(outList.toList()),
            startPoint
        )
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
    public fun readBox(
        line: String,
        defaultWidth: Double,
        defaultHeight: Double,
        defaultWeight: Double
    ): BoxInfo {

        val a = line.split(*sep).filter { it.isNotEmpty() }

        return BoxInfo(
            width = a.getOrNull(1)?.toDoubleOrNull() ?: defaultWidth,
            height = a.getOrNull(2)?.toDoubleOrNull() ?: defaultHeight,
            weight = a.getOrNull(3)?.toDoubleOrNull() ?: defaultWeight,
        )
    }

}