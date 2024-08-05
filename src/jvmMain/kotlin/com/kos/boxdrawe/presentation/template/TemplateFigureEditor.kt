package com.kos.boxdrawe.presentation.template

import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateFigureBuilderListener
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemMulti
import com.kos.boxdrawer.template.TemplateItemOne
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.TemplateMemoryItem
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.help.HelpInfoCommand
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

private const val FORM_NAME = "^form^"
const val MULTI_NAME = "^multi_names^"
const val ONE_NAME = "^one_name^"

class TemplateFigureEditor(val insertText: (String) -> Unit) : TemplateFigureBuilderListener {
    val memory = TemplateMemory()

    private var selectCommand: HelpInfoCommand = HelpInfoCommand("", emptyList())
    private var selectData: HelpData = HelpData("", "")
    override fun setFigure(command: HelpInfoCommand, data: HelpData) {
        if (selectCommand != command) {
            memory.clear()
        }
        selectCommand = command
        selectData = data
    }

    override fun put(arg: String, index: Int, count: Int, value: String) {
            println("$arg $index : $value")
        memory.put(arg, index, count, value)
        recalc()
    }

    override fun put(arg: String, value: String) {
            println("$arg : $value")
        memory.put(arg, value)
        recalc()
    }

    override fun get(arg: String): List<String> {
        return memory.get(arg)
    }

    override fun putList(arg: String, value: List<String>) {
        println("$arg list : $value")
        memory.put(arg, TemplateMemoryItem(value))
        recalc()
    }

    override fun removeItem(arg: String) {
        println("$arg remove")
        memory.remove(arg)
        recalc()
    }

    override fun editorRemoveItem(arg: String) {
        // nothing
    }

    override fun editorAddItem(name: String, title: String, argument: String) {
        // nothing
    }


    fun recalc() {
        val prefix = "${selectCommand.name}/"
        val com = selectCommand.name

        //Todo Нужно вставлять правильно параметры
        val creator = selectData.creator
        val text = if (creator != null) {
            com + buildCommand(creator, memory, prefix)
        } else {
            val args = selectData.params.map { p ->
                val v = memory.get(prefix + "@" + p.name)
                v.joinToString(" ")
            }
            args.joinToString(" ", "$com ")
        }
        insertText(text)
    }

    //region build command
    private fun buildCommand(
        creator: TortoiseParserStackBlock,
        memory: TemplateMemory,
        prefix: String,
    ): String {
        val sb = StringBuilder()
        creator.inner.forEachIndexed { i, b ->
            val t = createCommandText(b, memory, prefix, i)
            sb.append(t)
            sb.append(" ")
        }

        return sb.toString()
    }

    private fun buildActionCommand(
        creator: TortoiseParserStackBlock,
        memory: TemplateMemory,
        prefix: String,
    ): String {
        val sb = StringBuilder()
        val action = creator.name

        when (action) {
            TPArg.SOME,
            TPArg.VARIANT,
            TPArg.MULTI -> {
                val counter = memory.get(prefix + "._multi_names_")
                for (i in 1 until creator.inner.size) {
                    val b = creator.inner[i]
                    val t = createCommandText(b, memory, prefix, i)
                    sb.append(t)
                }
            }

            TPArg.UNION -> {
                for (i in 1 until creator.inner.size) {
                    val b = creator.inner[i]
                    val t = createCommandText(b, memory, prefix, i)
                    sb.append(t)
                }
            }

            else ->
                for (i in 1 until creator.inner.size) {
                    val b = creator.inner[i]
                    val t = createCommandText(b, memory, prefix, i)
                    sb.append(t)
                    sb.append(" ")
                }
        }
        return sb.toString()
    }

    private fun createCommandText(
        b: TortoiseParserStackItem,
        memory: TemplateMemory,
        prefix: String,
        index: Int,
    ): String {
        val t = if (b.isArgument()) {
            if (b.name.prefix() == '@') {
                val v = memory.get(prefix + b.name.name)
                v.joinToString(" ")
            } else {
                b.name.name
            }
        } else {
            if (b is TortoiseParserStackBlock) {
                if (TPArg.isAction(b)) {
                    buildActionCommand(b, memory, prefix + ".$index")
                } else {
                    "${b.skobka}${buildCommand(b, memory, prefix)}${b.closeBrace()}"
                }
            } else ""
        }
        return t
    }
    //endregion


    //region build editor

    override fun getForm(): TemplateForm {
        return this.selectData.creator?.let { c ->
            buildEditor(
                c,
                this.selectData,
                this.selectCommand.name
            )
        } ?: buildSimpleEditor(
            this.selectData,
            this.selectCommand.name
        )
    }

    private fun buildSimpleEditor(
        memory: HelpData,
        prefix: String,
    ): TemplateForm {
        val items = memory.params.mapNotNull { param ->
            templateItem(param)
        }
        return TemplateForm(
            "",
            prefix,
            items
        )
    }

    private fun buildEditor(
        creator: TortoiseParserStackBlock,
        memory: HelpData,
        prefix: String,
    ): TemplateForm? {

        val items = creator.inner.mapIndexedNotNull { i, b ->
            createEditField(b, memory, i)
        }

        return if (items.isNotEmpty()) {
            TemplateForm(
                "",
                prefix,
                items
            )
        } else
            null
    }

    private fun buildActionEditor(
        creator: TortoiseParserStackBlock,
        memory: HelpData,
        prefix: String,
    ): TemplateItem? {
        val action = creator.name

        val form: TemplateItem? = when (action) {
            TPArg.SOME,
            TPArg.MULTI -> {
                val sb = createArgumentEditor(creator, memory)
                (when {
                    sb.size > 1 -> {
                        TemplateForm(
                            "",
                            FORM_NAME,
                            sb.toList()
                        )
                    }

                    sb.size == 1 -> sb[0]
                    else -> null
                }
                        )?.let { t ->
                        TemplateItemMulti(
                            "",
                            prefix,
                            t
                        )
                    }
            }

            TPArg.VARIANT -> {
                val sb = createArgumentEditor(creator, memory,)
                if (sb.size >= 1) {
                    val m = sb.map { t ->
                        TemplateItemMulti(
                            "",
                            FORM_NAME,
                            t
                        )
                    }
                    TemplateForm(
                        "",
                        prefix,
                        m
                    )
                } else
                    null
            }

            TPArg.ONE ->{
                val sb =  createArgumentEditor(creator, memory)
                val m = when {
                    sb.size > 1 -> {
                        TemplateForm(
                            "",
                            FORM_NAME,
                            sb.toList()
                        )
                    }

                    sb.size == 1 -> sb[0]
                    else -> null
                }
                m?.let{ t ->
                    TemplateItemOne(
                        "",
                        prefix,
                        t
                    )
                }
            }
            TPArg.ONE_VARIANT -> {
                val sb =  createArgumentEditor(creator, memory)
                if (sb.size >= 1) {
                    val m = sb.map { t ->
                        TemplateItemOne(
                            "",
                            FORM_NAME,
                            t
                        )
                    }
                    TemplateForm(
                        "",
                        prefix,
                        m
                    )
                }else
                    null
            }
            else -> {
                val sb =  createArgumentEditor(creator, memory)
                TemplateForm(
                    "",
                    prefix,
                    sb.toList()
                )
            }
        }
        return form
    }

    private fun createArgumentEditor(
        creator: TortoiseParserStackBlock,
        memory: HelpData,
    ): MutableList<TemplateItem> {
        val sb = mutableListOf<TemplateItem>()
        for (i in 1 until creator.inner.size) {
            val b = creator.inner[i]
            createEditField(b, memory, i)?.let { t ->
                sb.add(t)
            }
        }
        return sb
    }

    private fun createEditField(
        b: TortoiseParserStackItem,
        memory: HelpData,
        index: Int,
    ): TemplateItem? {
        val t: TemplateItem? = if (b.isArgument()) {
            if (b.name.prefix() == '@') {
                val n = b.name.name.drop(1)
                memory.params.find { it.name == n }?.let { param ->
                    templateItem(param)
                }
            } else null
        } else {
            if (b is TortoiseParserStackBlock) {
                if (TPArg.isAction(b)) {
                    buildActionEditor(b, memory, "$index")
                } else {
                    buildEditor(b, memory, "$index")
                }
            } else null
        }
        return t
    }

    private fun templateItem(param: HelpDataParam) = TemplateCreator.createItem(
        param.kind,
        param.name,
        param.name,
        param.variants?.let { v ->
            TortoiseParserStackBlock(
                '(', param.kind, listOf(
                    TPArg.text(param.name),
                    TPArg.text(param.name),
                    TPArg.block(v.map { TPArg.text(it) })
                )
            )
        }
    )
    //endregion

}