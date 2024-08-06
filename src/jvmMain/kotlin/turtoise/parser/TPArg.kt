package turtoise.parser

import com.kos.boxdrawe.presentation.template.FORM_NAME
import com.kos.boxdrawe.presentation.template.VARIANT_NAME
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateItem
import com.kos.boxdrawer.template.TemplateItemContainer
import com.kos.boxdrawer.template.TemplateItemFigure
import com.kos.boxdrawer.template.TemplateItemHiddenText
import com.kos.boxdrawer.template.TemplateItemMulti
import com.kos.boxdrawer.template.TemplateItemOne
import com.kos.boxdrawer.template.TemplateItemSelector
import turtoise.memory.keys.MemoryKey

object TPArg {

    val MULTI = MemoryKey("@*")
    val ONE = MemoryKey("@?")
    val SOME = MemoryKey("@+")
    val VARIANT = MemoryKey("@*?")
    val ONE_VARIANT = MemoryKey("@??")
    val LINE = MemoryKey("@?-")
    val UNION = MemoryKey("@^")


    private val actions = setOf(MULTI, ONE, SOME, VARIANT, ONE_VARIANT, LINE, UNION)

    fun isAction(block: TortoiseParserStackBlock): Boolean {
        return block.name in actions
    }

    operator fun invoke(name: String, kind: String): TemplateItem =
        TemplateCreator.createItem(kind, name, name, null)
            ?: TemplateItemHiddenText("") //  TortoiseParserStackArgument(MemoryKey("@$name"))

    fun selector(name: String, variants: List<String>): TemplateItem =
        TemplateItemSelector(name, name, variants)

    fun create(name: String, vararg args: TemplateItem): TemplateForm {
        return TemplateForm(
            title = " ",
            argumentName = "/",
            list = args.toList(),
            separator = " ",
            prefix = "$name ",
        )  //TortoiseParserStackBlock(' ', name, args.toList())
    }

    fun createWithoutName(vararg args: TemplateItem): TemplateForm {
        return TemplateForm(
            title = " ",
            argumentName = "/",
            list = args.toList(),
            separator = " ",
            prefix = "",
        )  //TortoiseParserStackBlock(' ', name, args.toList())
    }


    fun item(name: String, vararg args: TemplateItem): TemplateItem {
        return TemplateItemContainer(
            title = "",
            argumentName = name,
            list = args.toList(),
            separator = " ",
            prefix = "($name ",
            suffix = ")"
        )
        //TortoiseParserStackBlock('(', name, args.toList())
    }

    fun block(vararg args: TemplateItem): TemplateItem {
        return TemplateItemContainer(
            title = "",
            argumentName = "",
            list = args.toList(),
            separator = " ",
            prefix = "(",
            suffix = ")"
        )

        // return TortoiseParserStackBlock('(').apply { this.addItems(args) }
    }

    // fun block(vararg args: TemplateItem): TemplateItem {

    //return TortoiseParserStackBlock('(').apply { this.addItems(args.toList()) }
    // }

    fun figure(name: String): TemplateItem {
        return TemplateItemFigure(name, name)
        //  return TortoiseParserStackBlock(' ', "@$name")
    }

    fun text(text: String): TemplateItem {
        return TemplateItemHiddenText(text)
        //return TortoiseParserStackArgument(MemoryKey(text))
    }

    /** Элементы следуют без разделителя */
    fun union(vararg args: TemplateItem): TemplateItem {
        return TemplateItemContainer(
            title = "",
            argumentName = "",
            list = args.toList(),
            separator = "",
            prefix = "",
            suffix = ""
        )
        //return TortoiseParserStackBlock('(',  UNION).apply { this.addItems(args.toList()) }
    }

    /** Ноль или больше раз */
    fun multi(name: String, vararg args: TemplateItem): TemplateItem = TemplateItemMulti(
        title = "",
        argumentName = name,
        data = TemplateItemContainer(
            title = "",
            argumentName = FORM_NAME,
            list = args.toList(),
            separator = " ",
            prefix = "",
            suffix = ""
        )
    )
    // (' ', MULTI).apply { this.addItems(args.toList()) }


    /**  Каждый из вариантов может быть использован сколько угодно раз */
    fun multiVariant(name: String, vararg args: TemplateItem) = TemplateForm(
        title = " ",
        argumentName = name,
        list = args.mapIndexed { i, a ->
            TemplateItemMulti(
                title = "",
                argumentName = "$VARIANT_NAME$i",
                data = a
            )
        },
        separator = " ",
        prefix = "",
        suffix = ""
    )  //  TortoiseParserStackBlock(' ', VARIANT).apply { this.addItems(args.toList()) }

    /** Ноль или один раз */
    fun noneOrOne(name: String, vararg args: TemplateItem): TemplateItem = TemplateItemOne(
        title = "",
        argumentName = name,
        data = TemplateItemContainer(
            title = "",
            argumentName = FORM_NAME,
            list = args.toList(),
            separator = " ",
            prefix = "",
            suffix = ""
        )
    )

    /** элементы следуют в указанном порядке но они не обязательны */
    fun noneOrLine(vararg args: TemplateItem) = TemplateItemContainer(
        title = "",
        argumentName = "",
        list = args.toList(),
        separator = " ",
        prefix = "",
        suffix = ""
    )

    /** Один или больше раз */
    fun oneOrMore(name: String, vararg args: TemplateItem): TemplateItem = TemplateItemMulti(
        title = "",
        argumentName = name,
        data = TemplateItemContainer(
            title = "",
            argumentName = FORM_NAME,
            list = args.toList(),
            separator = " ",
            prefix = "",
            suffix = ""
        )
    )
    //TortoiseParserStackBlock(' ', SOME).apply { this.addItems(args.toList()) }

    /**  Каждый из вариантов может быть использован один раз */
    fun oneVariant(name: String, vararg args: TemplateItem) = TemplateForm(
        title = " ",
        argumentName = name,
        list = args.mapIndexed { i, a ->
            TemplateItemOne(
                title = "",
                argumentName = "$VARIANT_NAME$i",
                data = a
            )
        },
        separator = " ",
        prefix = "",
        suffix = ""
    )
    //TortoiseParserStackBlock(' ', ONE_VARIANT).apply { this.addItems(args.toList()) }
}