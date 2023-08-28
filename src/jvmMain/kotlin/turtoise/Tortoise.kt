package turtoise

import figure.*
import vectors.Vec2
import kotlin.math.min
import kotlin.math.truncate

class Tortoise {

    fun draw(program: TortoiseProgram, startPoint: Vec2, ds: DrawerSettings, memory: TortoiseMemory): FigureList {

        val commands = program.commands.flatMap { a -> a.names.flatMap { n -> a.commands(n, ds) } }
        val state = TortoiseState()
        state.moveTo(startPoint)
        return FigureList(
            commands.flatMap { c -> draw(c, state, ds, 10, memory) }
        )
    }

    fun draw(commands: TortoiseBlock, startPoint: Vec2, ds: DrawerSettings, memory: TortoiseMemory): FigureList {
        val state = TortoiseState()
        state.moveTo(startPoint);
        return FigureList(draw(commands, state, ds, 10, memory))
    }

    fun draw(
        commands: TortoiseBlock,
        state: TortoiseState,
        ds: DrawerSettings,
        lineIndex: Int,
        memory: TortoiseMemory
    ): List<IFigure> {
        val res = mutableListOf<IFigure>()
        var result = mutableListOf<Vec2>()
        val le = commands.size
        var i = 0
        var stack: TortoiseStack? = null
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
                TortoiseCommand.TURTOISE_SPLIT -> {
                    saveLine()
                }

                TortoiseCommand.TURTOISE_CLEAR -> state.clear()
                TortoiseCommand.TURTOISE_CIRCLE -> {
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
                                    segmentStart = com.take(d + 0, 0.0, memory),
                                    segmentEnd = com.take(d + 1, 0.0, memory),
                                )
                            )
                        }
                    }
                }

                TortoiseCommand.TURTOISE_ELLIPSE -> {
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
                                        segmentStart = com.take(d , 0.0, memory),
                                        segmentEnd = com.take(d + 1, 0.0, memory),
                                    )
                                )
                            }
                        }
                }

                TortoiseCommand.TURTOISE_MOVE -> {
                    state.move(com[0, memory], com[1, memory])
                }

                TortoiseCommand.TURTOISE_HORIZONTAL -> {
                    state.x += com.value(memory)
                    result.add(state.xy)
                }

                TortoiseCommand.TURTOISE_VERTICAL -> {
                    state.y += com.value(memory)
                    result.add(state.xy)
                }

                TortoiseCommand.TURTOISE_ANGLE -> {
                    state.a = com.value(memory)
                }

                TortoiseCommand.TURTOISE_ANGLE_ADD -> {
                    state.a += com.value(memory)
                }

                TortoiseCommand.TURTOISE_LINE -> {
                    state.move(com.value(memory))
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
                    state.move(com.value(memory))
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
                    if (result.size > 1) {
                        result.add(result.first())
                        saveLine()
                    }
                }

                TortoiseCommand.TURTOISE_RECTANGLE -> {
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

                TortoiseCommand.TURTOISE_ZIGZAG -> {
                    if (result.size == 0) {
                        result.add(state.xy)
                    }

                    zigzag(
                        points = result,
                        origin = result.last(),
                        width = com.value(memory),
                        zigzagWidth = com.take(2, state.zigWidth, memory),
                        delta = com.take(1, state.zigDelta, memory),
                        angle = state.angle,
                        param = state.zigParam,
                        boardWeight = com.take(3, ds.boardWeight, memory)
                    );

                    state.moveTo(result.last());

                }

                TortoiseCommand.TURTOISE_BEZIER -> {
                    saveLine()
                    state.move(com[0, memory])
                    val angle = state.angle

                    val points = mutableListOf<Vec2>()
                    points.add(state.xy)
                    for (d in 1..com.size - 6 step 6) {
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

                        state.moveTo(xy)

                        points.add(
                            Vec2(
                                com[d + 3, memory],
                                com[d + 4, memory]
                            ).rotate(angle) + state.xy
                        )
                        points.add(xy)
                    }

                    if (points.size > 1) {
                        res.add(FigureBezier(points.toList()))
                    }
                }
                TortoiseCommand.TURTOISE_SPLINE -> {
                    saveLine()
                    state.move(com[0, memory])
                    val angle = state.angle

                    val points = mutableListOf<Vec2>()
                    points.add(state.xy)
                    for (d in 1..com.size - 2 step 2) {
                        val xy =  Vec2(
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


                else -> {

                }

            } // end when


            i++
        }//end while
        saveLine()
        return res

    }

    fun zigzag(
        points: MutableList<Vec2>,
        origin: Vec2,
        width: Double,
        zigzagWidth: Double,
        delta: Double,
        angle: Double,
        param: DrawingParam,
        boardWeight: Double
    ) {
        var zigzagWidthV = zigzagWidth
        var deltaV = delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV > deltaV) {
            zigzagWidthV = deltaV - boardWeight * 2
            if (zigzagWidthV < boardWeight) return
        }
        val bot = if (param.back) -1 else 1
        val distance = deltaV - zigzagWidthV
        val count = truncate(width / deltaV).toInt()

        var offset: Double = (width - deltaV * count + distance) / 2 * bot

        val weight = if (param.reverse) -boardWeight else boardWeight

        deltaV *= bot.toDouble()
        val zw = zigzagWidthV * bot
        if (count > 10000) return

        offset += 0.0
        val z = 0.0
        for (i in 0 until count) {
            points.add(Vec2(offset, z).rotate(angle) + origin)
            points.add(Vec2(offset, z + weight).rotate(angle) + origin)
            points.add(Vec2(offset + zw, z + weight).rotate(angle) + origin)
            points.add(Vec2(offset + zw, z).rotate(angle) + origin)
            offset += deltaV
        }
        points.add(Vec2(width * bot, z).rotate(angle) + origin)
    }


}