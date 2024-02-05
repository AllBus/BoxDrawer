package turtoise

import turtoise.memory.TortoiseMemory

class SimpleTortoiseMemory: TortoiseMemory {

    val m = mutableMapOf<String, Double>()

    override fun value(variable: String, defaultValue: Double): Double {
        if (variable.isNotEmpty()){
            val d = variable.toDoubleOrNull()
            if (d != null)
                return d

            return when (variable[0]){
                '-' ->  -(m[variable] ?: defaultValue)
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
        for(i in variables.indices){
            val c = variables[i]
            when (c){
                '+',
                '-',
                '*',
                '/' -> {
                    val d = varName.toDoubleOrNull()
                    val e =if (d != null){
                        d
                    }else{
                        m[varName]?: return@calculate defaultValue
                    }

                    when(com){
                        '+' -> sum+=e
                        '-' -> sum-=e
                        '*' -> sum*=e
                        '/' -> sum/=e
                        else -> {}
                    }
                    com = c
                    varName=""
                }
                '=' -> {}
                else -> { varName += c }
            }
        }

        val d = varName.toDoubleOrNull()
        val e =if (d != null){
            d
        }else{
            m[varName]?: return@calculate defaultValue
        }
        when(com){
            '+' -> sum+=e
            '-' -> sum-=e
            '*' -> sum*=e
            '/' -> sum/=e
            else -> {}
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