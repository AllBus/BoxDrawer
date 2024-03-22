package com.kos.boxdrawer.template

import com.kos.boxdrawer.presentation.TemplateFormBox
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem

class TemplateInfo(
    val form :TemplateForm,
    val values: TurtoiseParserStackItem,
) {
    fun memoryValues(): Map<String,String>{

        val block = values
        if (form.argumentName.isNotEmpty()) {
            values.getInnerAtName(form.argumentName)
        } else
            values

        val prefix =
            if (form.argumentName.isNotEmpty()) "." + form.argumentName else ""

        return memoryValues(form, block,  prefix)
    }

    fun memoryValues(item: TemplateItem, block: TurtoiseParserStackItem, prefix:String): Map<String,String>{
        val newPrefix = prefix + "." + item.argumentName
        val inner = block.getInnerAtName(item.argumentName)?: return  emptyMap()

        return when (item) {
            is TemplateForm -> {
                var mapp = mutableMapOf<String, String>()
                form.list.forEach {
                    mapp+= memoryValues(it, inner, newPrefix)
                }
                mapp
            }
            is TemplateItemLabel -> emptyMap()

            else -> {
                if (item.argumentCount == 1){
                    mapOf(newPrefix to inner.argument)
                }else{
                    (1..item.argumentCount).map{i ->
                        "$newPrefix.$i" to inner.get(i).orEmpty()
                    }.toMap()
                }
            }
        }
    }
}