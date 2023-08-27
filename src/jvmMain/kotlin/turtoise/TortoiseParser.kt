package turtoise


object TortoiseParser {

    private val sep = charArrayOf(' ', '\t', ';', '\n')

    private fun closeBrace(brace: Char): Char {
        return when (brace) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '>' -> '<'
            '<' -> '>'
            '"' -> '"'
            '\'' -> '\''
            else -> ' '
        }
    }

    private fun isDigit(c: Char): Boolean {
        return c >= '0' && c <= '9' || c == '-' || c == '+'
    }

    public fun extractTortoiseCommands(line: String): TortoiseAlgorithm {
        val a = line.split(*sep).filter { it.isNotEmpty() }
        return if (a.isEmpty()) {
            TortoiseSimpleAlgorithm("_", listOf<TortoiseCommand>())
        } else {
            parseSimpleLine(a)
        }
    }

    private fun parseSimpleLine(a: List<String>): TortoiseAlgorithm {
        val result = mutableListOf<TortoiseCommand>()

        var currentCommand = TortoiseCommand.TURTOISE_MOVE
        var currentValues = mutableListOf<String>()

        for (d in a) {
            var c = d.first()
            val sv: Int

            if (isDigit(c)) {
                c = ' ';
                sv = 0;
            } else {
                sv = 1;
            }

            val v = d.substring(sv)
            if (c != ' ' && c!= '@') {
                result.add(TortoiseCommand.create(currentCommand, currentValues))

                currentCommand = c
                currentValues = mutableListOf<String>()
            }

            if (v.isNotEmpty()) {
                currentValues.add(v)
            }
        }

        result.add(TortoiseCommand.create(currentCommand, currentValues))

        return TortoiseSimpleAlgorithm("_", result.toList());

    }
}