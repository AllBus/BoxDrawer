package com.kos.boxdrawer.template

class TemplateMemoryItem(val values: List<String>) {
    fun update(index: Int, value: String): TemplateMemoryItem {
        val s = if (index > values.size) index else values.size
        val r = List<String>(s) { i ->
            if (i + 1 == index) {
                value
            } else
                values.getOrNull(i) ?: "+"
        }
        return TemplateMemoryItem(r)
    }

    fun merge(other: TemplateMemoryItem):TemplateMemoryItem {
        if (values.size <= 1)
            return other

        val nm = other.values.mapIndexed{ i, v ->
            if (v.isEmpty() || v =="+")
                values.getOrNull(i)?:"+"
            else
                v
        }

        return TemplateMemoryItem(nm)
    }
}

