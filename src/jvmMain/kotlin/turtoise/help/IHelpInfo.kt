package turtoise.help

import androidx.compose.ui.text.AnnotatedString

interface IHelpInfo {
    fun help(): AnnotatedString
    val name: String
    fun help(command: String): AnnotatedString
}

abstract class SimpleHelpInfo : IHelpInfo {
    abstract val title: AnnotatedString
    abstract val commandList: List<HelpInfoCommand>

    override fun help(): AnnotatedString {
        val sb = AnnotatedString.Builder()
        sb.append(title)
        sb.appendLine()
        commandList.joinTo(sb, "\n\n") { it.text }
        return sb.toAnnotatedString()
    }

    override fun help(command: String): AnnotatedString {
        val coms = commandList.filter { it.name.contains(command) }
        if (coms.isEmpty()) {
            return help()
        }
        val sb = AnnotatedString.Builder()
        coms.joinTo(sb, "\n\n") { it.text }
        return sb.toAnnotatedString()
    }
}