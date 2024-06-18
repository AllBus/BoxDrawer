package turtoise

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.figure.FigureText
import turtoise.parser.TortoiseParserStackBlock
import java.text.DecimalFormat


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

            else -> {}
        }
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