package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.TemplateMemoryItem
import com.kos.boxdrawer.template.editor.TemplateEditorForm
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import turtoise.TemplateAlgorithm
import turtoise.TortoiseFigureExtractor
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.help.TortoiseHelpInfo
import turtoise.memory.TwoBlockTortoiseMemory
import turtoise.paint.PaintUtils
import turtoise.parser.TortoiseParserStackBlock
import vectors.Matrix
import vectors.Vec2

class TemplateData(
    override val tools: ITools,
    val selectedItem: MutableStateFlow<List<FigureInfo>>
) : SaveFigure {
    private val templater = TemplateCreator
    private val emptyAlgorithm = TemplateAlgorithm(
        "",
        TortoiseParserStackBlock(),
        TortoiseParserStackBlock(),
        TortoiseParserStackBlock()
    )

    val checkboxEditor = MutableStateFlow(false)

    private val algorithm = MutableStateFlow(
        emptyAlgorithm
    )

    private val algorithmName = MutableStateFlow("")
    private val menu2 = algorithm.map {
        TemplateInfo(
            templater.parse(it.template),
            it.default,
            false,
        )
    }

    val memory = TemplateMemory()

    val currentFigure = MutableStateFlow<IFigure>(FigureEmpty)
    val figureLine = mutableStateOf("")
    val figureName = mutableStateOf("")


    val templateEditor = MutableStateFlow(TemplateEditorForm(TemplateForm("", "", true, emptyList())))

    val menu = combine(menu2, templateEditor, checkboxEditor) { n, e, check ->
        if (check) TemplateInfo(e.form, TortoiseParserStackBlock(), true) else n
    }

    fun redraw() {
        val runner = TortoiseRunner(TortoiseProgram(emptyList(), tools.algorithms().toMap()))

        val top = TortoiseParserStackBlock()
        memory.memoryBlock(top)

        val m = TwoBlockTortoiseMemory(top, algorithm.value.default)

        selectedItem.value = emptyList()
        //  dxfPreview.value = false
        currentFigure.value =
            algorithm.value.draw(
                name = "",
                state = TortoiseState(),
                figureExtractor = TortoiseFigureExtractor(
                    ds = tools.ds(),
                    maxStackSize = 10,
                    memory = m,
                    runner = runner,
                )
            )
    }

    val templateGenerator = object : TemplateGeneratorListener {
        override fun put(arg: String, index: Int, count: Int, value: String) {
            if (count > 1) {
                memory.put(arg, index, count, value)
                redraw()
            } else {
                put(arg, value)
            }
        }

        override fun put(arg: String, value: String) {
            memory.put(arg, value)
            redraw()
        }

        override fun putList(arg: String, value: List<String>) {
            memory.put(arg, TemplateMemoryItem(value))
            redraw()
        }

        override fun removeItem(arg: String) {
            memory.remove(arg)
            redraw()
        }

        override fun get(arg: String): List<String> {
            return memory.get(arg)
        }

        override fun editorRemoveItem(arg: String) {
            templateEditor.value =
                TemplateEditorForm(
                    form = templateEditor.value.form.remove(arg)
                )
        }

        override fun editorAddItem(name: String, title: String, argument: String) {
            val arg = argument.split(".").last()
            templater.createItem(
                name = name,
                title = title,
                argument = arg,
                nameItems = true,
                block = TortoiseParserStackBlock().apply {
                    addItems(
                        listOf(
                            TortoiseParserStackBlock().apply {
                                add(listOf("title", title))
                            },
                            TortoiseParserStackBlock().apply {
                                add(listOf("arg", arg))
                            }
                        )
                    )
                })?.let { item ->
                val frm = templateEditor.value.form.replace(".$argument", item)

                templateEditor.value =
                    TemplateEditorForm(
                        frm
                    )
            }
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

    override suspend fun createFigure(): IFigure = currentFigure.value

    suspend fun print(): String {
        if (checkboxEditor.value) {
            return TemplateAlgorithm.constructBlock(
                figureName.value,
                templateEditor.value.form,
                memory,
                figureLine.value
            )

        } else {

//            if (dxfPreview.value){
//                val figures = FigureList(selectedItem.value)
//                return "f ("+figures.print()+")"
//            }else
//            {
            val name = algorithmName.value

            val top = TortoiseParserStackBlock()

            memory.union(menu.first().memoryValues()).memoryBlock(top)

            val memoryarguments = top.innerLine

            return "f (@${name} ${memoryarguments})"
            // }
        }
    }

    suspend fun onPress(point: Vec2, button: Int, scale: Float) {
        val figure = currentFigure.value
        val result = PaintUtils.findFiguresAtCursor(Matrix(), point, 1.0, listOf(figure))
        selectedItem.value = result
    }

    // val selectedItem = MutableStateFlow<List<IFigure>>(emptyList())
}