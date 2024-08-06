package com.kos.boxdrawe.presentation.template

import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateFigureBuilderListener
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemContainer
import com.kos.boxdrawer.template.TemplateItemHiddenText
import com.kos.boxdrawer.template.TemplateItemLabel
import com.kos.boxdrawer.template.TemplateItemMulti
import com.kos.boxdrawer.template.TemplateItemOne
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.TemplateMemoryItem
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

private const val FORM_NAME = "^form^"
private const val VARIANT_NAME = "^variant^"
const val MULTI_NAME = "^multi_names^"
const val ONE_NAME = "^one_name^"

class TemplateFigureEditor(val insertText: (String) -> Unit) : TemplateFigureBuilderListener {
    val memory = TemplateMemory()

    private var selectCommand: HelpInfoCommand = HelpInfoCommand("", emptyList())
    private var selectData: HelpData = HelpData("", "")
    private var form = TemplateForm("", "", emptyList())
    override fun setFigure(command: HelpInfoCommand, data: HelpData) {
        if (selectCommand != command) {
            memory.clear()
        }
        selectCommand = command
        selectData = data
        form = buildForm()
    }

    private fun buildForm(): TemplateForm{
        return this.selectData.creator?.let { c ->
            buildEditor(
                c,
                this.selectData,
                this.selectCommand.name,
                false
            )
        } ?: buildSimpleEditor(
            this.selectData,
            this.selectCommand.name
        )
    }

    override fun put(arg: String, index: Int, count: Int, value: String) {
    //    println("$arg $index : $value")
        memory.put(arg, index, count, value)
        recalc()
    }

    override fun put(arg: String, value: String) {
     //   println("$arg : $value")
        memory.put(arg, value)
        recalc()
    }

    override fun get(arg: String): List<String> {
        return memory.get(arg)
    }

    override fun putList(arg: String, value: List<String>) {
   //     println("$arg list : $value")
        memory.put(arg, TemplateMemoryItem(value))
        recalc()
    }

    override fun removeItem(arg: String) {
    //    println("$arg remove")
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
        val prefix = ".${selectCommand.name}."
        val com = selectCommand.name

        val creator = selectData.creator
        val text = if (creator != null) {
            com + buildText(form, memory, "") //buildCommand(creator, ::command, prefix)
        } else {
            val args = selectData.params.map { p ->
                command(prefix, MemoryKey("@" + p.name))
            }
            args.joinToString(" ", "$com ")
        }
        insertText(text)
    }

    private fun command(prefix: String, key: MemoryKey): String {
        return memory.get(prefix + key.name.drop(1)).joinToString(" ")
    }

    //region build editor

    override fun getForm(): TemplateForm {
        return form
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
        needSkobki: Boolean,
    ): TemplateForm? {

        val itemList = creator.inner.mapIndexedNotNull { i, b ->
            createEditField(b, memory, i)
        }

        val items = if (needSkobki) {
            listOf(TemplateItemHiddenText("${creator.skobka}")) + itemList +
                    TemplateItemHiddenText("${creator.closeBrace()}")
        } else itemList


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
                        //"i.form.x"
                        TemplateForm(
                            "",
                            FORM_NAME,
                            sb.toList()
                        )
                    }
                    //"i.x"
                    sb.size == 1 -> TemplateItemContainer("", FORM_NAME, sb[0])
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
                val sb = createArgumentEditor(creator, memory)
                //"i.varinatj.x"
                if (sb.size >= 1) {
                    val m = sb.mapIndexed { j, t ->
                        TemplateItemMulti(
                            "",
                            "$VARIANT_NAME${j + 1}",
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

            TPArg.ONE -> {
                val sb = createArgumentEditor(creator, memory)
                val m = when {
                    sb.size > 1 -> {
                        //"i.form.x"
                        TemplateForm(
                            "",
                            FORM_NAME,
                            sb.toList()
                        )
                    }
                    //"i.x"
                    sb.size == 1 -> TemplateItemContainer("", FORM_NAME, sb[0])
                    else -> null
                }
                m?.let { t ->
                    TemplateItemOne(
                        "",
                        prefix,
                        t
                    )
                }
            }

            TPArg.ONE_VARIANT -> {
                //"i.x.x"
                val sb = createArgumentEditor(creator, memory)
                if (sb.size >= 1) {
                    val m = sb.map { t ->
                        TemplateItemOne(
                            "",
                            t.argumentName,
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
            TPArg.UNION -> {
                val sb = createArgumentEditor(creator, memory)
                TemplateForm(
                    title = "",
                    argumentName = prefix,
                    list = sb.toList(),
                    separator = ""
                )
            }
            else -> {
                val sb = createArgumentEditor(creator, memory)
                //"i.x"
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
                //"x"
                memory.params.find { it.name == n }?.let { param ->
                    templateItem(param)
                }
            } else TemplateItemHiddenText(b.name.name)
        } else {
            if (b is TortoiseParserStackBlock) {
                if (TPArg.isAction(b)) {
                    //"i.x"
                    buildActionEditor(b, memory, "$index")
                } else {
                    ////"i.x"
                    buildEditor(b, memory, "$index", true)
                }
            } else null
        }
        return t
    }

    private fun buildText(info: TemplateItem, memory: TemplateMemory, prefix:String):String{
        val newPrefix = "$prefix.${info.argumentName}"
        return when (info){
            is TemplateForm -> {
            //    println("<< $newPrefix")
                info.list.joinToString(info.separator) { i -> buildText(i, memory, newPrefix) }
            }
            is TemplateItemContainer -> {
            //    println("<^ $newPrefix")
                buildText(info.data, memory,newPrefix)
            }
            is TemplateItemHiddenText -> {
                info.title
            }
            is TemplateItemLabel -> {
                ""
            }
            is TemplateItemMulti -> {
            //    println("<< $newPrefix.$MULTI_NAME : ${ memory.get("$newPrefix.$MULTI_NAME")}")
                memory.get("$newPrefix.$MULTI_NAME").joinToString(" ") { mn ->
                    //       println("<<< {$newPrefix.$mn}")
                    buildText(info.data, memory, "$newPrefix.$mn")
                }
            }
            is TemplateItemOne -> {
          //      println("<< $newPrefix.$ONE_NAME  : ${ memory.get("$newPrefix.$ONE_NAME")}")
                memory.get("$newPrefix.$ONE_NAME").firstOrNull()?.toDoubleOrNull()?.takeIf { it > 0.0 }?.let { mn ->
                  //  println("<<< $newPrefix.$ONE_NAME  : ${ memory.get("$newPrefix.$ONE_NAME")}")
                    buildText(info.data, memory, newPrefix)
                }?:""
            }
            else -> {
                memory.get(newPrefix).joinToString(" ")
            }
        }
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