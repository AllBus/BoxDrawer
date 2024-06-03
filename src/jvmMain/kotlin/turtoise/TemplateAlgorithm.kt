package turtoise

import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateMemory
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem



class TemplateAlgorithm(
    name: String,
    line: TortoiseParserStackItem,
    val default: TortoiseParserStackItem,
    val template: TortoiseParserStackItem,
) : TortoiseFigureAlgorithm(name, line) {

    companion object {
        private const val BLOCK_FIGURE = "figure"
        private const val BLOCK_DEFAULT_VALUE = "default"
        private const val BLOCK_FORM = "form"
        private const val TEMPLATE_ALGORITHM_NAME = "template@"

        fun create(name:String, items: TortoiseParserStackBlock): TemplateAlgorithm {
            return TemplateAlgorithm(
                name = name,
                line = items.getBlockAtName(BLOCK_FIGURE) ?: TortoiseParserStackBlock(),
                default = items.getBlockAtName(BLOCK_DEFAULT_VALUE) ?: TortoiseParserStackBlock(),
                template = items.getBlockAtName(BLOCK_FORM) ?: TortoiseParserStackBlock(),
            )
        }

        fun constructBlock(name:String, formBlock: TemplateForm, defaultValueBlock: TemplateMemory, figure: String):String{
            val form = formBlock.print().innerLine
            val top = TortoiseParserStackBlock('(', BLOCK_DEFAULT_VALUE)
            defaultValueBlock.memoryBlock(top)
            return "$TEMPLATE_ALGORITHM_NAME$name ($form)(${top.innerLine})($BLOCK_FIGURE $figure)"
        }
    }

}


