package turtoise

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.figure.FigureText
import turtoise.parser.TortoiseParserStackBlock
import vectors.Vec2
import java.text.DecimalFormat
import kotlin.math.abs


abstract class TortoiseSplash : TortoiseBase() {

    protected var printFormat = DecimalFormat("0.####")
    val splashMap: Map<String, ISplashDetail> = SplashMap.splashes

    protected fun variablesSplash(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    ) {
        val command = com.takeBlock(0)?.name?.name
        val memory = figureExtractor.memory
        when (command) {
            "length" -> {
                com.takeBlock(1)?.let { block ->
                    figureList(block, figureExtractor)?.let { f ->
                        val variables = com.takeBlock(2)?.inner.orEmpty()
                        figureExtractor.collectPaths(f).zip(variables) { p, v ->
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
                    memory.assign(v.argument, figureExtractor.ds.boardWeight)
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

            "r" -> {
                builder.state.zigParam = builder.state.zigParam.copy(reverse = true)
            }

            "f" -> {
                builder.state.zigParam = builder.state.zigParam.copy(reverse = false)
            }
            "l", "lv", "lh" -> {
                val variables = com.takeBlock(1)?.inner.orEmpty()

                val pos = intersectPosition(com, figureExtractor, builder)

                variables.getOrNull(0)?.let { v ->
                    memory.assign(v.argument, pos?.x?:0.0)
                }
                variables.getOrNull(1)?.let { v ->
                    memory.assign(v.argument, pos?.y?:0.0)
                }
            }

            else -> {}
        }
    }

    private fun intersectPosition(
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
        builder: TortoiseBuilder
    ): Vec2? {
        val isVertical = com.takeBlock(0)?.innerLine?.endsWith("v") ?: false
        val cx = com[2, figureExtractor.memory]
        val a = builder.angle
        val sp = builder.xy

        val eps = 0.0001

        val pos = if (com.size <= 2) {
            if (isVertical) {
                if (abs(cx - sp.x) < eps) {
                    null
                } else {
                    Vec2.intersection(
                        sp,
                        sp + Vec2(1.0, 0.0).rotate(a),
                        Vec2(cx, -1000.0),
                        Vec2(cx, 1000.0)
                    )
                }
            } else {
                if (abs(cx - sp.y) < eps) {
                    null
                } else {
                    Vec2.intersection(
                        sp,
                        sp + Vec2(1.0, 0.0).rotate(a),
                        Vec2(-1000.0, cx),
                        Vec2(1000.0, cx)
                    )
                }
            }
        } else {
            val cy = com[3, figureExtractor.memory]
            val ca = Math.toRadians(com[4, figureExtractor.memory])

            val css = Vec2(cx, cy)
            Vec2.intersection(
                sp,
                sp + Vec2(1.0, 0.0).rotate(a),
                css,
                css + Vec2(1.0, 0.0).rotate(ca)
            )

        }
        return pos
    }

    protected fun figuresSplash(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    ) {
        val command = com.takeBlock(0)?.name?.name
        splashMap[command]?.draw(
            builder = builder,
            com = com,
            figureExtractor = figureExtractor
        )
    }


}