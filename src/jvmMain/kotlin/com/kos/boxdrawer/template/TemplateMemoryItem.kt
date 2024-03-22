package com.kos.boxdrawer.template

class TemplateMemoryItem(val values: List<String>) {
    fun update(index: Int, value: String): TemplateMemoryItem {
        val s = if (index > values.size) index else values.size
        val r = List<String>(s) { i ->
            if (i + 1 == index) {
                value
            } else
                values.getOrNull(i) ?: "0.0"
        }
        return TemplateMemoryItem(r)
    }
}