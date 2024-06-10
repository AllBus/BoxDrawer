package com.kos.boxdrawer.template

import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem
import turtoise.memory.keys.MemoryKey.Companion.orEmpty

object TemplateCreator {

    fun parse(line: String): TemplateForm {
        return parse(TortoiseParser.parseSkobki(line))
    }

    fun parse(skobki: TortoiseParserStackItem): TemplateForm {
        if (skobki is TortoiseParserStackBlock) {
            return createForm(skobki)
        } else {
            return TemplateForm("", "", emptyList())
        }
    }

    private fun createForm(block: TortoiseParserStackBlock): TemplateForm {
        val title =
            block.getBlockAtName("title")?.blocks?.firstOrNull()?.innerLine.orEmpty()
        val argument = block.getBlockAtName("arg")?.value.orEmpty().name

        val items = block.getBlockAtName("items")?.blocks?.mapNotNull { b ->
            createItem(b)
        }.orEmpty()
        return TemplateForm(title, argument, items)
    }

    private fun createItem(block: TortoiseParserStackBlock): TemplateItem? {
        val title = block.inner.getOrNull(2)?.innerLine.orEmpty()
        val argument = block.inner.getOrNull(1)?.argument.orEmpty().name
        val name = block.name.name.lowercase()

        return createItem(name = name, title = title, argument = argument, block = block)
    }

    fun createMulti(block: TortoiseParserStackBlock): TemplateItemMulti? {
        val title =
            block.getBlockAtName("title")?.blocks?.firstOrNull()?.innerLine.orEmpty()
        val argument = block.getBlockAtName("arg")?.value.orEmpty().name

        return block.getBlockAtName("item")?.blocks?.firstOrNull()?.let { b ->
            createItem(b)
        }?.let { item ->
            TemplateItemMulti(
                title,
                argument,
                item
            )
        }
    }

    fun createItem(
        name: String,
        title: String,
        argument: String,
        block: TortoiseParserStackBlock? = null
    ): TemplateItem? {
        if (argument.isEmpty())
            return null
        return when (name) {
            "2", "size", "point", "offset" -> TemplateItemSize(
                title = title,
                argumentName = argument,
            )

            "4", "rect" -> TemplateItemRect(
                title = title,
                argumentName = argument,
            )

            "1", "float", "double", "mumeric", "decimal" -> TemplateItemNumeric(
                title = title,
                argumentName = argument,
            )

            "int" -> TemplateItemInt(
                title = title,
                argumentName = argument,
            )

            "3", "triple", "triangle", "coord" -> TemplateItemTriple(
                title = title,
                argumentName = argument,
            )

            "a", "angle", "degree" -> TemplateItemAngle(
                title = title,
                argumentName = argument,
            )

            "check", "chekbox", "switch" -> TemplateItemCheck(
                title = title,
                argumentName = argument,
            )

            "string" , "text" -> TemplateItemString(
                title = title,
                argumentName = argument,
            )

            "label" -> TemplateItemLabel(
                title = title,
                argumentName = argument,
            )

            "form" -> block?.let { b -> createForm(b) }
            "multi" -> block?.let { b -> createMulti(b) }
            else -> null
        }
    }
}