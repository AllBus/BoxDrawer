package com.kos.boxdrawer.template

interface TemplateGeneratorListener {
    fun put(arg: String, index:Int, count:Int, value: String)
    fun put(arg: String, value: String)
    fun putList(arg: String, value: List<String>)
    fun removeItem(arg:String)
    fun get(arg:String): List<String>
}