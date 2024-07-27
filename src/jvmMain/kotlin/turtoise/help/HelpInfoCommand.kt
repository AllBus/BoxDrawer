package turtoise.help

import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import turtoise.parser.TortoiseParser

class HelpInfoCommand(
    val name: String,
    val data: List<HelpData>,
    val description: String = ""
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

class HelpData(
    val argument: String,
    val description: String,
    val params: List<HelpDataParam> = emptyList()
)

class HelpDataParam(
    val name: String,
    val description: String,
    val kind: String = FIELD_1
)