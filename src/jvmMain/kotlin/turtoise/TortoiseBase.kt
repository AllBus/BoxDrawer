package turtoise

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.isIdentity
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolygon
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureOnPath
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslateWithRotate
import turtoise.TortoiseFigureExtractor.Companion.positionInPath
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MemoryKey.Companion.orEmpty
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

abstract class TortoiseBase {

    /**in degrees */
    protected fun calculateAngle(y: Double, x: Double): Double {
        return atan2(y, x) * 180.0 / PI
    }

    protected fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: Int,
        memory: TortoiseMemory,
        defaultValue: Double = 0.0,
    ): Double {
        return blockProperties?.get(index)?.let { key ->
            memory.value(key, defaultValue)
        } ?: defaultValue
    }

    protected fun rectangle(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        var c2 = builder.xy
        val angle = builder.angle
        for (d in 0 until com.size step 2) {
            val width = com[d, 0.0, memory]
            val height = com[d + 1, width, memory]
            if (d > 0) {
                c2 += Vec2(width / 2, 0.0).rotate(angle)
            }
            builder.add(rectangle(width, height, c2, angle))
            c2 += Vec2(width / 2, 0.0).rotate(angle)
        }
    }

    protected fun rectangleLine(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        if (com.size <= 2)
            rectangle(builder, com, memory)
        else {


            val angle = builder.angle

            val startWidth = com[0, 0.0, memory]
            val startHeight = com[1, startWidth, memory]
            val points = mutableListOf<Vec2>()
            val endPoints = mutableListOf<Vec2>()

            val c2 = builder.xy + Vec2(0.0, 0.0)
            var xp = 0.0 //startWidth/2

            for (d in 0 until com.size step 2) {
                val width = com[d, 0.0, memory]
                val height = com[d + 1, width, memory]
                points.add(Vec2(xp, -height / 2))
                endPoints.add(Vec2(xp, height / 2))
                xp += width
                points.add(Vec2(xp, -height / 2))
                endPoints.add(Vec2(xp, height / 2))
            }
            endPoints.reverse()
            builder.add(
                FigurePolyline(
                    (points + endPoints).map { c2 + it.rotate(angle) },
                    close = true
                )
            )
        }
    }

    protected fun polylineFromCommand(
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    ): List<IFigure> {
        val s = (0 until com.size).mapNotNull { index ->
            com.takeBlock(index)
        }.map { block ->
            figureList(block, figureExtractor) ?: FigureEmpty
            //?.list()
            //?.filterIsInstance(Approximation::class.java)
            // .orEmpty()
        }
        return s
    }

    protected fun figureArray(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        figure: IFigure,
        state: TortoiseState,
    ): IFigure {
        val ft = com.takeBlock(1)?.let { a -> a as? TortoiseParserStackBlock }
            ?.let { a ->

                val c = a.getBlockAtName("c")
                val r = a.getBlockAtName("r")
                val s = a.getBlockAtName("s")
                val m = (a.getBlockAtName("m")?.let { item ->
                    Vec2(
                        memory.value(item.get(1).orEmpty(), 0.0),
                        memory.value(item.get(2).orEmpty(), 0.0),
                    )
                } ?: Vec2.Zero)

                val columns = memory.value(c?.get(1).orEmpty(), 1.0).toInt()
                val rows = memory.value(r?.get(1).orEmpty(), 1.0).toInt()
                val scaleX = memory.value(s?.get(1).orEmpty(), 1.0)
                val scaleY = memory.value(s?.get(2).orEmpty(), scaleX)
                val distance = Vec2(
                    memory.value(c?.get(2).orEmpty(), 1.0),
                    memory.value(r?.get(2).orEmpty(), 1.0),
                )

                FigureArray(
                    figure = figure,
                    startPoint = m,
                    distance = distance,
                    columns = columns,
                    rows = rows,
                    angle = state.angle,
                    scaleX = scaleX,
                    scaleY = scaleY,
                )
            } ?: figure
        return ft
    }

    protected fun figure3d(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        g: IFigure,
        state: TortoiseState,
    ): IFigure {
        val angles = com.takeBlock(1)
        val translates = com.takeBlock(0)

        val mf = Matrix()

        translates?.let {
            mf.translate(
                memory.value(translates.get(0) ?: MemoryKey.ZERO, 0.0).toFloat(),
                memory.value(translates.get(1) ?: MemoryKey.ZERO, 0.0).toFloat(),
                memory.value(translates.get(2) ?: MemoryKey.ZERO, 0.0).toFloat(),
            )
        }

        angles?.let {
            mf.rotateX(memory.value(angles.get(0) ?: MemoryKey.ZERO, 0.0).toFloat())
            mf.rotateY(memory.value(angles.get(1) ?: MemoryKey.ZERO, 0.0).toFloat())
            mf.rotateZ(memory.value(angles.get(2) ?: MemoryKey.ZERO, 0.0).toFloat())
        }


        val f3d = if (mf.isIdentity())
            g
        else
            Figure3dTransform(
                vectors.Matrix(mf.values),
                g
            )

        val ft = com.takeBlock(3)?.let { a -> a as? TortoiseParserStackBlock }
            ?.let { a ->

                val c = a.getBlockAtName("c")
                val r = a.getBlockAtName("r")
                val s = a.getBlockAtName("s")
                val m = (a.getBlockAtName("m")?.let { item ->
                    Vec2(
                        memory.value(item.get(1).orEmpty(), 0.0),
                        memory.value(item.get(2).orEmpty(), 0.0),
                    )
                } ?: Vec2.Zero)

                val columns = memory.value(c?.get(1).orEmpty(), 1.0).toInt()
                val rows = memory.value(r?.get(1).orEmpty(), 1.0).toInt()
                val scaleX = memory.value(s?.get(1).orEmpty(), 1.0)
                val scaleY = memory.value(s?.get(2).orEmpty(), scaleX)
                val distance = Vec2(
                    memory.value(c?.get(2).orEmpty(), 1.0),
                    memory.value(r?.get(2).orEmpty(), 1.0),
                )

                FigureArray(
                    figure = f3d,
                    startPoint = m,
                    distance = distance,
                    columns = columns,
                    rows = rows,
                    angle = state.angle,
                    scaleX = scaleX,
                    scaleY = scaleY,
                )
            } ?: f3d

        return ft
    }

    protected fun roundrectangle(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        val width = com.value(memory)
        val height = com[1, width, memory]

        val width2 = width / 2
        val height2 = height / 2

        val smoothSize = com[2, min(width2, height2), memory]

        val c2 = builder.xy
        val angle = builder.angle

        builder.add(
            FigureRotate(
            FigureCreator.rectangle(
                -width2 + c2.x, -height2 + c2.y, width2 + c2.x, height2 + c2.y,
                enableSmooth = smoothSize != 0.0,
                smoothSize = smoothSize,
            ),
                angle,
                Vec2.Zero
            )
        )
    }

    protected fun triangle(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        val aa = com.value(memory)
        val bb = com[1, aa, memory]
        val cc = com[2, aa, memory]
        val angle = builder.angle
        val c2 = builder.xy

        val v = acos(
            (bb * bb + cc * cc - aa * aa) / (2 * bb * cc)
        )
        val vv = PI - if (v.isFinite()) v else 0.0

        val points = listOf<Vec2>(
            c2,
            c2 + Vec2(aa, 0.0).rotate(angle),
            c2 + Vec2(aa + bb * cos(vv), bb * sin(vv)).rotate(angle),
        )

        builder.add(FigurePolyline(points, true))
    }

    protected fun rectangle(
        width: Double,
        height: Double,
        c2: Vec2,
        angle: Double,
    ): IFigure {
        val width2 = width / 2
        val height2 = height / 2

        val points = listOf<Vec2>(
            c2 + Vec2(-width2, -height2).rotate(angle),
            c2 + Vec2(-width2, height2).rotate(angle),
            c2 + Vec2(width2, height2).rotate(angle),
            c2 + Vec2(width2, -height2).rotate(angle),
            c2 + Vec2(-width2, -height2).rotate(angle),
        )

        return FigurePolyline(points)
    }

    protected fun ellipse(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        builder: TortoiseBuilder,
    ) {
        val r1 = com.take(0, 0.0, memory)
        val r2 = com.take(1, r1, memory)

        if (com.size == 1) {
            builder.add(
                FigureCircle(
                    center = builder.state.xy,
                    radius = r1,
                )
            )
        } else
            if (com.size == 2) {
                builder.add(
                    FigureEllipse(
                        center = builder.state.xy,
                        radius = r1,
                        radiusMinor = r2,
                        rotation = builder.state.angle,
                    )
                )
            } else {

                for (d in 2 until com.size step 2) {
                    builder.add(
                        FigureEllipse(
                            center = builder.state.xy,
                            radius = r1,
                            radiusMinor = r2,
                            rotation = builder.state.angle,
                            segmentStart = com.take(d, 0.0, memory),
                            segmentSweep = com.take(d + 1, 360.0, memory),
                        )
                    )
                }
            }
    }

    protected fun circle(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        builder: TortoiseBuilder,
    ) {
        val r = com.take(0, 0.0, memory)

        if (com.size == 1) {
            builder.add(
                FigureCircle(
                    center = builder.state.xy,
                    radius = r,
                )
            )
        } else {
            for (d in 1 until com.size step 2) {
                builder.add(
                    FigureCircle(
                        center = builder.state.xy,
                        radius = r,
                        segmentStart = com.take(d + 0, 0.0, memory) - builder.state.angleInDegrees,
                        segmentSweep = com.take(d + 1, 360.0, memory),
                    )
                )
            }
        }
    }

    protected fun figureList(
        block: TortoiseParserStackItem?,
        figureExtractor: TortoiseFigureExtractor,
    ): IFigure? {
        return figureExtractor.figure(block)
    }

    protected fun figuresOnPath(
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    ): IFigure {
        val memory = figureExtractor.memory
        val block = com.takeBlock(0)
        val blockFigure = com.takeBlock(2)
        val blockProperties = com.takeBlock(1)
        val count = (blockProperties?.get(0) ?: MemoryKey.ZERO).toDoubleOrNull()?.toInt()
            ?: 0 //com.take(0, 2.0, memory).toInt()
        if (count > 0) {
            return figureList(block, figureExtractor)?.let { f ->
                f.list().filterIsInstance(IFigurePath::class.java).firstOrNull()
                    ?.let { path ->
                        figureList(
                            blockFigure,
                            figureExtractor,
                        )?.let { figure ->
                            FigureOnPath(
                                figure = figure,
                                path = path,
                                count = count,
                                distanceInPercent = valueAt(
                                    blockProperties,
                                    1,
                                    memory,
                                    1.0 / count,

                                ),
                                startOffsetInPercent = valueAt(
                                    blockProperties,
                                    2,
                                    memory,
                                    0.0,

                                ),
                                reverse = valueAt(
                                    blockProperties,
                                    7,
                                    memory,
                                    0.0,
                                ) >= 1.0,
                                useNormal = valueAt(
                                    blockProperties,
                                    4,
                                    memory,
                                    1.0,
                                ) >= 1.0,
                                angle = valueAt(blockProperties, 3,  memory),
                                pivot = Vec2(
                                    valueAt(blockProperties, 5,  memory),
                                    valueAt(blockProperties, 6,  memory)
                                ),
                            )
                        }
                    }
            } ?: FigureEmpty
        } else
            return FigureEmpty
    }

    fun figureGroups(
        commands: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    ): IFigure {
        /* (figure) (edge delta*) (f) + */
        val block = commands.takeBlock(0)
        if (commands.size < 2) {
            return FigureEmpty
        }

        return figureList(block, figureExtractor)?.let { f ->
            val paths = figureExtractor.collectPaths(f)
            FigureList(if (paths.isNotEmpty()) {
                (1 until commands.size step 2).flatMap {
                    val positions = commands.takeBlock(it)
                    val figures = commands.takeBlock(it + 1)
                    val resf: List<IFigure> = figureList(
                        figures,
                        figureExtractor
                    )?.let { figure ->
                        positions?.inner?.getOrNull(0)?.let { edgeMem ->
                            val edge = figureExtractor.memory.value(edgeMem.value, 0.0).toInt()
                            positions?.inner.orEmpty().drop(1).map { pos ->
                                figureExtractor.memory.value(pos.value, 0.0)
                            }.mapNotNull { delta ->
                                positionInPath(paths, edge, delta)?.let { p ->
                                    FigureTranslateWithRotate(
                                        figure,
                                        p.point,
                                        p.normal.angle * 180 / PI
                                    )
                                }
                            }
                        }
                    }.orEmpty()
                    resf
                }
            } else
                emptyList<IFigure>()
            ) + f
        } ?: FigureEmpty
    }

    abstract fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        figureExtractor:TortoiseFigureExtractor,
    ): List<IFigure>
}