package com.kos.boxdrawer.detal.polka

import androidx.compose.ui.text.AnnotatedString
import turtoise.TortoiseParser
import turtoise.help.HelpInfoCommand
import turtoise.help.IHelpInfo
import turtoise.help.SimpleHelpInfo

class PolkaHelpInfo: SimpleHelpInfo() {
    override val commandList: List<HelpInfoCommand>
        get() = emptyList()

    override val title: AnnotatedString
        get() {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Команды для рисования полки."))
            sb.appendLine()
            sb.append(TortoiseParser.helpArgument("@figure"))
            sb.append(TortoiseParser.helpDescr(" - Рисовать симметричный многоугольник"))
            sb.appendLine()
            sb.append(TortoiseParser.helpArgument("@side"))
            sb.append(TortoiseParser.helpDescr(" - Рисовать стенку под многоугольник figure"))
            sb.appendLine()
            sb.append(TortoiseParser.helpArgument("sH bOff? tOff? (w a ay? (hw hp hh ha)* )*"))
            return sb.toAnnotatedString()
        }

    override val name: String = "polka"


}