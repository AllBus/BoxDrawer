package turtoise.help

import androidx.compose.ui.text.AnnotatedString

class HelpInfoCommand(
    val name:String,
    val text: AnnotatedString
) {
}

class HelpData(
    val argument: String,
    val description:String,
    val params: List<HelpDataParam> = emptyList()
)

class HelpDataParam(
    val name:String,
    val description:String,
)