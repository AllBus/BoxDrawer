package com.kos.boxdrawer.detal.box

import androidx.compose.ui.text.AnnotatedString
import figure.IFigure
import turtoise.*

class BoxAlgorithm(
    val boxInfo: BoxInfo,
    val zigW: ZigzagInfo,
    val zigH: ZigzagInfo,
    val zigWe: ZigzagInfo,
    val wald: WaldParam,
) : TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String> = listOf("box")

    override fun draw(name: String, ds: DrawerSettings, runner: TortoiseRunner): IFigure {
        return BoxCad.box(
            runner.state.xy,
            boxInfo,
            zigW,
            zigH,
            zigWe,
            ds,
            wald,
            PolkaSort(),
            false
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
            val items: TurtoiseParserStackItem = TortoiseParser.parseSkobki(a)

            fun asDouble(text:String?): Double{
                return text?.toDoubleOrNull()?:0.0
            }

            fun asDouble(text:String?, defaultValue: Double): Double{
                return text?.toDoubleOrNull()?:defaultValue
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

            fun waldInfo(block: TurtoiseParserStackItem?
            ): WaldParam {
                if (block == null)
                    return WaldParam(
                        topOffset = 0.0,
                        bottomOffset = 0.0,
                        holeOffset = 0.0,
                        holeWeight = 0.0,
                        topForm = PazForm.None,
                        bottomForm = PazForm.Paz
                    )
                else
                    return WaldParam(
                        topOffset = asDouble(block.get(2)),
                        bottomOffset = asDouble(block.get(3)),
                        holeOffset = asDouble(block.get(4)),
                        holeWeight = asDouble(block.get(5)),
                        topForm = parsePazForm(block.get(0),PazForm.None),
                        bottomForm = parsePazForm(block.get(1), PazForm.Paz)
                    )
            }

            return BoxAlgorithm(
                boxInfo =    BoxInfo(
                    width = asDouble(items.get(0)),
                    height = asDouble(items.get(1)),
                    weight = asDouble(items.get(2)),
                ),
                zigW = zigInfo(items.blocks.getOrNull(0)),
                zigH = zigInfo(items.blocks.getOrNull(1)),
                zigWe = zigInfo(items.blocks.getOrNull(2)),
                wald = waldInfo(items.blocks.getOrNull(3)),
            )
        }

    }
}