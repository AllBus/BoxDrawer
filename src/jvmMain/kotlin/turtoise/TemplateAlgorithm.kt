package turtoise

import turtoise.parser.TortoiseParserStackItem

class TemplateAlgorithm(
    name: String,
    line: TortoiseParserStackItem,
    val default: TortoiseParserStackItem,
    val template: TortoiseParserStackItem,
) : TortoiseFigureAlgorithm(name, line) {


}
