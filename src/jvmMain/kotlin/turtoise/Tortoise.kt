package turtoise

import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.booleans.FigureDiff
import com.kos.figure.composition.booleans.FigureIntersect
import com.kos.figure.composition.booleans.FigureSymDiff
import com.kos.figure.composition.booleans.FigureUnion
import com.kos.figure.matrix.FigureMatrixRotate
import com.kos.figure.matrix.FigureMatrixScale
import com.kos.figure.matrix.FigureMatrixTranslate
import com.jsevy.jdxf.DXFColor
import turtoise.memory.TortoiseMemory
import vectors.Vec2
import java.util.Stack
import kotlin.math.min

private const val MAX_REGULAR_POLYGON_EDGES = 500

class Tortoise() : TortoiseSplash() {

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

        val le = commands.size
        var i = 0

        val builder = TortoiseBuilder(state)

        /** стак вызовов циклов */
        var stack: TortoiseStack? = null
        val stateStack = Stack<TortoiseState>()
        var cancel = false



        while (i < le && !cancel) {
            val com = commands[i]

            when (com.command) {
                TortoiseCommand.TURTOISE_CLEAR -> state.clear()
                TortoiseCommand.TURTOISE_CIRCLE -> circle(com, memory, builder)
                TortoiseCommand.TURTOISE_ELLIPSE -> ellipse(com, memory, builder)

                TortoiseCommand.TURTOISE_MOVE -> {
                    state.move(com[0, memory], com[1, memory])
                    if (com.size>=3) {
                        state.a += com[2, memory]
                        if (com.size>3) {
                            state.move(com[3, memory], com[4, memory])
                        }
                    }
                }

                TortoiseCommand.TURTOISE_HORIZONTAL -> {
                    builder.startPoint()
                    state.x += com.value(memory)
                    builder.add(state.xy)
                }

                TortoiseCommand.TURTOISE_VERTICAL -> {
                    builder.startPoint()
                    state.y += com.value(memory)
                    builder.add(state.xy)
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
                    if (v != 0.0) {
                        builder.startPoint()
                    }
                    state.move(v)
                    builder.add(state.xy)
                    for (d in 1 until com.size) {
                        if (d % 2 == 0) {
                            state.move(com[d, memory])
                        } else {
                            state.move90(com[d, memory])
                        }
                        builder.add(state.xy)
                    }
                }

                TortoiseCommand.TURTOISE_LINE_WITH_ANGLE -> {
                    val v = com.value(memory)
                    if (v != 0.0) {
                        builder.startPoint()
                    }
                    state.move(v)
                    builder.add(state.xy)
                    val currentAngle = state.a;
                    for (d in 1 until com.size step 2) {
                        val a2 = com[d, memory]
                        val di = com[d + 1, memory]
                        state.a = currentAngle + a2;
                        state.move(di);
                        builder.add(state.xy)
                    }
                    state.a = currentAngle;
                }

                TortoiseCommand.TURTOISE_LINE_PERPENDICULAR -> {
                    state.move90(com.value(memory))
                    builder.add(state.xy)
                }

                TortoiseCommand.TURTOISE_CLOSE -> {
                    builder.closeLine()
                }

                TortoiseCommand.TURTOISE_SPLIT -> {
                    builder.saveLine()
                }

                TortoiseCommand.TURTOISE_RECTANGLE -> {
                    rectangleLine(builder, com, memory)
                }

                TortoiseCommand.TURTOISE_ROUND_RECTANGLE -> {
                    roundrectangle(builder, com, memory)
                }

                TortoiseCommand.TURTOISE_TRIANGLE -> {
                    triangle(builder, com, memory)
                }

                TortoiseCommand.TURTOISE_REGULAR_POLYGON -> {
                    val r = com.take(1, 0.0, memory)
                    val count = min(com.take(0, 3.0, memory).toInt(), MAX_REGULAR_POLYGON_EDGES)

                    builder.add(
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
                        figureList(block, ds, maxStackSize, memory, runner)
                    }
                    if (s.isNotEmpty()) {
                        builder.addProduct(
                            if (s.size == 1)
                                s[0]
                            else
                                FigureList(s)
                        )
                    }
                }

                TortoiseCommand.TURTOISE_IF_FIGURE -> {
                    if (com.value(memory) > 0.5) {
                        val block = com.takeBlock(1)
                        figureList(block, ds, maxStackSize, memory, runner)?.let { g ->
                            builder.addProduct(g)
                        }
                    }
                }

                TortoiseCommand.TURTOISE_GROUP -> {
                    builder.addProduct(figureGroups(com, ds, maxStackSize, memory, runner))
                }

                TortoiseCommand.TURTOISE_COLOR -> {
                    val color = com.value(memory).toInt()
                    val block = com.takeBlock(1)
                    figureList(block, ds, maxStackSize, memory, runner)?.let { g ->
                        builder.addProduct(
                            FigureColor(
                                color = DXFColor.getRgbColor(color),
                                dxfColor = color,
                                figure = g,
                            )
                        )
                    }
                }

                TortoiseCommand.TURTOISE_PATH -> {
                    builder.addProduct(
                        figuresOnPath(com, ds, maxStackSize, memory, runner)
                    )
                }

                TortoiseCommand.TURTOISE_SPLASH -> {
                    figuresSplash(
                        builder = builder,
                        com = com,
                        ds = ds,
                        maxStackSize = maxStackSize,
                        memory = memory,
                        runner = runner,
                    )
//                    if (sp !is FigureEmpty) {
//                        res.add(
//                            product(
//                                sp,
//                                state
//                            )
//                        )
//                    }
                }


                TortoiseCommand.TURTOISE_ZIGZAG_FIGURE -> {
                    builder.saveLine()

                    val zigWidth = com.take(2, state.zigWidth, memory)
                    val board = com.take(3, ds.boardWeight, memory)

                    val block = com.takeBlock(0)
                    val f = figureList(block, ds, maxStackSize, memory, runner)
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
                    builder.add(
                        ZigConstructor.zigZag(
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
                    )
                    state.move(width)

                }

                TortoiseCommand.TURTOISE_ZIGZAG -> {
                    builder.startPoint()

                    FigureCreator.zigzag(
                        points = builder.result,
                        origin = builder.result.last(),
                        width = com.value(memory),
                        zig = ZigzagInfo(
                            com.take(2, state.zigWidth, memory),
                            com.take(1, state.zigDelta, memory),
                        ),
                        angle = state.angle,
                        param = state.zigParam,
                        boardWeight = com.take(3, ds.boardWeight, memory)
                    )

                    state.moveTo(builder.result.last());

                }

                TortoiseCommand.TURTOISE_BEZIER -> {
                    builder.saveLine()

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
                        builder.add(FigureBezier(points.toList()))
                    }
                }

                TortoiseCommand.TURTOISE_POLYLINE -> {
                    builder.saveLine()
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
                        builder.add(FigurePolyline(points.toList()))
                        state.moveTo(points.last())
                    }
                }

                TortoiseCommand.TURTOISE_UNION -> {
                    val s = polylineFromCommand(com, ds, maxStackSize, memory, runner)
                    builder.addProduct(
                        FigureUnion(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                    //  res.add(UnionFigure.union(s.flatten()))
                }

                TortoiseCommand.TURTOISE_INTERSECT -> {
                    val s = polylineFromCommand(com, ds, maxStackSize, memory, runner)
                    builder.addProduct(
                        FigureIntersect(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                }

                TortoiseCommand.TURTOISE_DIFF -> {
                    val s = polylineFromCommand(com, ds, maxStackSize, memory, runner)
                    builder.addProduct(
                        FigureDiff(
                            s.firstOrNull() ?: Figure.Empty,
                            s.getOrNull(1) ?: Figure.Empty,
                            ds.appoximationSize
                        )
                    )
                }

                TortoiseCommand.TURTOISE_SYMDIFF -> {
                    val s = polylineFromCommand(com, ds, maxStackSize, memory, runner)
                    builder.addProduct(
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
                    builder.add(FigureMatrixTranslate(com[0, memory], com[1, memory]))
                }

                TortoiseCommand.TURTOISE_MATRIX_SCALE -> {
                    builder.add(FigureMatrixScale(com[0, 1.0, memory], com[1, 1.0, memory]))
                }

                TortoiseCommand.TURTOISE_MATRIX_ROTATE -> {
                    builder.add(FigureMatrixRotate(com[0, 0.0, memory], state.xy))
                }

                TortoiseCommand.TURTOISE_MEMORY_ASSIGN -> {
                    com.assign(memory)
                }

                TortoiseCommand.TURTOISE_3D -> {
                    val block = com.takeBlock(2)
                    val f = figureList(block, ds, maxStackSize, memory, runner)

                    f?.let { g ->
                        builder.addProduct(
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
        builder.saveLine()
        return builder.build()
    }
}