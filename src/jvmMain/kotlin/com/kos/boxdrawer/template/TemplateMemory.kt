package com.kos.boxdrawer.template

import turtoise.TurtoiseParserStackBlock

class TemplateMemory(){
    private val memory = mutableMapOf<String, TemplateMemoryItem>()

    constructor(defaultMemory: Map<String, TemplateMemoryItem>) :this(){
        defaultMemory.forEach { (k, value) ->
            put(k, value)
        }
    }

    fun memoryBlock(
        top: TurtoiseParserStackBlock,
    ): TurtoiseParserStackBlock {

        memory.forEach { (k, value) ->
            val sp = k.split('.').drop(1)
            var p = top
            for (c in sp) {
                val b = p.getBlockAtName(c)
                if (b == null) {
                    val nb = TurtoiseParserStackBlock('(')
                    nb.add(c)
                    p.add(nb)
                    p = nb
                } else
                    p = b
            }
            value.values.forEach { vv ->
                p.add(vv)
            }
        }
        return top
    }

    fun clear() {
        memory.clear()
    }

    fun put(arg: String, templateMemoryItem: TemplateMemoryItem) {
        memory.put(arg, templateMemoryItem)
    }

    fun put(arg: String, value: String) {
        memory.put(arg, TemplateMemoryItem(listOf(value)))
    }

    fun put(arg: String, index: Int, count: Int, value: String){
        memory.put(
            arg,
            (memory.get(arg) ?: TemplateMemoryItem(ArrayList<String>(count))).update(
                index,
                value
            )
        )
    }

    fun union(other: TemplateMemory):TemplateMemory{
        val nm = TemplateMemory()
        other.memory.forEach { (k, value) ->
            nm.put(k, value)
        }
        memory.forEach { (k, value) ->
            val r =nm.memory.get(k)?.let{ pred ->
                pred.merge(value)
            }?: value

            nm.put(k, r)
        }
        return nm
    }
}