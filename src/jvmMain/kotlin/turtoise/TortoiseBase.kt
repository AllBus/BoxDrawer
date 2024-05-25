package turtoise

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.isIdentity
import com.kos.boxdrawer.detal.soft.SoftRez
import com.kos.figure.Figure
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureOnPath
import com.kos.figure.composition.FigureTranslateWithRotate
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MemoryKey.Companion.ZERO
import turtoise.memory.keys.MemoryKey.Companion.orEmpty
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sign
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
        defaultValue: Double,
        memory: TortoiseMemory
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
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner
    ): List<IFigure> {
        val s = (0 until com.size).mapNotNull { index ->
            com.takeBlock(index)
        }.map { block ->
            figureList(block, ds, maxStackSize, memory, runner) ?: Figure.Empty
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
            FigureCreator.rectangle(
                -width2 + c2.x, -height2 + c2.y, width2 + c2.x, height2 + c2.y,
                enableSmooth = smoothSize != 0.0,
                smoothSize = smoothSize,
            ).rotate(angle)
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
                        segmentStart = com.take(d + 0, 0.0, memory) - builder.state.a,
                        segmentSweep = com.take(d + 1, 360.0, memory) - builder.state.a,
                    )
                )
            }
        }
    }

    protected fun figureList(
        block: TortoiseParserStackItem?,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure? {
        val f = block?.let { b ->
            val l = TortoiseParser.parseSimpleLine(block)
            val st = TortoiseState()

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
        return f
    }

    protected fun figuresOnPath(
        com: TortoiseCommand,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure {
        val block = com.takeBlock(0)
        val blockFigure = com.takeBlock(2)
        val blockProperties = com.takeBlock(1)
        val count = (blockProperties?.get(0) ?: MemoryKey.ZERO).toDoubleOrNull()?.toInt()
            ?: 0 //com.take(0, 2.0, memory).toInt()
        if (count > 0) {
            return figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                f.list().filterIsInstance(IFigurePath::class.java).firstOrNull()
                    ?.let { path ->
                        figureList(
                            blockFigure,
                            ds,
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

    fun figureGroups(
        commands: TortoiseCommand,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure {
        val block = commands.takeBlock(0)
        if (commands.size < 2) {
            return Figure.Empty
        }

        return figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
            val paths = f.list().filterIsInstance(IFigurePath::class.java)
            FigureList(if (paths.isNotEmpty()) {
                (1 until commands.size step 2).flatMap {
                    val positions = commands.takeBlock(it)
                    val figures = commands.takeBlock(it + 1)
                    val resf: List<IFigure> = figureList(
                        figures,
                        ds,
                        maxStackSize,
                        memory,
                        runner
                    )?.let { figure ->
                        positions?.inner?.getOrNull(0)?.let { edgeMem ->
                            val edge = memory.value(edgeMem.value, 0.0).toInt()
                            positions?.inner.orEmpty().drop(1).map { pos ->
                                memory.value(pos.value, 0.0)
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

    private fun positionInPath(paths: List<IFigurePath>, edge: Int, delta: Double): PointWithNormal? {
        var e = edge
        for ( p in paths){
            if (p.edgeCount()> e){
                return p.positionInPath(e, delta)
            } else {
                e -= p.edgeCount()
            }
        }
        return null
    }

    protected fun figuresSplash(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ) {
        val command = com.takeBlock(0)?.name?.name
        when (command) {
            "length" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val variables = com.takeBlock(2)?.inner.orEmpty()
                        f.list().filterIsInstance(IFigurePath::class.java).zip(variables) { p, v ->
                            memory.assign(v.argument, p.pathLength())
                        }
                    }
                }
            }

            "arc" -> {
                val cs = com.size
                val figures = FigureList(
                    (1 until cs).map { i -> com.takeBlock(i) }.map { block ->
                        val r = memory.value(block?.get(0) ?: ZERO, 0.0)
                        val p = Vec2(memory.value(block?.get(1) ?: ZERO, 0.0), 0.0)
                        val z = Vec2(memory.value(block?.get(2) ?: ZERO, 0.0), 0.0)
                        arcInTwoPoint(p, z, r)
                    }
                )
                builder.addProduct(figures)
            }

            "tooth" -> {
                builder.startPoint()
                if (builder.state.zigParam.reverse) {
                    toothreverse(builder, com, memory)
                } else {
                    tooth(builder, com, memory)
                }
            }

            "toothr" -> {
                builder.startPoint()
                if (builder.state.zigParam.reverse) {
                    tooth(builder, com, memory)
                } else {
                    toothreverse(builder, com, memory)
                }
            }

            "line" -> {
                builder.startPoint()
                for (i in 1 until com.size step 2) {
                    val a = com[i, memory]
                    val b = com[i + 1, memory]
                    builder.state.move(a, -b)
                    builder.add(builder.xy)
                }
            }

            "rline",
            "roundline" -> {

                val cr = Vec2.Zero
                val rest = mutableListOf<IFigure>()

                val i = 1
                val r = com[i, memory]
                val ax = com[i + 1, memory]
                val ay = com[i + 2, memory]
                val ll = if (com.size > 4) com[i + 3, memory] else null
                val aa = Vec2(ax, ay)

                val aap = if (r > 0) {
                    val preda = sign(ay) * 90
                    val cc = Vec2(0.0, sign(ay) * r)

                    val al = (aa - cc).angle
                    val hl = Vec2.distance(cc, aa)
                    val rv = asin(r / hl) * sign(ay)
                    val alp = al + rv
                    val p = cc + Vec2(0.0, r).rotate((if (ay > 0) PI + alp else alp))

                    //   println (" ${ rv *180/ PI} : ${al*PI/180} : ${p} ${hl} ${r}")
                    //rest += FigureLine(p + cr, cc + cr)

                    val ap = if (ll == null) {
                        aa
                    } else {
                        p+Vec2.normalize( p, aa ) * ll
                    }
                    if (ap != p) {
                        rest += FigureLine(p + cr, cr + ap)
                    }

                    rest += FigureCircle(cr + cc, r, preda, -alp * 180 / PI)
                    ap
                } else {
                    val ap = if (ll == null) {
                        aa
                    } else {
                        Vec2.normalize(Vec2.Zero, aa) * ll
                    }

                    if (ap != Vec2.Zero) {
                        rest += FigureLine(cr, ap + cr)
                    }
                    ap
                }

                builder.addProduct(FigureList(rest))
                builder.state.move(aap.x, aap.y)
            }

            "rez" -> {
                val width = com[1, memory]
                val height = com[2, memory]
                val delta = com[3, 5.2, memory]
                val dlina = com[4, 18.0, memory]
                val soedinenie = com[5, 6.0, memory]
                val firstSmall = com[6, memory]

                val figures = SoftRez().drawRez(
                    width,
                    height,
                    delta,
                    dlina,
                    soedinenie,
                    firstSmall > 0
                )
                builder.addProduct(figures)
            }

            "r" -> {
                builder. state.zigParam =builder. state.zigParam.copy(reverse = true)
            }

            "f" -> {
                builder.state.zigParam = builder.state.zigParam.copy(reverse = false)
            }

            "paz" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = f.list().filterIsInstance(IFigurePath::class.java)
                        val v = (2  until com.size).mapNotNull { j ->
                            com.takeBlock(j)?.let{ item ->
                                val e = memory.value(item.get(0)?: ZERO, 0.0)
                                val d = memory.value(item.get(1)?: ZERO, 0.0)
                                val zigle = item.get(2)?.let{ memory.value(it, 15.0)} ?: 15.0
                                val zighe = item.get(3)?.let{ memory.value(it, ds.boardWeight)} ?: ds.boardWeight
                                positionInPath(paths, e.toInt() , d)?.let{ pos ->
                                    FigurePolyline(
                                        listOf(
                                            Vec2(-zigle, 0.0),
                                            Vec2(-zigle, zighe),
                                            Vec2(zigle, zighe),
                                            Vec2(zigle, 0.0),
                                        ).map { pos.point+it.rotate(pos.normal.angle+PI/2) }
                                    )
                                }
                            }
                        }
                        builder.addProduct(FigureList(v+f))
                    }
                }
            }

            "stena" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = f.list().filterIsInstance(IFigurePath::class.java)
//                        val v = (2  until com.size).mapNotNull { j ->
                        val h = com.takeBlock(2)?.let { item ->
                            memory.value(item.get(0) ?: ZERO, 0.0)
                            //val d = memory.value(item.get(1)?: ZERO, 0.0)
//                                val zigle = item.get(2)?.let{ memory.value(it, 15.0)} ?: 15.0
//                                val zighe = item.get(3)?.let{ memory.value(it, ds.boardWeight)} ?: ds.boardWeight
//                                positionInPath(paths, e.toInt() , d)?.let{ pos ->
//                                    FigurePolyline(
//                                        listOf(
//                                            Vec2(-zigle, 0.0),
//                                            Vec2(-zigle, zighe),
//                                            Vec2(zigle, zighe),
//                                            Vec2(zigle, 0.0),
//                                        ).map { pos.point+it.rotate(pos.normal.angle+PI/2) }
//                                    )
//                                }
                        } ?: 10.0
                        val heights = (3 until com.size).mapNotNull {j ->
                            com.takeBlock(j)?.let { item ->
                                val e = (item.get(0)?.let { memory.value(it, 0.0) } ?: 0.0).toInt()
                                val h = (item.get(1)?.let { memory.value(it, 0.0) } ?: 0.0)
                                e to h
                            }
                        }.toMap()

                        var xc = 0.0
                        var edge = 0
                        val v = paths.flatMap { path ->
                            val er = edge
                            edge += path.edgeCount()
                            (0 until path.edgeCount()).map { e ->
                                val he = heights.get(e + er) ?: h
                                val le = path.pathLength(e)
                                val f = FigurePolyline(
                                    listOf(
                                        Vec2(xc, 0.0),
                                        Vec2(xc + le, 0.0),
                                        Vec2(xc + le, he),
                                        Vec2(xc, he),
                                    ),
                                    close = true
                                )
                                xc += le
                                f
                            }
                        }

                        builder.addProduct(FigureList(v))
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun arcInTwoPoint(p: Vec2, z: Vec2, radius: Double): IFigure {
        val distance2 = Vec2.distance(p, z) / 2
        return if (radius >= distance2) {
            val hyp = sqrt(radius * radius - distance2 * distance2)
            val pza = (z - p).angle
            val h2 = (p + z) / 2.0 + Vec2(0.0, hyp).rotate(pza)

            val b = (z - h2).angle
            val a = (p - h2).angle
            //      println("$r $p $z $h2 $a $b")
            FigureCircle(-h2, radius, a * 180 / PI, (b - a) * 180 / PI)
        } else
            FigureEmpty
    }

    private fun toothreverse(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        for (i in 1 until com.size step 2) {
            val a = com[i, memory]
            val b = com[i + 1, memory]
            builder.state.move(0.0, -b)
            builder.addPoint()
            builder.state.move(a, b)
            builder.addPoint()
        }
    }

    private fun tooth(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        memory: TortoiseMemory,
    ) {
        for (i in 1 until com.size step 2) {
            val a = com[i, memory]
            val b = com[i + 1, memory]
            builder.state.move(a, -b)
            builder.addPoint()
            builder.state.move(0.0, b)
            builder.addPoint()
        }
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