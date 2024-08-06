package com.kos.boxdrawe.presentation.template

import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateMemory

class TemplateMoveListener(val insertText: (String) -> Unit): TemplateGeneratorSimpleListener {
    val memory = TemplateMemory()

    override fun put(arg: String, index: Int, count: Int, value: String) {
        if (arg == "pos") {
            reposition(value)
        } else {
            if (count > 1) {
                memory.put(arg, index, count, value)
                recalc()
            } else {
                put(arg, value)
            }
        }
    }

    override fun put(arg: String, value: String) {
        if (arg == "pos") {
            reposition(value)
        } else {
            memory.put(arg, value)
            recalc()
        }
    }

    override fun get(arg: String): List<String> {
        return memory.get(arg)
    }

    fun reposition(value: String) {
        val f = " $value"
        insertText(f)
    }

    fun recalc() {
        val x = memory.get("xy").getOrElse(0) { "0" }
        val y = memory.get("xy").getOrElse(1) { "0" }
        val xa = memory.get("axy").getOrElse(0) { "0" }
        val ya = memory.get("axy").getOrElse(1) { "0" }
        val a = memory.get("a").getOrElse(0) { "0" }

        val f = if (xa.toDoubleOrNull() == 0.0 && ya.toDoubleOrNull() == 0.0) {
            if (x.toDoubleOrNull() == 0.0 && y.toDoubleOrNull() == 0.0) {
                " q $a "
            } else {
                " m $x $y $a "
            }
        } else {
            " m $x $y $a $xa $ya "
        }

        insertText(f)
    }
}