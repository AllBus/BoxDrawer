package turtoise.dxf

import androidx.compose.ui.text.AnnotatedString
import turtoise.parser.TortoiseParser
import turtoise.help.HelpInfoCommand
import turtoise.help.SimpleHelpInfo

class DxfHelpInfo : SimpleHelpInfo() {
    override val title: AnnotatedString
        get() = TortoiseParser.helpTitle("Рисование содержимое dxf файла")
    override val commandList: List<HelpInfoCommand>
        get() = listOf(
            TortoiseParser.helpName(
                "dxf@",
                "(path/file.dxf)",
                "Название файла внути скобок"
            )
        )
    override val name: String
        get() = "dxf"
}