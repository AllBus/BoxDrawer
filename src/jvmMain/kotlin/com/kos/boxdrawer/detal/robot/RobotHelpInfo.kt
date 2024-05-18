package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.parser.TortoiseParser
import turtoise.help.HelpInfoCommand
import turtoise.help.SimpleHelpInfo

class RobotHelpInfo(): SimpleHelpInfo() {

    override val commandList: List<HelpInfoCommand> = RobotLine.factories.map { it.help() }
    override val title: AnnotatedString = TortoiseParser.helpTitle("Команды рисования робота. Каждая команда окружается скобками ()")
    override val name: String = "robot"
}