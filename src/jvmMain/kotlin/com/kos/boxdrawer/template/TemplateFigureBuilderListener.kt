package com.kos.boxdrawer.template

import androidx.compose.runtime.MutableState
import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand

interface TemplateFigureBuilderListener: TemplateGeneratorListener {
    fun setFigure(command : HelpInfoCommand, data: HelpData)

    fun getForm(): TemplateForm

    fun insertFigure()
    val currentText: MutableState<String>
}