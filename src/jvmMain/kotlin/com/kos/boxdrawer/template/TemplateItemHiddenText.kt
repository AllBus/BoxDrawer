package com.kos.boxdrawer.template

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_HIDDEN_TEXT
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

data class TemplateItemHiddenText(
    override val title: String,
) : TemplateItem {

    override val argumentName: String = ""

    override val argumentCount: Int
        get() = 0

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add(FIELD_HIDDEN_TEXT)
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}