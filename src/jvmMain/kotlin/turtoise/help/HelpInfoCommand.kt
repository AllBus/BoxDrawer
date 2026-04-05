package turtoise.help

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import turtoise.parser.TortoiseParser

@Immutable
class HelpInfoCommand(
    val name: String,
    val data: List<HelpData>,
    val description: String = "",
) {

    val text: AnnotatedString by lazy { createText()}

    fun createText():AnnotatedString{
        val sb = AnnotatedString.Builder()
        data.forEach { argument ->
            sb.append(TortoiseParser.helpName(name))
            sb.append(" ")
            sb.append(TortoiseParser.helpArgument(argument.argument))
            sb.append(TortoiseParser.helpDescr(" - " + argument.description))
            sb.appendLine()
            argument.params.forEach { param ->
                sb.append("    ")
                sb.append(TortoiseParser.helpParam(param.name))
                sb.append(" - ")
                sb.append(TortoiseParser.helpDescr(param.description))
                sb.appendLine()
            }
        }
        return sb.toAnnotatedString()
    }
}

