package turtoise.road

import androidx.compose.ui.graphics.Matrix
import com.jsevy.jdxf.DXFColor
import com.kos.boxdrawer.detal.box.BoxCad.EOutVariant
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureTranslate
import com.kos.figure.composition.FigureTranslateWithRotate
import turtoise.DrawerSettings
import turtoise.DrawingParam
import turtoise.LineInfo
import turtoise.ZigConstructor
import turtoise.ZigzagInfo
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object RoadCad {

    fun simpleZigZag(zihe: ZigzagInfo, reverse: Boolean = false): IFigure {
        val h = (if (reverse) -1 else 1) * zihe.height
        return FigurePolyline(
            listOf(
                Vec2.Zero,
                Vec2(0.0, h),
                Vec2(zihe.width, h),
                Vec2(zihe.width, 0.0),
            )
        )
    }

    fun createKrishka(
        pred: Vec2,
        cur: Vec2,
        next: Vec2,
        boardHeight: Double,
        rp: RoadProperties,
        drawInfo: RoadDrawInfo,
        state: RoadCadState,
        outResult: RoadCadResult
    ): RoadCadState {
        val pl = pred
        val w = Vec2.distance(pl, cur)
        val a = Vec2.angle(pl, cur)

        val drr = boardOtstup(next, cur, pl, boardHeight)

        outResult.simFigures += FigurePolyline(
            listOf(
                Vec2(state.drl, 0.0),
                Vec2(w - drr, 0.0),
                Vec2(w - drr, boardHeight),
                Vec2(state.drl, boardHeight)
            ).map { it.rotate(a) + pl }, true
        )

        val small =
            (w - drawInfo.lineInfo.startOffset - drawInfo.lineInfo.endOffset < drawInfo.zihe.width)

        val dp = DrawingParam(
            reverse = false,
            back = false,
        )
        // Линия
        outResult += ZigConstructor.zigZag(
            origin = pl,
            width = w,
            zig = if (small) drawInfo.sziIn else drawInfo.ziIn,
            angle = a,
            param = dp,
            zigzagFigure = if (small) drawInfo.szagFigure else drawInfo.zagFigure,
            lineInfo = drawInfo.lineInfo,
        )

        //Верхняя крышка
        val wd = w - drr
        val wrr = w - state.drl - drr
        val tp1 = Vec2(state.drl, boardHeight)
        val tp2 = Vec2(state.drl, rp.width - boardHeight)
        val tp4 = Vec2(wd, boardHeight)
        val tp3 = Vec2(wd, rp.width - boardHeight)

        val li = LineInfo(
            drawInfo.lineInfo.startOffset - state.drl,
            drawInfo.lineInfo.endOffset - drr
        )

        outResult.topFigures +=
            FigureCoord(
                pl,
                rotateX = 90.0,
                rotateY = Math.toDegrees(a),
                sdvig = state.tt,
                createTopBar(
                    tp1 = tp1,
                    tp2 = tp2,
                    tp3 = tp3,
                    tp4 = tp4,
                    width = wrr,
                    zigzagInfo = if (small) drawInfo.szihe else drawInfo.zihe,
                    drawingParam = dp,
                    zigFigure = if (small) drawInfo.szigFigure else drawInfo.zigFigure,
                    lineInfo = li,
                    zigReverseFigure = if (small) drawInfo.szigReverseFigure else drawInfo.zigReverseFigure
                )
            )
        return RoadCadState(
            drl = drr,
            tt = state.tt + Vec2(w, 0.0)
        )
    }

    private fun boardOtstup(
        next: Vec2,
        cur: Vec2,
        pred: Vec2,
        boardHeight: Double
    ): Double {
        val ang = Vec2.angle(next, cur, pred)

        val drr = if (ang > 0.01 && ang < Math.PI) {
            val alpha = ang - Math.PI / 2
            boardHeight * (1 - sin(alpha)) / cos(alpha)
        } else 0.0
        return drr
    }

    fun build(line: FigurePolyline, rp: RoadProperties, ds: DrawerSettings): IFigure {

        val dp = DrawingParam(
            reverse = false,
            back = false,
        )
        //  val rm: Map<Int, IFigure> = mutableMapOf()

        val p1 = line.points.first() + Vec2(0.0, rp.startHeight)
        val lpoints = line.points.map { it - p1 }

        val drawInfo = RoadDrawInfo(ds, rp.zigzagInfo, ::simpleZigZag)

        val result: RoadCadResult = RoadCadResult()

        val h = ds.boardWeight

        if (line.isClose() && lpoints.size < 3) {
            return FigureEmpty
        }


        if (!line.isClose() && rp.startHeight > 0.0) {
            result += FigureLine(Vec2.Zero, lpoints.first())
            val pl = lpoints.last()

            result += FigureLine(pl, Vec2(pl.x, 0.0))

            result += ZigConstructor.zigZag(
                origin = Vec2(pl.x, 0.0),
                width = pl.x,
                zig = drawInfo.ziIn,
                angle = Math.PI,
                param = dp,
                zigzagFigure = drawInfo.zagFigure,
                lineInfo = drawInfo.lineInfo,
            )

            result.topFigures += FigureCoord(
                Vec2.Zero,
                rotateX = 90.0,
                rotateY = 0.0,
                sdvig = Vec2(0.0, rp.width + h * 3),
                createTopBar(
                    tp1 = Vec2(0.0, h),
                    tp2 = Vec2(0.0, rp.width - h),
                    tp3 = Vec2(pl.x, rp.width - h),
                    tp4 = Vec2(pl.x, h),
                    width = pl.x,
                    zigzagInfo = drawInfo.zihe,
                    drawingParam = dp,
                    zigFigure = drawInfo.zigFigure,
                    lineInfo = drawInfo.lineInfo,
                    zigReverseFigure = drawInfo.zigReverseFigure
                )
            )
        }

        var delp = 0.0
        var delm = 0.0

        var pl = lpoints.first()
        val lp = lpoints.drop(1)
        var state = RoadCadState(
            drl = if (line.isClose()) boardOtstup(lp[0], pl, lp[lp.size - 2], h)
            else boardOtstup(lp[0], pl, Vec2(pl.x, 0.0), h),
            tt = Vec2(0.0, h * 2)
        )
        lp.forEachIndexed { i, cur ->
            delp = max(delp, cur.y)
            delm = min(delm, cur.y)

            val np = if (i + 1 < lp.size) {
                lp[i + 1]
            } else {
                if (line.isClose()) {
                    lp[0]
                } else {
                    Vec2(cur.x, 0.0)
                }
            }

            state = createKrishka(
                pl, cur, np,
                h,
                rp,
                drawInfo,
                state,
                result
            )

            pl = cur

        }

        val sdvig = Vec2(0.0,  delp)
        val tops = outTopFigures(result, rp, sdvig)

        val res = outFigures(rp, result, delp, delm)

        val currentColor = 4
        val sim = if (rp.outStyle == EOutVariant.VOLUME) {
            listOf(
                FigureColor(
                    color = DXFColor.getRgbColor(currentColor),
                    dxfColor = currentColor,
                    figure = FigureList(result.simFigures.toList())
                )
            )
        } else emptyList()

        return FigureList(
            res + tops +sim
        )
    }

    private fun outTopFigures(
        result: RoadCadResult,
        rp: RoadProperties,
        sdvig:Vec2,
    ) = result.topFigures.map { tp ->
        when (rp.outStyle) {
            EOutVariant.COLUMN -> {
                FigureTranslateWithRotate(
                    tp.figure,
                    tp.position,
                    tp.rotateY,
                )
            }

            EOutVariant.ALTERNATIVE -> {
                FigureTranslate(tp.sdvig+sdvig, tp.figure)
            }

            EOutVariant.VOLUME -> {
                val mf = Matrix()
                val md = Matrix()
                md.translate(tp.position.x.toFloat(), tp.position.y.toFloat())
                mf.rotateY(tp.rotateY.toFloat())
                mf.rotateX(90f)
                mf *= md
                Figure3dTransform(vectors.Matrix(mf.values), tp.figure)
            }
        }
    }

    private fun outFigures(
        rp: RoadProperties,
        result: RoadCadResult,
        delp: Double,
        delm: Double
    ): List<IFigure> {
        val res = when (rp.outStyle) {
            EOutVariant.COLUMN -> {
                result.result.toList() +
                        FigureTranslate(
                            Vec2(0.0, -(delp - delm + rp.startHeight)),
                            FigureList(result.result.toList())
                        )

            }

            EOutVariant.ALTERNATIVE -> {

                result.result.toList() +
                        FigureTranslate(
                            Vec2(0.0, -(delp - delm + rp.startHeight)),
                            FigureList(result.result.toList())
                        )

            }

            EOutVariant.VOLUME -> {
                val mm = Matrix()
                mm.translate(0.0.toFloat(), 0.0.toFloat(), rp.width.toFloat())

                listOf(
                    FigureList(
                        result.result.toList()
                    ),
                    Figure3dTransform(
                        vectors.Matrix(mm.values), FigureList(
                            result.result.toList()
                        )
                    )
                )

            }
        }
        return res
    }

    private fun createTopBar(
        tp1: Vec2,
        tp2: Vec2,
        tp3: Vec2,
        tp4: Vec2,
        width: Double,
        drawingParam: DrawingParam,
        lineInfo: LineInfo,
        zigzagInfo: ZigzagInfo,
        zigFigure: IFigure,
        zigReverseFigure: IFigure
    ) = FigureList(
        listOf(
            FigureLine(tp1, tp2),
            ZigConstructor.zigZag(
                origin = tp2,
                width = width,
                zig = zigzagInfo,
                angle = 0.0,
                param = drawingParam,
                zigzagFigure = zigFigure,
                lineInfo = lineInfo,
            ),
            FigureLine(tp3, tp4),
            ZigConstructor.zigZag(
                origin = tp1,
                width = width,
                zig = zigzagInfo,
                angle = 0.0,
                param = drawingParam.copy(reverse = true),
                zigzagFigure = zigReverseFigure,
                lineInfo = lineInfo,
            )
        )
    )

    val FRONT = 1
    val BACK = 2
}

class RoadProperties(
    val width: Double,
    val startHeight: Double,
    val count: Int,
    val outStyle: EOutVariant,
    val zigzagInfo: ZigzagInfo,

    )

class FigureCoord(
    val position: Vec2,
    val rotateX: Double,
    val rotateY: Double,
    val sdvig: Vec2,
    val figure: IFigure,
)

class RoadDrawInfo(
    val ds: DrawerSettings,
    val zihe: ZigzagInfo,
    val simpleZigZag: (ZigzagInfo, Boolean) -> IFigure
) {
    val lineInfo = LineInfo(
        startOffset = ds.boardWeight,
        endOffset = ds.boardWeight,
    )

    val ziIn = zihe.copy(
        width = zihe.width - ds.holeDrop,
        drop = ds.holeDrop
    )

    val zigFigure = simpleZigZag(zihe, false)
    val zigReverseFigure = simpleZigZag(zihe, true)
    val zagFigure = simpleZigZag(ziIn, false)

    val szihe = ZigzagInfo(
        width = 5.0,
        delta = 10.0,
        height = ds.boardWeight,
    )

    val sziIn = ZigzagInfo(
        width = 5.0 - ds.holeDrop,
        delta = 10.0,
        height = ds.boardWeight,
        drop = ds.holeDrop
    )

    val szigFigure = simpleZigZag(szihe, false)
    val szigReverseFigure = simpleZigZag(szihe, true)
    val szagFigure = simpleZigZag(sziIn, false)
}

class RoadCadResult {
    val result = mutableListOf<IFigure>()
    val topFigures = mutableListOf<FigureCoord>()
    val simFigures = mutableListOf<IFigure>()

    operator fun plusAssign(figure: IFigure) {
        result += figure
    }
}

class RoadCadState(
    val drl: Double,
    val tt: Vec2,
)