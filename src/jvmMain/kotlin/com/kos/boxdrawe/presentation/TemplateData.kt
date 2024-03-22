package com.kos.boxdrawe.presentation

import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.TemplateMemoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import turtoise.TemplateAlgorithm
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.TurtoiseParserStackBlock
import turtoise.memory.TwoBlockTortoiseMemory

class TemplateData(val tools: Tools) {
    private val templater = TemplateCreator

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
    val menu = algorithm.map {
        TemplateInfo(
            templater.parse(it.template),
            it.default,
        )
    }

    val templateText = MutableStateFlow("")

    val memory = TemplateMemory()

    fun redraw() {
        val runner = TortoiseRunner(TortoiseProgram(emptyList(), tools.algorithms().toMap()))

        val top = TurtoiseParserStackBlock()
        memory.memoryBlock(top)

        val m = TwoBlockTortoiseMemory(top, algorithm.value.default)

        templateText.value = top.line

        tools.currentFigure.value =
            algorithm.value.draw("", tools.ds(), TortoiseState(), m, runner, 10)
    }

    val templateGenerator = object : TemplateGeneratorListener {
        override fun templateGenerator(arg: String, index: Int, count: Int, value: String) {
            if (count > 1) {
                memory.put(arg, index, count, value)
                redraw()
            } else {
                templateGenerator(arg, value)
            }
        }

        override fun templateGenerator(arg: String, value: String) {
            memory.put(arg, value)
            redraw()
        }
    }

    fun setTemplate(a: TemplateAlgorithm, name: String) {
        algorithm.value = a
        algorithmName.value = name
        memory.clear()

        redraw()
    }

    fun clearTemplate() {
        algorithm.value = emptyAlgorithm
        algorithmName.value = ""
        memory.clear()
        redraw()
    }

    fun save(fileName: String) {
        //   redraw()
        tools.saveFigures(fileName, tools.currentFigure.value)
        tools.updateChooserDir(fileName)
    }

    suspend fun print(): String {
        val name = algorithmName.value

        val top = TurtoiseParserStackBlock()

        memory.union( menu.first().memoryValues()).memoryBlock(top)

        val memoryarguments = top.line

        return "f (@${name} ${memoryarguments})"
    }


}