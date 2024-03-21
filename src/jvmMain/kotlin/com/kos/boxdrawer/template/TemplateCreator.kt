package com.kos.boxdrawer.template

import turtoise.TortoiseParser
import turtoise.TurtoiseParserStackBlock

class TemplateCreator {

    fun parse(line: String): TemplateForm {
        return parse(TortoiseParser.parseSkobki(line))
    }

    fun parse(skobki: TurtoiseParserStackBlock): TemplateForm {
        return createForm(skobki.blocks.firstOrNull()?:TurtoiseParserStackBlock())
    }

    private fun createForm(block: TurtoiseParserStackBlock): TemplateForm {
        val title = block.getBlockAtName("title")?.blocks?.firstOrNull()?.line.orEmpty().dropSkobki()
        val argument = block.getBlockAtName("arg")?.argument.orEmpty()
        val items = block.getBlockAtName("items")?.blocks?.mapNotNull { b ->
            createItem(b)
        }.orEmpty()
        return TemplateForm(title, argument,  items)
    }

    private fun createItem(block: TurtoiseParserStackBlock): TemplateItem? {
        val title = block.inner.getOrNull(2)?.line.orEmpty().dropSkobki()
        val argument = block.inner.getOrNull(1)?.line.orEmpty().dropSkobki()
        val name = block.name.lowercase()

        return when (name){
            "2",  "size", "point", "offset" -> TemplateItemSize(
                title = title,
                argumentName = argument,
            )
            "4",  "rect"  -> TemplateItemRect(
                title = title,
                argumentName = argument,
            )
            "1",  "float", "double","mumeric","decimal"   -> TemplateItemNumeric(
                title = title,
                argumentName = argument,
            )
            "int"   -> TemplateItemInt(
                title = title,
                argumentName = argument,
            )
            "3", "triple" , "triangle", "coord"  -> TemplateItemTriple(
                title = title,
                argumentName = argument,
            )
            "check", "chekbox" , "switch"  -> TemplateItemCheck(
                title = title,
                argumentName = argument,
            )
            "string"  -> TemplateItemString(
                title = title,
                argumentName = argument,
            )
            "label"  -> TemplateItemLabel(
                title = title,
                argumentName = argument,
            )
            "form" -> createForm(block)
            else -> null
        }
    }

    fun String.dropSkobki():String{
        return this.drop(1).dropLast(1)
    }
}