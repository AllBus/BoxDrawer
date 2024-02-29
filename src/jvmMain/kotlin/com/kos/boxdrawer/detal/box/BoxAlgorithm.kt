package com.kos.boxdrawer.detal.box

import androidx.compose.ui.text.AnnotatedString
import com.kos.figure.IFigure
import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseParser
import turtoise.TortoiseParser.asDouble
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem
import turtoise.ZigzagInfo
import turtoise.memory.TortoiseMemory

class BoxAlgorithm(
    val boxInfo: BoxInfo,
    val zigs: ZigInfoList,
    val wald: WaldParam,
    val polki: List<Polka>,
    val outVariant: BoxCad.EOutVariant,
    val polkiIn: Boolean,
) : TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String> = listOf("box")

    override fun draw(
        name: String,
        ds: DrawerSettings,
        state: TortoiseState,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
        maxStackSize: Int
    ): IFigure {

        val bwi = boxInfo.width - ds.boardWeight * 2
        val bwe = boxInfo.weight - ds.boardWeight * 2
        val upWidth = if (polkiIn) ds.boardWeight else 0.0

        val polkiSort = CalculatePolka.calculatePolki(polki, bwi, bwe, upWidth)

        polkiSort.zigPolkaH = zigs.zigPolka
        polkiSort.zigPolkaPol = zigs.zigPolkaPol

        return BoxCad.box(
            startPoint = state.xy,
            boxInfo = boxInfo,
            zigW = zigs.zigW,
            zigH = zigs.zigH,
            zigWe = zigs.zigWe,
            drawerSettings = ds,
            waldParams = wald,
            polki = polkiSort,
            outVariant = outVariant
        )
    }

    fun commandLine(): String {
        val p = polki.joinToString(" ", "(p ", ")") { po ->
            po.commandLine()
        }

        return "box@ ${boxInfo.commandLine()} ${if (polkiIn) 1 else 0} ${outVariantName(outVariant)} ${zigs.commandLine()}" +
                "(w ${wald.commandLine()}) " + (if (polki.isNotEmpty()) p else "")
    }

    companion object {

        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Рисование коробки"))
            sb.appendLine()
            sb.append(
                TortoiseParser.helpName(
                    "",
                    "w h we (polka (( d o s e (h*) (as ac (a*))*)*) (zig (w d h e)*5)",
                    ""
                )
            )
            sb.appendLine()

            return sb.toAnnotatedString()
        }

        fun parseBox(
            items: TurtoiseParserStackItem,
            useAlgorithms: Array<String>?
        ): TortoiseAlgorithm {

            val (polki, other) = items.blocks.partition { it.name.startsWith("p") }
            val (zig, other2) = other.partition { it.name.startsWith("z") }
            val waldBlock = other2.find { it.name.startsWith("w") }

            val polkiList = polki.flatMap {
                it.blocks.map { line -> CalculatePolka.polka(line) }
            }

            val zigs = ZigInfoList(
                zigW = zigInfo(zig.getOrNull(0)),
                zigWe = zigInfo(zig.getOrNull(1)),
                zigH = zigInfo(zig.getOrNull(2)),
                zigPolka = zigInfo(zig.getOrNull(3)),
                zigPolkaPol = zigInfo(zig.getOrNull(4)),
            )

            val ind = if (items.name.contains("@"))
                1 else 0

            return BoxAlgorithm(
                boxInfo = BoxInfo(
                    width = asDouble(items.get(0 + ind)),
                    weight = asDouble(items.get(1 + ind)),
                    height = asDouble(items.get(2 + ind)),
                ),
                zigs = zigs,
                wald = waldInfo(waldBlock),
                polki = polkiList,
                outVariant = parseOutVariant(items.get(4 + ind)),
                polkiIn = asDouble(items.get(3 + ind)) > 0.1,
            )
        }

        fun zigInfo(
            block: TurtoiseParserStackItem?
        ): ZigzagInfo {
            return if (block == null)
                ZigzagInfo(width = 15.0, delta = 35.0)
            else
                ZigzagInfo(
                    width = asDouble(block.get(1), 15.0),
                    delta = asDouble(block.get(2), 35.0),
                    height = asDouble(block.get(3), 0.0),
                    enable = block.get(4)?.lowercase()?.startsWith("f") != true
                )
        }

        fun parseOutVariant(text: String?): BoxCad.EOutVariant {
            return when (text?.lowercase()) {
                "a" -> BoxCad.EOutVariant.ALTERNATIVE
                "c" -> BoxCad.EOutVariant.COLUMN
                "v" -> BoxCad.EOutVariant.VOLUME
                else -> BoxCad.EOutVariant.ALTERNATIVE
            }
        }

        fun outVariantName(variant: BoxCad.EOutVariant): String {
            return when (variant) {
                BoxCad.EOutVariant.ALTERNATIVE -> "a"
                BoxCad.EOutVariant.COLUMN -> "c"
                BoxCad.EOutVariant.VOLUME -> "v"
            }
        }

        fun parsePazForm(text: String?, defaultValue: PazForm): PazForm {
            return when (text?.lowercase()) {
                "hole", "h" -> PazForm.Hole
                "zig", "zag", "zigzag", "paz", "z" -> PazForm.Paz
                "back", "b" -> PazForm.BackPaz
                "flat", "f" -> PazForm.Flat
                "p", "paper" -> PazForm.Paper
                "o", "out", "outside" -> PazForm.Outside
                "", null -> defaultValue
                else -> PazForm.None
            }
        }

        fun pazName(paz: PazForm): String {
            return when (paz) {
                PazForm.None -> "n"
                PazForm.Paz -> "z"
                PazForm.Hole -> "h"
                PazForm.BackPaz -> "b"
                PazForm.Flat -> "f"
                PazForm.Paper -> "p"
                PazForm.Outside -> "o"
            }
        }

        fun waldInfo(block: TurtoiseParserStackItem?): WaldParam {
            if (block is TurtoiseParserStackBlock)
                return WaldParam.from(block)
            else
                return WaldParam(
                    topOffset = 0.0,
                    bottomOffset = 0.0,
                    holeBottomOffset = 2.0,
                    holeTopOffset = 2.0,
                    holeWeight = 0.0,
                    topForm = PazForm.None,
                    bottomForm = PazForm.Paz,
                    edgeForm = PazForm.Paz,
                    bottomRoundRadius = 0.0,
                    topRoundRadius = 0.0,
                    edgeTopRoundRadius = 0.0,
                    holeEdgeOffset = 0.0,
                    zigFigure = null,
                    zagFigure = null,
                )
        }
    }
}

data class ZigInfoList(
    val zigW: ZigzagInfo,
    val zigWe: ZigzagInfo,
    val zigH: ZigzagInfo,
    val zigPolka: ZigzagInfo,
    val zigPolkaPol: ZigzagInfo,
) {
    fun commandLine(): String {
        return "(z ${zigW.commandLine()}) " +
                "(z ${zigWe.commandLine()}) " +
                "(z ${zigH.commandLine()}) " +
                "(z ${zigPolka.commandLine()}) " +
                "(z ${zigPolkaPol.commandLine()})"
    }
}