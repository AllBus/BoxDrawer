package turtoise.help

import androidx.compose.ui.text.AnnotatedString

class HideHelpInfo : IHelpInfo {
    override fun help(): AnnotatedString = AnnotatedString("")

    override fun help(command: String): AnnotatedString = AnnotatedString("")

    override val name: String = "hide"
}