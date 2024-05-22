package turtoise

import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigurePolyline
import com.kos.figure.FigureSpline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.booleans.FigureDiff
import com.kos.figure.composition.booleans.FigureIntersect
import com.kos.figure.composition.booleans.FigureSymDiff
import com.kos.figure.composition.booleans.FigureUnion
import com.kos.figure.matrix.FigureMatrixRotate
import com.kos.figure.matrix.FigureMatrixScale
import com.kos.figure.matrix.FigureMatrixTranslate
import org.kabeja.dxf.DXFColor
import turtoise.memory.TortoiseMemory
import vectors.Vec2
import java.util.Stack
import kotlin.math.min

private const val MAX_REGULAR_POLYGON_EDGES = 500

class Tortoise() : TortoiseBase() {

    override fun draw(
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
                    rectangle(state, com, memory, res)
                }

                TortoiseCommand.TURTOISE_ROUND_RECTANGLE -> {
                    roundrectangle(com, memory, state, res)
                }

                TortoiseCommand.TURTOISE_TRIANGLE -> {
                    triangle(com, memory, state, res)
                }

                TortoiseCommand.TURTOISE_REGULAR_POLYGON -> {
                    val r = com.take(1, 0.0, memory)
                    val count = min(com.take(0, 3.0, memory).toInt(), MAX_REGULAR_POLYGON_EDGES)

                    res.add(
                        FigureCreator.regularPolygon(
                            center = state.xy,
                            count = count,
                            angle = state.angle,
                            radius = r
                        )
                    )
                }

                TortoiseCommand.TURTOISE_FIGURE -> {
                    val s = (0 until com.size).mapNotNull { index ->
                        com.takeBlock(index)
                    }.mapNotNull { block ->
                        figureList(block, ds, state, maxStackSize, memory, runner)
                    }
                    res.addAll(s)
                }

                TortoiseCommand.TURTOISE_IF_FIGURE -> {
                    if (com.value(memory) > 0.5) {
                        val block = com.takeBlock(1)
                        figureList(block, ds, state, maxStackSize, memory, runner)?.let { g ->
                            res.add(
                                g
                            )
                        }
                    }
                }

                TortoiseCommand.TURTOISE_COLOR -> {
                    val color = com.value(memory).toInt()
                    val block = com.takeBlock(1)
                    figureList(block, ds, state, maxStackSize, memory, runner)?.let { g ->
                        res.add(
                            FigureColor(
                                DXFColor.getRgbColor(color),
                                g
                            )
                        )
                    }
                }

                TortoiseCommand.TURTOISE_PATH -> {
                    res.add(figuresOnPath(com, ds, state, maxStackSize, memory, runner))
                }

                TortoiseCommand.TURTOISE_ZIGZAG_FIGURE -> {
                    saveLine()

                    val zigWidth = com.take(2, state.zigWidth, memory)
                    val board = com.take(3, ds.boardWeight, memory)

                    val block = com.takeBlock(0)
                    val f = figureList(block, ds, TortoiseState(), maxStackSize, memory, runner)
                    val zf = f ?: FigureCreator.zigFigure(
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

                    FigureCreator.zigzag(
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

                TortoiseCommand.TURTOISE_UNION -> {
                    val s = polylineFromCommand(com, ds, state, maxStackSize, memory, runner)
                    res.add(
                        FigureUnion(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                    //  res.add(UnionFigure.union(s.flatten()))
                }

                TortoiseCommand.TURTOISE_INTERSECT -> {
                    val s = polylineFromCommand(com, ds, state, maxStackSize, memory, runner)
                    res.add(
                        FigureIntersect(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                }

                TortoiseCommand.TURTOISE_DIFF -> {
                    val s = polylineFromCommand(com, ds, state, maxStackSize, memory, runner)
                    res.add(
                        FigureDiff(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                }

                TortoiseCommand.TURTOISE_SYMDIFF -> {
                    val s = polylineFromCommand(com, ds, state, maxStackSize, memory, runner)
                    res.add(
                        FigureSymDiff(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
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
                    res.add(FigureMatrixRotate(com[0, 0.0, memory], state.xy))
                }

                TortoiseCommand.TURTOISE_MEMORY_ASSIGN -> {
                    com.assign(memory)
                }

                TortoiseCommand.TURTOISE_3D -> {
                    val block = com.takeBlock(2)
                    val f = figureList(block, ds, state, maxStackSize, memory, runner)

                    f?.let { g ->
                        res.add(
                            figure3d(com, memory, g, state)
                        )
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
}