package turtoise.memory

import turtoise.memory.keys.ICalculatorMemoryKey
import turtoise.memory.keys.MemoryKey

open class SimpleTortoiseMemory : TortoiseMemory {

    val m = mutableMapOf<MemoryKey, Double>()

    override fun value(variable: MemoryKey, defaultValue: Double): Double {
        if (variable.isNotEmpty()) {
            val d = variable.toDoubleOrNull()
            if (d != null)
                return d

            if (variable.isCalculator()){
                return calculateCalculator(variable, defaultValue)
            }

            return when (variable.prefix()) {
                '-' -> -value(variable.drop(), defaultValue)
                '+' -> value(variable.drop(), defaultValue)
                '=' -> calculate(variable.drop(), defaultValue)
                else -> m[variable] ?: defaultValue
            }
        }
        return defaultValue
    }

    private fun calculate(variables: MemoryKey, defaultValue: Double): Double {
        if (variables.isCalculator()){
            return calculateCalculator(variables, defaultValue)
        }
        var com = '+'
        var varName = ""
        var sum = 0.0
        for (i in variables.name.indices) {
            val c = variables.name[i]
            when (c) {
                '+',
                '-',
                '*',
                '/' -> {
                    if (varName.isEmpty()) {
                        if (c == '+' || c == '-') {
                            varName += c
                        }
                    } else {
                        val e = value(MemoryKey(varName), defaultValue)

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
            val e = value(MemoryKey(varName), defaultValue)
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

    private fun calculateCalculator(variable: MemoryKey, defaultValue: Double): Double {
        if (variable is ICalculatorMemoryKey) {
            return variable.calculate { v -> value(v, defaultValue) }
        } else
            return defaultValue
    }

    override fun assign(variable: MemoryKey, value: Double) {
        m[variable] = value
    }

    override fun clear(variable: MemoryKey) {
        m.remove(variable)
    }

    override fun reset() {
        m.clear()
    }
}

