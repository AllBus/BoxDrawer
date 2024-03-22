package turtoise.memory

open class SimpleTortoiseMemory : TortoiseMemory {

    val m = mutableMapOf<String, Double>()

    override fun value(variable: String, defaultValue: Double): Double {
        if (variable.isNotEmpty()) {
            val d = variable.toDoubleOrNull()
            if (d != null)
                return d

            return when (variable[0]) {
                '-' -> -value(variable.drop(1), defaultValue)
                '+' -> value(variable.drop(1), defaultValue)
                '=' -> calculate(variable.drop(1), defaultValue)
                else -> m[variable] ?: defaultValue
            }
        }
        return defaultValue
    }

    private fun calculate(variables: String, defaultValue: Double): Double {
        var com = '+'
        var varName = ""
        var sum = 0.0
        for (i in variables.indices) {
            val c = variables[i]
            when (c) {
                '+',
                '-',
                '*',
                '/' -> {
                    if (varName.isEmpty()) {
                        if (c == '+' || c == '-') {
                            varName += c
                        } else {
                        }


                    } else {
                        val e = value(varName, defaultValue)

                        when (com) {
                            '+' -> sum += e
                            '-' -> sum -= e
                            '*' -> sum *= e
                            '/' -> sum /= e
                            else -> {}
                        }
                        com = c
                        varName = ""
                    }
                }

                '=' -> {}
                else -> {
                    varName += c
                }
            }
        }

        if (varName.isNotEmpty()) {
            val e = value(varName, defaultValue)
            when (com) {
                '+' -> sum += e
                '-' -> sum -= e
                '*' -> sum *= e
                '/' -> sum /= e
                else -> {}
            }
        }
        return sum
    }


    override fun assign(variable: String, value: Double) {
        m[variable] = value
    }

    override fun clear(variable: String) {
        m.remove(variable)
    }

    override fun reset() {
        m.clear()
    }
}