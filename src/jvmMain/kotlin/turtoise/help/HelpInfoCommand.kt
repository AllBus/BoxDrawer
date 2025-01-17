package turtoise.help

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

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

@Immutable
class HelpData(
    val argument: String,
    val description: String,
    val params: List<HelpDataParam> = emptyList(),
    val creator : TemplateForm? = null,
)

@Immutable
class HelpDataParam(
    val name: String,
    val description: String,
    val kind: String = FIELD_1,
    val variants : List<String>? = null,
)