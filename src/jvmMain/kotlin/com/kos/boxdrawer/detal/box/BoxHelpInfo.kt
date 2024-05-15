package com.kos.boxdrawer.detal.box

import androidx.compose.ui.text.AnnotatedString
import turtoise.TortoiseParser
import turtoise.help.HelpInfoCommand
import turtoise.help.SimpleHelpInfo

class BoxHelpInfo: SimpleHelpInfo() {
    override val title: AnnotatedString
        get() = TortoiseParser.helpTitle("Рисование коробки. Её Можно получить в табе Коробка")

    override val commandList: List<HelpInfoCommand>
        get() = listOf(
            TortoiseParser.helpName(
                "",
                "w h we (polka (( d o s e (h*) (as ac (a*))*)*) (zig (w d h e)*5)",
                ""
            )
        )
    override val name: String= "box"
}