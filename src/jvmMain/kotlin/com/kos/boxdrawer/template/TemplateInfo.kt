package com.kos.boxdrawer.template

import com.kos.boxdrawer.presentation.TemplateFormBox
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem

class TemplateInfo(
    val form: TemplateForm,
    val values: TurtoiseParserStackItem,
    val edit: Boolean,
) {
    fun memoryValues(): TemplateMemory {

        val block = values
        if (form.argumentName.isNotEmpty()) {
            values.getInnerAtName(form.argumentName)
        } else
            values

        val prefix =
            if (form.argumentName.isNotEmpty()) "." + form.argumentName else ""
     //   println(">>")
        val v = memoryFormValues(form, block, prefix)

       // println(v.map { (k, v) -> "$k : ${v.values.joinToString(", ")}" })
        return TemplateMemory(v)
    }

    fun memoryFormValues(
        form: TemplateForm,
        block: TurtoiseParserStackItem,
        prefix: String
    ): Map<String, TemplateMemoryItem> {

        val mapp = mutableMapOf<String, TemplateMemoryItem>()
        form.list.forEach {
            mapp += memoryValues(it, block, prefix)
        }
        return mapp
    }

    fun memoryValues(
        item: TemplateItem,
        block: TurtoiseParserStackItem,
        prefix: String
    ): Map<String, TemplateMemoryItem> {
        val newPrefix = prefix + "." + item.argumentName
//        println(newPrefix)
//        println(block.line)
//        println()
        val inner = block.getInnerAtName(item.argumentName) ?: return emptyMap()

        return when (item) {
            is TemplateForm -> {
                memoryFormValues(
                    form = item,
                    block = inner,
                    prefix = newPrefix,
                )
            }

            is TemplateItemLabel -> emptyMap()

            else -> {
                if (item.argumentCount == 1) {
                    mapOf(newPrefix to TemplateMemoryItem(listOf(inner.argument)))
                } else {
                    mapOf(
                        newPrefix to TemplateMemoryItem((1..item.argumentCount).map { i ->
                            inner.get(i).orEmpty()
                        })
                    )

                }
            }
        }
    }

    companion object {
        val EMPTY = TemplateInfo(TemplateForm("", "", emptyList()), TurtoiseParserStackBlock(), false)
    }
}