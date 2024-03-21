package com.kos.boxdrawe.presentation

import com.kos.boxdrawer.template.TemplateCreator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import turtoise.memory.SimpleTortoiseMemory

class TemplateData(val tools: ITools) {
    val templater = TemplateCreator()
    val menuText = MutableStateFlow("(form  (arg u) (title (Кубик)) " +
            "(items " +
            "(size s (Размер)) " +
            "(float w (Ширина)) " +
            "(rect r (Область)) " +
            "(triple t (Положение)) " +
            "(form (arg f) (title (Узлы)) (items "+
            "(check ch (Внутреняя)) " +
            "(int ci (Количество узлов)) " +
            "(string cs (Большая надпись))"+
            "(label l (Готово для вывода))"+
            "))"+
            ")" +
            ")")

    val menu = menuText.map { templater.parse(it) }

    val templateText =MutableStateFlow("")

    val memory = SimpleTortoiseMemory()

    fun templateGenerator(arg: String, value : String){
        when (value) {
            "true" -> memory.assign(arg, 1.0)
             "false" -> memory.assign(arg, 0.0)
            else -> memory.assign(arg, value.toDoubleOrNull()?:0.0)
        }

        templateText.value = memory.m.map { (k, v) -> "$k : $v" }.joinToString("\n")
    }
}