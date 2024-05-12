package turtoise

import androidx.compose.ui.graphics.Matrix
import com.kos.figure.FigureBezier
import com.kos.figure.FigureBezierList
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.FigureSpline
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureOnPath
import com.kos.figure.matrix.FigureMatrixRotate
import com.kos.figure.matrix.FigureMatrixScale
import com.kos.figure.matrix.FigureMatrixTranslate
import turtoise.memory.keys.MemoryKey.Companion.orEmpty
import turtoise.memory.TortoiseMemory
import vectors.Vec2
import java.util.Stack
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.truncate

private const val MAX_REGULAR_POLYGON_EDGES = 500

class Tortoise() {

    fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): List<IFigure> {
        if (maxStackSize <= 0)
            return emptyList()

        val res = mutableListOf<IFigure>()
        var result = mutableListOf<Vec2>()
        val le = commands.size
        var i = 0

        /** стак вызовов циклов */
        var stack: TortoiseStack? = null
        val stateStack = Stack<TortoiseState>()
        var cancel = false

        fun saveLine() {
            if (result.size > 1) {
                res.add(FigurePolyline(points = result.toList()))
            }
            result = mutableListOf()
        }

        while (i < le && !cancel) {
            val com = commands[i]

            when (com.command) {
                TortoiseCommand.TURTOISE_CLEAR -> state.clear()
                TortoiseCommand.TURTOISE_CIRCLE -> circle(com, memory, res, state)
                TortoiseCommand.TURTOISE_ELLIPSE -> ellipse(com, memory, res, state)

                TortoiseCommand.TURTOISE_MOVE -> {
                    state.move(com[0, memory], com[1, memory])
                }

                TortoiseCommand.TURTOISE_HORIZONTAL -> {
                    if (result.isEmpty()) {
                        result.add(state.xy)
                    }
                    state.x += com.value(memory)
                    result.add(state.xy)
                }

                TortoiseCommand.TURTOISE_VERTICAL -> {
                    if (result.isEmpty()) {
                        result.add(state.xy)
                    }
                    state.y += com.value(memory)
                    result.add(state.xy)
                }

                TortoiseCommand.TURTOISE_ANGLE -> {
                    if (com.size == 2) {
                        state.a = calculateAngle(com[0, memory], com[1, memory])
                    } else {
                        state.a = com.value(memory)
                    }
                }

                TortoiseCommand.TURTOISE_ANGLE_ADD -> {
                    if (com.size == 2) {
                        state.a += calculateAngle(com[0, memory], com[1, memory])
                    } else {
                        state.a += com.value(memory)
                    }
                }

                TortoiseCommand.TURTOISE_LINE -> {
                    val v = com.value(memory)
                    if (result.isEmpty() && v != 0.0) {
                        result.add(state.xy)
                    }
                    state.move(v)
                    result.add(state.xy)
                    for (d in 1 until com.size) {
                        if (d % 2 == 0) {
                            state.move(com[d, memory])
                        } else {
                            state.move90(com[d, memory])
                        }
                        result.add(state.xy)
                    }
                }

                TortoiseCommand.TURTOISE_LINE_WITH_ANGLE -> {
                    val v = com.value(memory)
                    if (result.isEmpty() && v != 0.0) {
                        result.add(state.xy)
                    }
                    state.move(v)
                    result.add(state.xy)
                    val currentAngle = state.a;
                    for (d in 1 until com.size step 2) {
                        val a2 = com[d, memory]
                        val di = com[d + 1, memory]
                        state.a = currentAngle + a2;
                        state.move(di);
                        result.add(state.xy)
                    }
                    state.a = currentAngle;
                }

                TortoiseCommand.TURTOISE_LINE_PERPENDICULAR -> {
                    state.move90(com.value(memory))
                    result.add(state.xy)
                }

                TortoiseCommand.TURTOISE_CLOSE -> {
                    if (result.size > 2) {
                        result.add(result.first())
                        saveLine()
                    }
                }

                TortoiseCommand.TURTOISE_SPLIT -> {
                    saveLine()
                }

                TortoiseCommand.TURTOISE_RECTANGLE -> {
                    rectangle(com, memory, state, res)
                }

                TortoiseCommand.TURTOISE_ROUND_RECTANGLE -> {
                    roundrectangle(com, memory, state, res)
                }

                TortoiseCommand.TURTOISE_REGULAR_POLYGON -> {
                    val r = com.take(1, 0.0, memory)
                    val count = min(com.take(0, 3.0, memory).toInt(), MAX_REGULAR_POLYGON_EDGES)

                    res.add(regularPolygon(
                        center = state.xy,
                        count = count,
                        angle = state.angle,
                        radius = r
                    ))
                }

                TortoiseCommand.TURTOISE_FIGURE -> {
                    val block = com.takeBlock(0)
                    figureList(block, ds, state, maxStackSize, memory, runner)?.let { g ->
                        res.add(
                            g
                        )
                    }
                }

                TortoiseCommand.TURTOISE_PATH -> {
                    val block = com.takeBlock(0)
                    val blockFigure = com.takeBlock(1)
                    val blockProperties = com.takeBlock(2)
                    val count = com.take(0, 2.0, memory).toInt()
                    if (count>0) {
                        figureList(block, ds, state, maxStackSize, memory, runner)?.let { f ->
                            f.list().filterIsInstance(IFigurePath::class.java).firstOrNull()?.let { path ->

                                figureList(
                                    blockFigure,
                                    ds,
                                    state,
                                    maxStackSize,
                                    memory,
                                    runner
                                )?.let { figure ->
                                    res += FigureOnPath(
                                        figure = figure,
                                        path = path,
                                        count = count,
                                        distanceInPercent = com.take(1, 1.0/count, memory),
                                        startOffsetInPercent = com.take(2, 0.0, memory),
                                        reverse = com.take(7, 0.0, memory)>=1.0,
                                        useNormal = com.take(4, 1.0, memory)>=1.0,
                                        angle = com.take(3, 0.0, memory),
                                        pivot = Vec2(com.take(5, 0.0, memory), com.take(6, 0.0, memory)),
                                    )
                                }
                            }
                        }
                    }
                }

                TortoiseCommand.TURTOISE_ZIGZAG_FIGURE -> {
                    saveLine()

                    val zigWidth = com.take(2, state.zigWidth, memory)
                    val board = com.take(3, ds.boardWeight, memory)

                    val block = com.takeBlock(0)
                    val f = figureList(block, ds, TortoiseState(), maxStackSize, memory, runner)
                    val zf = f ?: zigFigure(
                        hz = com.take(4, 0.0, memory),
                        bz1x = com.take(5, board / 2, memory),
                        bz2x = com.take(6, board / 2, memory),
                        bz1y = com.take(7, 0.0, memory),
                        bz2y = com.take(8, 0.0, memory),
                        board = board,
                        zigWidth = zigWidth
                    )

                    val width = com.value(memory)
                    res += ZigConstructor.zigZag(
                        origin = state.xy,
                        width = width,
                        zig = ZigzagInfo(
                            zigWidth,
                            com.take(1, state.zigDelta, memory),
                        ),
                        angle = state.angle,
                        param = state.zigParam,
                        zigzagFigure = zf

                    )
                    state.move(width)

                }

                TortoiseCommand.TURTOISE_ZIGZAG -> {
                    if (result.size == 0) {
                        result.add(state.xy)
                    }

                    zigzag(
                        points = result,
                        origin = result.last(),
                        width = com.value(memory),
                        zig = ZigzagInfo(
                            com.take(2, state.zigWidth, memory),
                            com.take(1, state.zigDelta, memory),
                        ),
                        angle = state.angle,
                        param = state.zigParam,
                        boardWeight = com.take(3, ds.boardWeight, memory)
                    )

                    state.moveTo(result.last());

                }

                TortoiseCommand.TURTOISE_BEZIER -> {
                    saveLine()

                    val angle = state.angle

                    val points = mutableListOf<Vec2>()
                    points.add(state.xy)
                    for (d in 0..com.size - 6 step 6) {
                        points.add(
                            Vec2(
                                com[d + 0, memory],
                                com[d + 1, memory]
                            ).rotate(angle) + state.xy
                        )

                        val xy = Vec2(
                            com[d + 4, memory],
                            com[d + 5, memory]
                        ).rotate(angle) + state.xy

                        points.add(
                            Vec2(
                                com[d + 2, memory],
                                com[d + 3, memory]
                            ).rotate(angle) + state.xy
                        )
                        points.add(xy)
                        state.moveTo(xy)
                    }

                    if (points.size > 1) {
                        res.add(FigureBezier(points.toList()))
                    }
                }

                TortoiseCommand.TURTOISE_SPLINE -> {
                    saveLine()

                    val angle = state.angle

                    val points = mutableListOf<Vec2>()
                    points.add(state.xy)
                    for (d in 1..com.size - 2 step 2) {
                        val xy = Vec2(
                            com[d + 0, memory],
                            com[d + 1, memory]
                        ).rotate(angle) + state.xy
                        points.add(xy)
                        state.moveTo(xy)
                    }
                    if (points.size > 1) {
                        res.add(FigureSpline(points.toList()))
                    }
                }

                TortoiseCommand.TURTOISE_POLYLINE -> {
                    saveLine()
                    val c2 = state.xy
                    val angle = state.angle

                    val points = mutableListOf<Vec2>()
                    for (d in 0..com.size - 2 step 2) {
                        points.add(
                            Vec2(
                                com[d + 0, memory],
                                com[d + 1, memory]
                            ).rotate(angle) + c2
                        )
                    }
                    if (points.size > 1) {
                        res.add(FigurePolyline(points.toList()))
                    }
                }

                TortoiseCommand.TURTOISE_LOOP -> {
                    stack = TortoiseStack(
                        top = stack,
                        position = i,
                        counter = min(com.value(memory).toInt(), 100) - 1
                    )
                }

                TortoiseCommand.TURTOISE_END_LOOP -> {
                    val p = stack
                    if (p != null) {
                        stack = if (p.counter > 0) {
                            i = p.position

                            TortoiseStack(
                                top = p.top,
                                position = i,
                                counter = p.counter - 1
                            )
                        } else {
                            p.top
                        }
                    } else {
                        cancel = true
                    }
                }

                TortoiseCommand.TURTOISE_MOVE_TO -> {
                    state.moveTo(Vec2(com[0, memory], com[1, memory]))
                }

                TortoiseCommand.TURTOISE_MATRIX_TRANSLATE -> {
                    res.add(FigureMatrixTranslate(com[0, memory], com[1, memory]))
                }

                TortoiseCommand.TURTOISE_MATRIX_SCALE -> {
                    res.add(FigureMatrixScale(com[0, 1.0, memory], com[1, 1.0, memory]))
                }

                TortoiseCommand.TURTOISE_MATRIX_ROTATE -> {
                    res.add(FigureMatrixRotate(com[0, 1.0, memory], state.xy))
                }

                TortoiseCommand.TURTOISE_MEMORY_ASSIGN -> {
                    com.assign(memory)
                }

                TortoiseCommand.TURTOISE_3D -> {
                    val block = com.takeBlock(0)
                    val f = figureList(block, ds, state, maxStackSize, memory, runner)

                    f?.let { g ->

                        figure3d(com, memory, g, state, res)
                    }
                }

                TortoiseCommand.TURTOISE_SAVE -> {
                    stateStack.push(TortoiseState().from(state))
                }

                TortoiseCommand.TURTOISE_LOAD -> {
                    if (stateStack.isNotEmpty()) {
                        state.from(stateStack.pop())
                    }
                }

                TortoiseCommand.TURTOISE_PEEK -> {
                    if (stateStack.isNotEmpty()) {
                        state.from(stateStack.peek())
                    }
                }

                else -> {

                }
            } // end when
            i++
        }//end while
        saveLine()
        return res

    }
    private fun figure3d(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        g: IFigure,
        state: TortoiseState,
        res: MutableList<IFigure>
    ): Boolean {
        val mf = Matrix()

        mf.translate(
            com[3, memory].toFloat(),
            com[4, memory].toFloat(),
            com[5, memory].toFloat()
        )

        mf.rotateX(com[0, memory].toFloat())
        mf.rotateY(com[1, memory].toFloat())
        mf.rotateZ(com[2, memory].toFloat())


        val f3d = Figure3dTransform(
            vectors.Matrix(mf.values),
            g
        )

        val ft = com.takeBlock(1)?.let { a -> a as? TurtoiseParserStackBlock }
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

        return res.add(
            ft
        )
    }

    private fun roundrectangle(
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
            rectangle(
                -width2 + c2.x, -height2 + c2.y, width2 + c2.x, height2 + c2.y,
                enableSmooth = smoothSize != 0.0,
                smoothSize = smoothSize,
            ).rotate(angle)
        )
    }

    private fun rectangle(
        com: TortoiseCommand,
        memory: TortoiseMemory,
        state: TortoiseState,
        res: MutableList<IFigure>
    ) {
        val width = com.value(memory)
        val height = com[1, width, memory]

        val width2 = width / 2
        val height2 = height / 2

        val c2 = state.xy
        val angle = state.angle
        val points = listOf<Vec2>(
            c2 + Vec2(-width2, -height2).rotate(angle),
            c2 + Vec2(-width2, height2).rotate(angle),
            c2 + Vec2(width2, height2).rotate(angle),
            c2 + Vec2(width2, -height2).rotate(angle),
            c2 + Vec2(-width2, -height2).rotate(angle),
        )

        res.add(FigurePolyline(points))
    }

    private fun ellipse(
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
                            segmentEnd = com.take(d + 1, 0.0, memory),
                        )
                    )
                }
            }
    }

    private fun circle(
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
                        segmentEnd = com.take(d + 1, 0.0, memory) - state.a,
                    )
                )
            }
        }
    }

    private fun figureList(
        block: TurtoiseParserStackItem?,
        ds: DrawerSettings,
        state: TortoiseState,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    ): IFigure? {
        val f = block?.let { b ->
            val l = TortoiseParser.parseSimpleLine(block)
            val st = TortoiseState().from(state)

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

    private fun calculateAngle(y: Double, x: Double): Double {
        return atan2(y, x) * 180.0 / PI
    }

    companion object {

        fun regularPolygon(
            center:Vec2,
            count: Int,
            angle: Double,
            radius: Double,
        ):IFigure {
            val p = (1..count).map { ind ->
                val ang = angle + 2 * Math.PI * ind / count
                center + Vec2(sin(ang), cos(ang)) * radius
            }
            return  FigurePolyline(p, true)
        }

        fun zigzag(
            points: MutableList<Vec2>,
            origin: Vec2,
            width: Double,
            zig: ZigzagInfo,
            angle: Double,
            param: DrawingParam,
            boardWeight: Double
        ) {
            val bot = if (param.back) -1 else 1
            val z = 0.0
            val angleV =  angle

            if (!zig.enable) {
                points.add(Vec2(width * bot, z).rotate(angleV) + origin)
                return
            }
            var zigzagWidthV = zig.width
            var deltaV = zig.delta

            if (deltaV > width) {
                deltaV = width
            }
            if (zigzagWidthV > deltaV) {
                zigzagWidthV = deltaV - boardWeight * 2
                if (zigzagWidthV < boardWeight) {
                    points.add(Vec2(width * bot, z).rotate(angleV) + origin)
                    return
                }
            }


            val distance = deltaV - zigzagWidthV
            val count = truncate(width / deltaV).toInt()

            var offset: Double = (width - deltaV * count + distance) / 2 * bot

            val zv = if (zig.height == 0.0) boardWeight else zig.height
            val weight = if (param.reverse) -zv else zv


            deltaV *= bot.toDouble()
            val zw = zigzagWidthV * bot
            if (count > 10000) {
                points.add(Vec2(width * bot, z).rotate(angleV) + origin)
                return
            }

            offset += 0.0

            for (i in 0 until count) {
                points.add(Vec2(offset, z).rotate(angleV) + origin)
                points.add(Vec2(offset, z + weight).rotate(angleV) + origin)
                points.add(Vec2(offset + zw, z + weight).rotate(angleV) + origin)
                points.add(Vec2(offset + zw, z).rotate(angleV) + origin)
                offset += deltaV
            }
            points.add(Vec2(width * bot, z).rotate(angleV) + origin)
        }

        fun holes(
            origin: Vec2,
            width: Double,
            zig: ZigzagInfo,
            angle: Double,
            param: DrawingParam,
            boardWeight: Double
        ): List<IFigure> {
            if (!zig.enable) {
                return emptyList()
            }

            var zigzagWidthV = zig.width
            var deltaV = zig.delta

            if (deltaV > width) {
                deltaV = width
            }
            if (zigzagWidthV > deltaV) {
                zigzagWidthV = deltaV - boardWeight * 2
                if (zigzagWidthV < boardWeight) return emptyList()
            }

            val bot = if (param.back) -1 else 1
            val distance = deltaV - zigzagWidthV
            val count = truncate(width / deltaV).toInt()

            var offset: Double = (width - deltaV * count + distance) / 2 * bot

            val weight = if (param.reverse) -boardWeight else boardWeight

            deltaV *= bot.toDouble()
            val zw = zigzagWidthV * bot
            if (count > 10000) return emptyList()

            offset += 0.0
            val z = 0.0

            return (0 until count).map { i ->
                val v = FigurePolyline(
                    listOf(
                        Vec2(offset, z).rotate(angle) + origin,
                        Vec2(offset, z + weight).rotate(angle) + origin,
                        Vec2(offset + zw, z + weight).rotate(angle) + origin,
                        Vec2(offset + zw, z).rotate(angle) + origin,
                        Vec2(offset, z).rotate(angle) + origin,
                    )
                )
                offset += deltaV
                v
            }
        }


        val next: List<Pair<Int, Int>> = listOf(
            (-1 to 0),
            (0 to -1),
            (1 to 0),
            (0 to 1),
        )

        private val tan = 0.552284749831
        fun bezierQuartir(v: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
            val p1 = next[g1 % 4]
            val p2 = next[g2 % 4]
            return FigureBezierList(
                Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
                Vec2(
                    v.x - p1.first * smoothSize * (1 - tan),
                    v.y - p1.second * smoothSize * (1 - tan)
                ),
                Vec2(
                    v.x + p2.first * smoothSize * (1 - tan),
                    v.y + p2.second * smoothSize * (1 - tan)
                ),
                Vec2(v.x + p2.first * smoothSize, v.y + p2.second * smoothSize)
            )
        }

        fun bezierLine(v: Vec2, v2: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
            val p1 = next[g1 % 4]
            val p2 = next[g2 % 4]
            return FigureBezierList(
                Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
                Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
                Vec2(v2.x + p2.first * smoothSize, v2.y + p2.second * smoothSize),
                Vec2(v2.x + p2.first * smoothSize, v2.y + p2.second * smoothSize)
            )
        }

        fun bezierLine(
            v: Vec2,
            v2: Vec2,
            smoothSizeStart: Double,
            smoothSizeEnd: Double,
            g1: Int,
            g2: Int
        ): FigureBezierList {
            val p1 = next[g1 % 4]
            val p2 = next[g2 % 4]
            return FigureBezierList(
                Vec2(v.x - p1.first * smoothSizeStart, v.y - p1.second * smoothSizeStart),
                Vec2(v.x - p1.first * smoothSizeStart, v.y - p1.second * smoothSizeStart),
                Vec2(v2.x + p2.first * smoothSizeEnd, v2.y + p2.second * smoothSizeEnd),
                Vec2(v2.x + p2.first * smoothSizeEnd, v2.y + p2.second * smoothSizeEnd)
            )
        }

        fun rectangle(
            left: Double,
            top: Double,
            right: Double,
            bottom: Double,
            enableSmooth: Boolean,
            smoothSize: Double,
        ): IFigure {
            if (enableSmooth) {
                val lt = Vec2(left, top);
                val rt = Vec2(right, top);
                val lb = Vec2(left, bottom);
                val rb = Vec2(right, bottom);

                val bz = FigureBezierList.simple(
                    listOf(
                        bezierQuartir(lt, smoothSize, 1, 2),
                        bezierLine(lt, rt, smoothSize, 0, 0),
                        bezierQuartir(rt, smoothSize, 2, 3),
                        bezierLine(rt, rb, smoothSize, 1, 1),
                        bezierQuartir(rb, smoothSize, 3, 0),
                        bezierLine(rb, lb, smoothSize, 2, 2),
                        bezierQuartir(lb, smoothSize, 0, 1),
                        bezierLine(lb, lt, smoothSize, 3, 3),
                    )
                )

                return bz
            } else {
                val bz = FigurePolyline(
                    listOf(
                        Vec2(left, top),
                        Vec2(right, top),
                        Vec2(right, bottom),
                        Vec2(left, bottom),
                    ),
                    true
                )
                return bz
            }
        }

        fun zigFigure(
            hz: Double,
            bz1x: Double,
            bz2x: Double,
            bz1y: Double,
            bz2y: Double,
            board: Double,
            zigWidth: Double,
        ): IFigure {
            return FigureList(
                listOf(
                    FigureLine(
                        Vec2(0.0, 0.0),
                        Vec2(0.0, hz)
                    ),
                    FigureBezier(
                        listOf(
                            Vec2(0.0, hz),
                            Vec2(-bz1x, hz + bz1y),
                            Vec2(-bz2x, board + bz2y),
                            Vec2(0.0, board),
                        )
                    ),
                    FigureLine(
                        Vec2(0.0, board),
                        Vec2(zigWidth, board),

                        ),
                    FigureBezier(
                        listOf(
                            Vec2(zigWidth, hz),
                            Vec2(zigWidth + bz1x, hz + bz1y),
                            Vec2(zigWidth + bz2x, board + bz2y),
                            Vec2(zigWidth, board),
                        )
                    ),
                    FigureLine(
                        Vec2(zigWidth, hz),
                        Vec2(zigWidth, 0.0)
                    )
                )
            )
        }

        fun rectangle(center: Vec2, width: Double, height: Double) :IFigure{
            val c2 = center
            val angle = 0.0
            val width2 = width/2.0
            val height2 = height/2.0
            val points = listOf<Vec2>(
                c2 + Vec2(-width2, -height2).rotate(angle),
                c2 + Vec2(-width2, height2).rotate(angle),
                c2 + Vec2(width2, height2).rotate(angle),
                c2 + Vec2(width2, -height2).rotate(angle),
                c2 + Vec2(-width2, -height2).rotate(angle),
            )

            return FigurePolyline(points)
        }
    }
}