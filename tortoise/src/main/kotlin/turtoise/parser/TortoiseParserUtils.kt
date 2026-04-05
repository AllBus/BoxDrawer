package turtoise.parser

object TortoiseParserUtils {

    private val sep = charArrayOf(' ', '\t', ';', '\n')

    fun closeBrace(brace: Char): Char {
        return when (brace) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '>' -> '<'
            '<' -> '>'
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '"' -> '"'
            '\'' -> '\''
            '/' -> '/'
            '\\' -> '\\'
            else -> ' '
        }


    }
}