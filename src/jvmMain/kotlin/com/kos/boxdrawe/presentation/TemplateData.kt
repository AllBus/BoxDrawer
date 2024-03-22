package com.kos.boxdrawe.presentation

import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateMemoryItem
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import turtoise.TemplateAlgorithm
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem
import turtoise.memory.BlockTortoiseMemory
import turtoise.memory.SimpleTortoiseMemory
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

    val memory = mutableMapOf<String, TemplateMemoryItem>()

    fun redraw() {
        val runner = TortoiseRunner(TortoiseProgram(emptyList() ,tools.algorithms().toMap()))
        val mb = memoryBlock()
        val m = TwoBlockTortoiseMemory(mb, algorithm.value.default)

        templateText.value = mb.line

        tools.currentFigure.value = algorithm.value.draw("", tools.ds(), TortoiseState(), m, runner,10)
    }

     val templateGenerator = object:TemplateGeneratorListener {
         override fun templateGenerator(arg: String, index:Int, count: Int, value: String) {
//        when (value) {
//            "true" -> memory.assign(arg, 1.0)
//            "false" -> memory.assign(arg, 0.0)
//            else -> memory.assign(arg, value.toDoubleOrNull() ?: 0.0)
//        }


             if (count > 1) {
                 memory.put(arg,
                     (memory.get(arg)?: TemplateMemoryItem(ArrayList<String>(count))).update(index, value)
                 )
                 redraw()
             } else {
                 templateGenerator(arg, value)
             }
         }
         override fun templateGenerator(arg: String, value: String) {
             memory.put(arg, TemplateMemoryItem(listOf(value)))
             redraw()
         }
     }

    fun setTemplate(a: TemplateAlgorithm, name:String ){
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
        memory.forEach { (k, value) ->
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
            value.values.forEach { vv ->
                p.add(vv)
            }
        }
        return top
    }


}