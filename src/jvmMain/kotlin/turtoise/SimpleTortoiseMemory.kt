package turtoise

class SimpleTortoiseMemory: TortoiseMemory {

    val m = mutableMapOf<String, Double>()

    override fun value(variable: String, defaultValue: Double): Double {
        val d = variable.toDoubleOrNull()
        if (d != null)
            return d

        return m[variable] ?: defaultValue
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