package com.kos.boxdrawer.template

interface TemplateGeneratorListener {
    fun templateGenerator(arg: String, index:Int, count:Int, value: String)
    fun templateGenerator(arg: String, value: String)
}