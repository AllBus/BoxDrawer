package com.kos.boxdrawer.template

import com.kos.boxdrawer.template.editor.TemplateField
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

data class TemplateItemContainer(
    override val title: String,
    override val argumentName: String,
    val list: List<TemplateItem>,
    val separator: String,
    val prefix:String,
    val suffix:String,
) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val inner = TortoiseParserStackBlock('(', "items", list.map { it.print() })
        val tp = TortoiseParserStackBlock()
        tp.add(TemplateField.FIELD_CONTAINER)
        tp.add("title", "[$title]")
        tp.add("arg", argumentName)
        tp.add("sep", "[$separator]")
        tp.add("prefix", "[$prefix]")
        tp.add("suffix", "[$suffix]")
        tp.add(inner)
        return tp
    }
}