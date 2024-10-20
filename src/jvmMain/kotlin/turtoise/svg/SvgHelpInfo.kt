package turtoise.svg

import androidx.compose.ui.text.AnnotatedString
import turtoise.help.HelpInfoCommand
import turtoise.help.SimpleHelpInfo
import turtoise.parser.TortoiseParser

class SvgHelpInfo : SimpleHelpInfo() {
    override val title: AnnotatedString
        get() = TortoiseParser.helpTitle("Рисование пути по командам svg")
    override val commandList: List<HelpInfoCommand>
        get() = listOf(
            TortoiseParser.helpName(
                "svg@",
                "(commands)*",
                "Команды пути svg"
            )
        )
    override val name: String
        get() = "svg"
}