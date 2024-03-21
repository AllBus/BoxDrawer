package com.kos.boxdrawe.presentation

import com.kos.boxdrawer.template.TemplateCreator
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import turtoise.TemplateAlgorithm
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem
import turtoise.memory.BlockTortoiseMemory
import turtoise.memory.SimpleTortoiseMemory

class TemplateData(val tools: Tools) {
    private val templater = TemplateCreator()

    private val emptyAlgorithm = TemplateAlgorithm(
        "",
        TurtoiseParserStackBlock(),
        TurtoiseParserStackBlock(),
        TurtoiseParserStackBlock()
    )

    private val algorithm = MutableStateFlow<TemplateAlgorithm>(
        emptyAlgorithm
    )

    private val algorithmName = MutableStateFlow("")
    private val menuText = algorithm.map { it.template }

    val menu = menuText.map {
        templater.parse(it)
    }

    val templateText = MutableStateFlow("")

    val memory = mutableMapOf<String, String>()

    fun redraw() {
        val runner = TortoiseRunner(TortoiseProgram(emptyList() ,tools.algorithms().toMap()))
        val mb = memoryBlock()
        val m = BlockTortoiseMemory(mb)

        templateText.value = mb.line

        tools.currentFigure.value = algorithm.value.draw("", tools.ds(), TortoiseState(), m, runner,10)
    }

    fun templateGenerator(arg: String, value: String) {
//        when (value) {
//            "true" -> memory.assign(arg, 1.0)
//            "false" -> memory.assign(arg, 0.0)
//            else -> memory.assign(arg, value.toDoubleOrNull() ?: 0.0)
//        }

        memory.put(arg, value)
       // templateText.value = memory.m.map { (k, v) -> "$k : $v" }.joinToString("\n")
        redraw()
    }

    fun setTemplate(a: TemplateAlgorithm, name:String ){
        algorithm.value = a
        algorithmName.value = name
        memory.clear()
        putMemoryArguments(a.default)
        redraw()
    }

    private fun putMemoryArguments(default: TurtoiseParserStackItem) {
        // Todo:
    }

    fun clearTemplate() {
        algorithm.value = emptyAlgorithm
        algorithmName.value = ""
        memory.clear()
        redraw()
    }

    fun save(fileName: String) {
     //   redraw()
        tools.saveFigures(fileName,  tools.currentFigure.value)
        tools.updateChooserDir(fileName)
    }

    fun print(): String {
        val name = algorithmName.value

        val memoryarguments = memoryBlock().line

        return "f (@${name} ${memoryarguments})"
    }

    private fun memoryBlock(): TurtoiseParserStackBlock {
        val top = TurtoiseParserStackBlock()
        memory.forEach { (k, v) ->
            val sp = k.split('.').drop(1)
            var p = top
            for (c in sp) {
                val b = p.getBlockAtName(c)
                if (b == null) {
                    val nb = TurtoiseParserStackBlock('(')
                    nb.add(c)
                    p.add(nb)
                    p = nb
                } else
                    p = b
            }
            p.add(v)
        }
        return top
    }


}