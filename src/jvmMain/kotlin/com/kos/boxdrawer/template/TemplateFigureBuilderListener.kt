package com.kos.boxdrawer.template

import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand

interface TemplateFigureBuilderListener: TemplateGeneratorSimpleListener {
    fun setFigure(command : HelpInfoCommand, data: HelpData)
}