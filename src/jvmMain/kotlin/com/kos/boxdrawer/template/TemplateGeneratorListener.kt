package com.kos.boxdrawer.template

import androidx.compose.runtime.Stable

@Stable
interface TemplateGeneratorSimpleListener{
    fun put(arg: String, index:Int, count:Int, value: String)
    fun put(arg: String, value: String)
    fun get(arg:String): List<String>
}

@Stable
interface TemplateGeneratorListener: TemplateGeneratorSimpleListener {
    fun putList(arg: String, value: List<String>)
    fun removeItem(arg:String)

    fun editorRemoveItem(arg:String)
    fun editorAddItem(name:String, title:String, argument:String)
}