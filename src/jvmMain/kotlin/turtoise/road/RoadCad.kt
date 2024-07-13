package turtoise.road

import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawer.detal.box.BoxCad.EOutVariant
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

    fun simpleZigZag(zihe:ZigzagInfo, reverse:Boolean= false):IFigure{
        val h = (if (reverse) -1 else 1) *zihe.height
        return FigurePolyline(
            listOf(
                Vec2.Zero,
                Vec2(0.0, h),
                Vec2(zihe.width, h),
                Vec2(zihe.width, 0.0),
            )
        )
    }

    fun build(line: FigurePolyline, rp: RoadProperties, ds: DrawerSettings): IFigure {

        val dp = DrawingParam(
            reverse = false,
            back = false,
        )
        //  val rm: Map<Int, IFigure> = mutableMapOf()

        val p1 = line.points.first() + Vec2(0.0, rp.startHeight)
        val lpoints = line.points.map { it - p1 }

        val lineInfo = LineInfo(
            startOffset = ds.boardWeight * 2,
            endOffset = ds.boardWeight * 2,
        )

        val zihe = rp.zigzagInfo

        val ziIn = zihe.copy(
            width = zihe.width-ds.holeDrop,
            drop = ds.holeDrop
        )

        val zigFigure = simpleZigZag(zihe)
        val zigReverseFigure = simpleZigZag(zihe, true)
        val zagFigure = simpleZigZag(ziIn)

        val szihe = ZigzagInfo(
            width = 5.0,
            delta = 15.0,
            height = ds.boardWeight,
        )

        val sziIn = ZigzagInfo(
            width = 5.0 - ds.holeDrop,
            delta = 15.0,
            height = ds.boardWeight,
            drop = ds.holeDrop
        )

        val szigFigure = simpleZigZag(szihe)
        val szigReverseFigure = simpleZigZag(szihe, true)
        val szagFigure = simpleZigZag(sziIn)

        val result = mutableListOf<IFigure>()
        val topFigures = mutableListOf<FigureCoord>()
        val simFigures = mutableListOf<IFigure>()


        if (!line.isClose() && rp.startHeight>0.0) {
            result += FigureLine(Vec2.Zero, lpoints.first())
            val pl = lpoints.last()

            result += FigureLine(pl, Vec2(pl.x, 0.0))

            result += FigureLine(Vec2.Zero, Vec2(pl.x, 0.0))
        }

        var delp = 0.0
        var delm= 0.0
        val h = ds.boardWeight
        var pl = lpoints.first()
        val lp = lpoints.drop(1)
        var drl = 0.0
        var tt = Vec2(0.0, h*2)
        result += lp.mapIndexed { i, cur ->

            delp = max(delp, cur.y)
            delm = min(delm, cur.y)
            val w = Vec2.distance(pl, cur)
            val a = Vec2.angle(pl, cur)

            val np = if (i + 1 < lp.size) {
                lp[i + 1]
            } else {
                Vec2(cur.x, 0.0)
            }
            val ang = Vec2.angle(np, cur, pl)


            val drr = if (ang > 0.01 && ang < Math.PI) {
                val alpha = ang - Math.PI / 2
                h * (1 - sin(alpha)) / cos(alpha)
            } else 0.0
            simFigures += FigurePolyline(
                listOf(
                    Vec2(drl, 0.0),
                    Vec2(w - drr, 0.0),
                    Vec2(w - drr, h),
                    Vec2(drl, h)
                ).map { it.rotate(a) + pl }, true
            )

            val small =  (w-lineInfo.startOffset-lineInfo.endOffset < 15f)

            // Линия
            val f = ZigConstructor.zigZag(
                origin = pl,
                width = w,
                zig =  if (small) sziIn else ziIn ,
                angle = a,
                param = dp,
                zigzagFigure = if (small) szagFigure else zagFigure,
                lineInfo = lineInfo,
            )

            //Верхняя крышка
            val wd = w - drr
            val wrr = w - drl - drr
            val tp1 = Vec2(drl, h)
            val tp2 = Vec2(drl, rp.width-h)
            val tp4 = Vec2(wd, h)
            val tp3 = Vec2(wd, rp.width-h)

            topFigures +=
                FigureCoord(
                    pl,
                    rotateX = 90.0,
                    rotateY = Math.toDegrees(a),
                    center = Vec2(w/2.0, rp.width/2.0),
                    size = Vec2(wrr, rp.width),
                    sdvig = tt,
                    FigureList(
                        listOf(
                            FigureLine(tp1, tp2),
                            ZigConstructor.zigZag(
                                origin = tp2,
                                width = wrr,
                                zig =  if (small) szihe else zihe,
                                angle = 0.0,
                                param = dp,
                                zigzagFigure = if (small) szigFigure else zigFigure,
                                lineInfo = LineInfo(
                                    lineInfo.startOffset - drl,
                                    lineInfo.endOffset - drr
                                ),
                            ),
                            FigureLine(tp3, tp4),
                            ZigConstructor.zigZag(
                                origin = tp1,
                                width = wrr,
                                zig = if (small) szihe else zihe,
                                angle = 0.0,
                                param = dp.copy(reverse = true),
                                zigzagFigure = if (small) szigReverseFigure else  zigReverseFigure,
                                lineInfo = LineInfo(
                                    lineInfo.startOffset - drl,
                                    lineInfo.endOffset - drr
                                ),
                            )
                        )
                    )


                )
            tt += Vec2(w, 0.0)


            drl = drr
            //  println(">>> $pl $cur $np == $ang")
            //topFigures+=FigureTranslate( FigureText("${ang*180f/Math.PI}"), cur)


            pl = cur
            f

        }


        val tops = topFigures.map { tp ->
            when (rp.outStyle){
                EOutVariant.COLUMN -> {
                    FigureTranslateWithRotate(tp.figure,
                        tp.position,
                        tp.rotateY,
                        )
                }
                EOutVariant.ALTERNATIVE -> {
                    FigureTranslate(tp.sdvig, tp.figure)
                }
                EOutVariant.VOLUME -> {
                    val mf = Matrix()
                    val md = Matrix()
                    md.translate(tp.position.x.toFloat(), tp.position.y.toFloat())
                    mf.rotateY(tp.rotateY.toFloat())
                    mf.rotateX(90f)
                    mf*=md
                    Figure3dTransform(vectors.Matrix(mf.values), tp.figure)
                }
            }
        }

        val res = when (rp.outStyle){
            EOutVariant.COLUMN -> {
                    result.toList()+
                    FigureTranslate(Vec2(0.0, -(delp-delm+rp.startHeight)),  FigureList( result.toList()))

            }
            EOutVariant.ALTERNATIVE -> {

                    result.toList()+
                            FigureTranslate(Vec2(0.0, -(delp-delm+rp.startHeight)),  FigureList( result.toList()))

            }
            EOutVariant.VOLUME -> {
                val mm = Matrix()
                mm.translate(0.0.toFloat(), 0.0.toFloat(), rp.width.toFloat())

                    listOf(
                        FigureList(
                            result.toList()
                        ),
                        Figure3dTransform(vectors.Matrix(mm.values),  FigureList(
                            result.toList()
                        ))
                    )

            }
        }


        //val currentColor = 4
        return FigureList(
            res + tops

//                    FigureColor(
//                color = com.jsevy.jdxf.DXFColor.getRgbColor(currentColor),
//                dxfColor = currentColor,
//                figure = FigureList(simFigures.toList())
//            )
        )
    }

    val FRONT = 1
    val BACK = 2
}

class RoadProperties(
    val width: Double,
    val startHeight: Double,
    val count: Int,
    val outStyle: EOutVariant,
    val zigzagInfo : ZigzagInfo,

)

class FigureCoord(
    val position: Vec2,
    val rotateX: Double,
    val rotateY: Double,
    val center: Vec2,
    val size:Vec2,
    val sdvig:Vec2,
    val figure: IFigure,
)