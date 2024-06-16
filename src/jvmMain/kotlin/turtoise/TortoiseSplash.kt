package turtoise

import com.jsevy.jdxf.DXFColor
import com.kos.boxdrawer.detal.soft.SoftRez
import com.kos.figure.FigureCircle
import com.kos.figure.FigureLine
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.FigureText
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureWithPosition
import com.kos.figure.composition.PositionOnFigure
import org.jetbrains.skia.Color
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParserStackBlock
import vectors.Vec2
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.sign


abstract class TortoiseSplash : TortoiseBase() {

    protected var printFormat = DecimalFormat("0.####")


    protected fun variablesSplash(
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
                        collectPaths(f).zip(variables) { p, v ->
                            memory.assign(v.argument, p.pathLength())
                        }
                    }
                }
            }

            "pos",
            "position" -> {
                val variables = com.takeBlock(1)?.inner.orEmpty()
                variables.getOrNull(0)?.let { v ->
                    memory.assign(v.argument, builder.state.x)
                }
                variables.getOrNull(1)?.let { v ->
                    memory.assign(v.argument, builder.state.y)
                }
                variables.getOrNull(2)?.let { v ->
                    memory.assign(v.argument, builder.state.angleInDegrees)
                }
            }

            "board" -> {
                val variables = com.takeBlock(1)?.inner.orEmpty()
                variables.firstOrNull()?.let { v ->
                    memory.assign(v.argument, ds.boardWeight)
                }
            }

            "print" -> {
                /* [text] | (variable) */
                val text = (1 until com.size).mapNotNull { i -> com.takeBlock(i) }.map { block ->
                    if (block is TortoiseParserStackBlock && block.skobka != '(') {
                        block.innerLine
                    } else {
                        printFormat.format(memory.value(block.argument, 0.0))
                    }
                }.joinToString(" ")
                builder.addProduct(FigureText(text))
            }

            "printcoord",
            "printc",
            "coord",
            "coordinate",
            "print_coord",
            "print_coordinate" -> {
                builder.addProduct(FigureText(builder.xy.toString()))
            }

            else -> {}
        }
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

            "arc" -> {
                /* (radius pointStart pointEnd)+ */
                val cs = com.size
                val figures = FigureList(
                    (1 until cs).map { i -> com.takeBlock(i) }.map { block ->
                        val r = memory.value(block?.get(0) ?: MemoryKey.ZERO, 0.0)
                        val p = Vec2(memory.value(block?.get(1) ?: MemoryKey.ZERO, 0.0), 0.0)
                        val z = Vec2(memory.value(block?.get(2) ?: MemoryKey.ZERO, 0.0), 0.0)
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
                /* r ax ay */
                builder.saveLine()
                val (rest, aap, aaa) = roundLine(com, memory)

                builder.addProduct(FigureList(rest))
                builder.state.move(aap.x, aap.y)
                builder.state.angleInDegrees = aaa
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
                builder.state.zigParam = builder.state.zigParam.copy(reverse = true)
            }

            "f" -> {
                builder.state.zigParam = builder.state.zigParam.copy(reverse = false)
            }

            "paz" -> {
                /* (Figure) (edge delta le he)* */
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = collectPaths(f)
                        val v = (2 until com.size).mapNotNull { j ->
                            com.takeBlock(j)?.let { item ->
                                val e = memory.value(item.get(0) ?: MemoryKey.ZERO, 0.0)
                                if (item.get(1) == null) {
                                    FigureColor(
                                        Color.GREEN,
                                        DXFColor.getClosestDXFColor(Color.GREEN),
                                        pathAtIndex(paths, e.toInt()).toFigure()
                                    )

                                } else {
                                    val d = memory.value(item.get(1) ?: MemoryKey.ZERO, 0.0)
                                    val zigle = item.get(2)?.let { memory.value(it, 15.0) } ?: 15.0
                                    val zighe =
                                        item.get(3)?.let { memory.value(it, ds.boardWeight) }
                                            ?: ds.boardWeight
                                    positionInPath(paths, e.toInt(), d)?.let { pos ->
                                        FigurePolyline(
                                            listOf(
                                                Vec2(-zigle, 0.0),
                                                Vec2(-zigle, zighe),
                                                Vec2(zigle, zighe),
                                                Vec2(zigle, 0.0),
                                            ).map { pos.point + it.rotate(pos.normal.angle + PI / 2) }
                                        )
                                    }
                                }
                            }
                        }
                        builder.addProduct(FigureList(v + f))
                    }
                }
            }

            "stena" -> {
                /* (Figure) he (edge he)* */
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = collectPaths(f)
                        val (h , we) = com.takeBlock(2)?.let { item ->
                            val h = (item.get(0)?.let{ im -> memory.value(im, 0.0)}?: 10.0)
                            val w = (item.get(1)?.let { im ->
                                memory.value(im, ds.boardWeight)
                            }?: ds.boardWeight)
                            h to w
                        } ?: (10.0 to ds.boardWeight)

                        val heights = (3 until com.size).mapNotNull { j ->
                            com.takeBlock(j)?.let { item ->
                                val e = (item.get(0)?.let { memory.value(it, 0.0) } ?: 0.0).toInt()
                                val h1 = (item.get(1)?.let { memory.value(it, 0.0) } ?: 0.0)
                                e to h1
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

            "o" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = collectPaths(f)
                        if (paths.isNotEmpty()) {
                            (2 until com.size).map { ind ->
                                val h = com.takeBlock(ind)?.let { item ->
                                    valueAt(item, 0, memory, ds.boardWeight)
                                } ?: ds.boardWeight

                                builder.addProduct(
                                    FigureList(
                                        paths.map { p ->
                                            p.duplicationAtNormal(h)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
            "edit", "e" -> {
                /* (figure) (m e x y) (r e+) */
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                         collectPolygons(f).firstOrNull()?.let {
                            path ->
                            val points = path.points.toMutableList()
                            (2 until com.size).mapNotNull { j ->
                                com.takeBlock(j)?.let { item ->
                                    when (item.name.name) {
                                        "m" -> {
                                            val e = valueAt(item, 1, memory).toInt()
                                            val x = valueAt(item, 2, memory)
                                            val y = valueAt(item, 3, memory)
                                            val a = valueAt(item, 4, memory)
                                            if (e>=0 && e< points.size) {
                                                points[e] = points[e]+Vec2(x, y).rotate(a)
                                            }
                                        }
                                        "r" -> {
                                            (1 until item.size).map{ i ->
                                                valueAt(item, i, memory).toInt()
                                            }.sorted().reversed().forEach {ind ->
                                                if (ind>=0 && ind< points.size) {
                                                    points.removeAt(ind)
                                                }
                                            }
                                        }

                                        else -> {}
                                    }
                                }
                            }
                            builder.addProduct(path.create(points.toList()))
                        }
                    }
                }
            }
            "drop" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = collectPaths(f)
                        val edges = (2 until com.size).mapNotNull { j ->
                            com.takeBlock(j)?.let { item ->
                                val e = valueAt(item, 0, memory).toInt()
                                val x = valueAt(item, 1, memory)
                                val wi = valueAt(item, 2, memory, 15.0)
                                val bias = valueAt(item, 3, memory, 0.5)
                                PositionOnFigure(e, x, wi ,bias)
                            }
                        }

                        var currentEdge = 0
                        val fg = paths.mapIndexed{ i, p ->
                            val pe = p.edgeCount()
                           val f = FigureWithPosition(p,
                            edges.filter { it.edge>= currentEdge && it.edge< pe+currentEdge }.map{
                                it.copy(edge= it.edge-currentEdge)
                            },
                               true,)
                            currentEdge+=pe
                            f
                        }
                        builder.addProduct(FigureList(fg))
                    }
                }
            }
            "take" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, ds, maxStackSize, memory, runner)?.let { f ->
                        val paths = collectPaths(f)
                        val edges = (2 until com.size).mapNotNull { j ->
                            com.takeBlock(j)?.let { item ->
                                val e = valueAt(item, 0, memory).toInt()
                                val x = valueAt(item, 1, memory)
                                val wi = valueAt(item, 2, memory, 15.0)
                                val bias = valueAt(item, 3, memory, 0.5)
                                PositionOnFigure(e, x, wi ,bias)
                            }
                        }

                        var currentEdge = 0
                        val fg = paths.mapIndexed{ i, p ->
                            val pe = p.edgeCount()
                            val f = FigureWithPosition(p,
                                edges.filter { it.edge>= currentEdge && it.edge< pe+currentEdge }.map{
                                    it.copy(edge= it.edge-currentEdge)
                                },
                                false
                            )
                            currentEdge+=pe
                            f
                        }
                        builder.addProduct(FigureList(fg))
                    }
                }
            }

            "print" -> {
                /* [text] | (variable) */
                val text = (1 until com.size).mapNotNull { i -> com.takeBlock(i) }.map { block ->
                    if (block is TortoiseParserStackBlock && block.skobka != '(') {
                        block.innerLine
                    } else {
                        printFormat.format( memory.value(block.argument, 0.0))
                    }
                }.joinToString(" ")
                builder.addProduct(FigureText(text))
            }
            "printcoord",
            "printc",
            "coord",
            "coordinate",
            "print_coord",
            "print_coordinate" -> {
                builder.addProduct(FigureText(builder.xy.toString()))
            }

            else -> {
            }
        }
    }

    private fun roundLine(
        com: TortoiseCommand,
        memory: TortoiseMemory
    ): Triple<List<IFigure>, Vec2, Double> {
        /* r ax ay */
        val cr = Vec2.Zero
        val rest = mutableListOf<IFigure>()

        val i = 1
        val r = com[i, memory]
        val ax = com[i + 1, memory]
        val ay = com[i + 2, memory]
        val ll = if (com.size > 4) com[i + 3, memory] else null
        val aa = Vec2(ax, ay)

        val (aap, angle) = if (r > 0) {
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
                p + Vec2.normalize(p, aa) * ll
            }
            if (ap != p) {
                rest += FigureLine(p + cr, cr + ap)
            }

            rest += FigureCircle(cr + cc, r, preda, -alp * 180 / PI)
            Pair(ap , (ap-p).angle*180/ PI)
        } else {
            val ap = if (ll == null) {
                aa
            } else {
                Vec2.normalize(Vec2.Zero, aa) * ll
            }

            if (ap != Vec2.Zero) {
                rest += FigureLine(cr, ap + cr)
            }
            Pair(ap , ap.angle*180/ PI)

        }
        return Triple(rest, aap, angle)
    }
}