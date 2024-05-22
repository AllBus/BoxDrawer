package turtoise

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.isIdentity
import com.kos.figure.Figure
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureOnPath
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
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

abstract class TortoiseBase {

    /**in degrees */
    protected fun calculateAngle(y: Double, x: Double): Double {
        return atan2(y, x) * 180.0 / PI
    }

    protected fun valueAt(
        blockProperties: TortoiseParserStackItem?,
        index: Int,
        defaultValue: Double,
        memory: TortoiseMemory
    ): Double {
        return memory.value(blockProperties?.get(index) ?: MemoryKey.ZERO, defaultValue)
    }

    protected fun rectangle(
        state: TortoiseState,
        com: TortoiseCommand,
        memory: TortoiseMemory,
        res: MutableList<IFigure>
    ) {
        var c2 = state.xy
        val angle = state.angle
        for (d in 0 until com.size step 2) {
            val width = com[d, 0.0, memory]
            val height = com[d + 1, width, memory]
            if (d > 0) {
                c2 += Vec2(width / 2, 0.0).rotate(angle)
            }
            res.add(rectangle(width, height, c2, angle))
            c2 += Vec2(width / 2, 0.0).rotate(angle)
        }
    }

    protected fun figuresOnPath(
        com: TortoiseCommand,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure {
        val block = com.takeBlock(0)
        val blockFigure = com.takeBlock(1)
        val blockProperties = com.takeBlock(2)
        val count = (blockProperties?.get(0) ?: MemoryKey.ZERO).toDoubleOrNull()?.toInt()
            ?: 0 //com.take(0, 2.0, memory).toInt()
        if (count > 0) {
            return figureList(block, ds, state, maxStackSize, memory, runner)?.let { f ->
                f.list().filterIsInstance(IFigurePath::class.java).firstOrNull()
                    ?.let { path ->
                        figureList(
                            blockFigure,
                            ds,
                            state,
                            maxStackSize,
                            memory,
                            runner
                        )?.let { figure ->
                            FigureOnPath(
                                figure = figure,
                                path = path,
                                count = count,
                                distanceInPercent = valueAt(
                                    blockProperties,
                                    1,
                                    1.0 / count,
                                    memory
                                ),
                                startOffsetInPercent = valueAt(
                                    blockProperties,
                                    2,
                                    0.0,
                                    memory
                                ),
                                reverse = valueAt(
                                    blockProperties,
                                    7,
                                    0.0,
                                    memory
                                ) >= 1.0,
                                useNormal = valueAt(
                                    blockProperties,
                                    4,
                                    1.0,
                                    memory
                                ) >= 1.0,
                                angle = valueAt(blockProperties, 3, 0.0, memory),
                                pivot = Vec2(
                                    valueAt(blockProperties, 5, 0.0, memory),
                                    valueAt(blockProperties, 6, 0.0, memory)
                                ),
                            )
                        }
                    }
            } ?: Figure.Empty
        } else
            return Figure.Empty
    }

    protected fun polylineFromCommand(
        com: TortoiseCommand,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner
    ): List<IFigure> {
        val s = (0 until com.size).mapNotNull { index ->
            com.takeBlock(index)
        }.map { block ->
            figureList(block, ds, state, maxStackSize, memory, runner) ?: Figure.Empty
            //?.list()
            //?.filterIsInstance(Approximation::class.java)
            // .orEmpty()
        }
        return s
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

        mf.rotateX(memory.value(angles?.get(0) ?: MemoryKey.ZERO, 0.0).toFloat())
        mf.rotateY(memory.value(angles?.get(1) ?: MemoryKey.ZERO, 0.0).toFloat())
        mf.rotateZ(memory.value(angles?.get(2) ?: MemoryKey.ZERO, 0.0).toFloat())


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
        com: TortoiseCommand,
        memory: TortoiseMemory,
        state: TortoiseState,
        res: MutableList<IFigure>
    ) {
        val width = com.value(memory)
        val height = com[1, width, memory]

        val width2 = width / 2
        val height2 = height / 2

        val smoothSize = com[2, min(width2, height2), memory]

        val c2 = state.xy
        val angle = state.angle

        res.add(
            FigureCreator.rectangle(
                -width2 + c2.x, -height2 + c2.y, width2 + c2.x, height2 + c2.y,
                enableSmooth = smoothSize != 0.0,
                smoothSize = smoothSize,
            ).rotate(angle)
        )
    }

    protected fun triangle(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        state: TortoiseState,
        res: MutableList<IFigure>
    ) {
        val aa = com.value(memory)
        val bb = com[1, aa, memory]
        val cc = com[2, aa, memory]
        val angle = state.angle
        val c2 = state.xy

        val v = acos(
            (bb * bb + cc * cc - aa * aa) / (2 * bb * cc)
        )
        val vv = PI - if (v.isFinite()) v else 0.0

        val points = listOf<Vec2>(
            c2,
            c2 + Vec2(aa, 0.0).rotate(angle),
            c2 + Vec2(aa + bb * cos(vv), bb * sin(vv)).rotate(angle),
        )

        res.add(FigurePolyline(points, true))
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
        res: MutableList<IFigure>,
        state: TortoiseState
    ) {
        val r1 = com.take(0, 0.0, memory)
        val r2 = com.take(1, r1, memory)

        if (com.size == 1) {
            res.add(
                FigureCircle(
                    center = state.xy,
                    radius = r1,
                )
            )
        } else
            if (com.size == 2) {
                res.add(
                    FigureEllipse(
                        center = state.xy,
                        radius = r1,
                        radiusMinor = r2,
                        rotation = state.angle,
                    )
                )
            } else {

                for (d in 2 until com.size step 2) {
                    res.add(
                        FigureEllipse(
                            center = state.xy,
                            radius = r1,
                            radiusMinor = r2,
                            rotation = state.angle,
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
        res: MutableList<IFigure>,
        state: TortoiseState
    ) {
        val r = com.take(0, 0.0, memory)

        if (com.size == 1) {
            res.add(
                FigureCircle(
                    center = state.xy,
                    radius = r,
                )
            )
        } else {
            for (d in 1 until com.size step 2) {
                res.add(
                    FigureCircle(
                        center = state.xy,
                        radius = r,
                        segmentStart = com.take(d + 0, 0.0, memory) - state.a,
                        segmentSweep = com.take(d + 1, 360.0, memory) - state.a,
                    )
                )
            }
        }
    }

    protected fun figureList(
        block: TortoiseParserStackItem?,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure? {
        val f = block?.let { b ->
            val l = TortoiseParser.parseSimpleLine(block)
            val st = TortoiseState() //.from(state)


            val n = block.name.name
            if (n.startsWith("@")) {
                runner.figure(
                    algName = n.drop(1),
                    ds = ds,
                    state = st,
                    maxStackSize = maxStackSize,
                    arguments = block
                )

            } else {
                FigureList(
                    l.commands(l.names.first(), ds).flatMap { c ->
                        draw(
                            commands = c,
                            state = st,
                            ds = ds,
                            maxStackSize = maxStackSize - 1,
                            memory = memory,
                            runner = runner,
                        )
                    }
                )
            }

        }
        return f?.let { FigureTranslate(FigureRotate(it, state.a, Vec2.Zero), state.xy) }
    }

    abstract fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): List<IFigure>
}