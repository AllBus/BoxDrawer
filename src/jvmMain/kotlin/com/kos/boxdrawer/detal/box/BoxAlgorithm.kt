package com.kos.boxdrawer.detal.box

import androidx.compose.ui.text.AnnotatedString
import figure.IFigure
import turtoise.*
import turtoise.TortoiseParser.asDouble

class BoxAlgorithm(
    val boxInfo: BoxInfo,
    val zigW: ZigzagInfo,
    val zigH: ZigzagInfo,
    val zigWe: ZigzagInfo,
    val wald: WaldParam,
    val polki : List<Polka>,
    val alternative:Boolean,
) : TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String> = listOf("box")

    override fun draw(name: String, ds: DrawerSettings, runner: TortoiseRunner): IFigure {

        val polkiSort = CalculatePolka.calculatePolki(polki, boxInfo.width, boxInfo.weight, ds.boardWeight)

        return BoxCad.box(
            startPoint = runner.state.xy,
            boxInfo = boxInfo,
            zigW = zigW,
            zigH = zigH,
            zigWe = zigWe,
            drawerSettings = ds,
            waldParams = wald,
            polki = polkiSort,
            outVariant = BoxCad.EOutVariant.ALTERNATIVE
        )
    }

    companion object {

        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Рисование коробки"))
            sb.appendLine()
            sb.append(
                TortoiseParser.helpName(
                    "",
                    "w h we (polka (( d o s e (h*) )*) (zig (w d h e)*5) ",
                    //"w h we (zW zWd) (zH zHd) (zWe zWed) (pazTop, pazBottom, toff, boff, hoff, hwe)",
                    ""
                )
            )
            sb.appendLine()

            return sb.toAnnotatedString()
        }
        fun parseBox(a: String, useAlgorithms: Array<String>?): TortoiseAlgorithm {
            val items: TurtoiseParserStackBlock = TortoiseParser.parseSkobki(a)

            items.blocks.forEach{ b ->
                println("${b.argument} ${b.size} ${b.name}")
            }
            println("---")

            val (polki, other) = items.blocks.partition { it.name.startsWith("p") }
            val (zig, other2) = other.partition { it.name.startsWith("z") }


            val polkiList =  polki.flatMap {
                it.blocks.map { it.line }
            }.flatMap { line ->
                CalculatePolka.createPolki(line)
            }

            return BoxAlgorithm(
                boxInfo =    BoxInfo(
                    width = asDouble(items.get(0)),
                    weight = asDouble(items.get(1)),
                    height = asDouble(items.get(2)),
                ),
                zigW = zigInfo(zig.getOrNull(0)),
                zigH = zigInfo(zig.getOrNull(1)),
                zigWe = zigInfo(zig.getOrNull(2)),
                wald = waldInfo(other2.getOrNull(0)),
                polki = polkiList,
                alternative = true
            )
        }

        fun zigInfo(block: TurtoiseParserStackItem?
        ):ZigzagInfo{
            return if (block == null)
                ZigzagInfo(width = 15.0, delta = 35.0)
            else
                ZigzagInfo(
                    width = asDouble(block.get(0), 15.0),
                    delta = asDouble(block.get(1), 35.0),
                    height = asDouble(block.get(2), 0.0),
                )
        }

        fun parsePazForm(text:String?, defaultValue: PazForm):PazForm{
            return when (text?.lowercase()){
                "hole", "h" -> PazForm.Hole
                "zig", "zag", "zigzag", "paz", "z" -> PazForm.Paz
                "back", "b" -> PazForm.BackPaz
                "flat", "f" -> PazForm.Flat
                "", null -> defaultValue
                else -> PazForm.None
            }
        }

        fun waldInfo(block: TurtoiseParserStackItem?): WaldParam {
            if (block == null)
                return WaldParam(
                    topOffset = 0.0,
                    bottomOffset = 0.0,
                    holeBottomOffset = 2.0,
                    holeTopOffset = 2.0,
                    holeWeight = 0.0,
                    topForm = PazForm.None,
                    bottomForm = PazForm.Paz
                )
            else
                return WaldParam(
                    bottomForm = parsePazForm(block.get(0), PazForm.Paz),
                    topForm = parsePazForm(block.get(1),PazForm.None),
                    bottomOffset = asDouble(block.get(2)),
                    topOffset = asDouble(block.get(3)),
                    holeBottomOffset = asDouble(block.get(4)),
                    holeTopOffset = asDouble(block.get(5)),
                    holeWeight = asDouble(block.get(6)),
                )
        }
    }


}