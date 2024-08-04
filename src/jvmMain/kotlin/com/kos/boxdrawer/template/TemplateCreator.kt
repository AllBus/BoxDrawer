package com.kos.boxdrawer.template

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_3
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_4
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_CHECK
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_COLOR
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FORM
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_LABEL
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_MULTI
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_SELECTOR
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
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

    fun createSelector(block: TortoiseParserStackBlock): TemplateItemSelector {
        val title = block.inner.getOrNull(2)?.innerLine.orEmpty()
        val argument = block.inner.getOrNull(1)?.argument.orEmpty().name
        val variants = block.inner.getOrNull(3)?.inner.orEmpty()

        return TemplateItemSelector(
            title,
            argument,
            variants.map { it.innerLine }
        )
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

            FIELD_1, "float", "double", "mumeric", "decimal" -> TemplateItemNumeric(
                title = title,
                argumentName = argument,
            )
            FIELD_2, "size", "point", "offset" -> TemplateItemSize(
                title = title,
                argumentName = argument,
            )

            FIELD_4, "rect" -> TemplateItemRect(
                title = title,
                argumentName = argument,
            )

            FIELD_INT -> TemplateItemInt(
                title = title,
                argumentName = argument,
            )
            FIELD_COLOR -> {
                TemplateItemColor(
                    title = title,
                    argumentName = argument,
                )
            }
            FIELD_3, "triple", "triangle", "coord" -> TemplateItemTriple(
                title = title,
                argumentName = argument,
            )

            FIELD_ANGLE, "angle", "degree" -> TemplateItemAngle(
                title = title,
                argumentName = argument,
            )

            FIELD_CHECK, "chekbox", "switch" -> TemplateItemCheck(
                title = title,
                argumentName = argument,
            )

            FIELD_FIGURE  -> TemplateItemFigure(
                title = title,
                argumentName = argument,
            )

            FIELD_TEXT ,"string"  -> TemplateItemString(
                title = title,
                argumentName = argument,
            )

            FIELD_LABEL -> TemplateItemLabel(
                title = title,
                argumentName = argument,
            )

            FIELD_SELECTOR -> block?.let { b -> createSelector(b) }

            FIELD_FORM -> block?.let { b -> createForm(b) }
            FIELD_MULTI -> block?.let { b -> createMulti(b) }

            else -> null
        }
    }


}