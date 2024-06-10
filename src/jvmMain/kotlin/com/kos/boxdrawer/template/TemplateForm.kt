package com.kos.boxdrawer.template

import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

interface TemplateItem {
    fun print(): TortoiseParserStackItem

    val title: String
    val argumentName: String
    val argumentCount: Int
}

data class TemplateForm(
    override val title: String,
    override val argumentName: String,
    val list: List<TemplateItem>,
) : TemplateItem {
    fun isEmpty(): Boolean = list.isEmpty()

    override fun print(): TortoiseParserStackItem {
        val inner = TortoiseParserStackBlock('(', "items", list.map { it.print() })
        val tp = TortoiseParserStackBlock()
        tp.add("form")
        tp.add("title", "[$title]")
        tp.add("arg", argumentName)
        tp.add(inner)
        return tp
    }

    fun remove(arg: String): TemplateForm {
        val a = arg.split(".")

        fun removeInner(b: List<String>, form: TemplateForm): TemplateForm {
            if (b.isEmpty())
                return form

            val ar = b.first()
            val next = b.drop(1)
            return form.copy(
                list = form.list.mapNotNull { f ->
                    if (f.argumentName == ar) {
                        if (f is TemplateForm) {
                            if (next.isEmpty()) {
                                null
                            } else {
                                removeInner(next, f)
                            }
                        } else {
                            null
                        }
                    } else {
                        f
                    }
                }
            )
        }

        return removeInner(a.drop(1), this)
    }


    fun replace(arg: String, item: TemplateItem): TemplateForm {
        val a = arg.split(".").drop(1)
        //      println("Start insert $a from ${this.argumentName} and ${item.argumentName}")

        var inserted = false

        fun insertItem(b: List<String>, item: TemplateItem): TemplateItem? {
            //    println("insert $inserted $b")
            if (inserted)
                return null

            inserted = true
            val br = b.reversed()
            var ins = item

            br.forEach { ar ->
                ins = TemplateForm(
                    title = "",
                    argumentName = ar,
                    list = listOf(ins),
                )
            }
            return ins
        }

        fun removeInner(b: List<String>, form: TemplateForm): TemplateForm {
            if (b.isEmpty() || inserted)
                return form

            val ar = b.first()
            val next = b.drop(1)
            //  println("remove $ar from ${form.argumentName}")
            return form.copy(
                list = form.list.mapNotNull { f ->
                    //     println("check $ar with ${f.argumentName}")
                    if (f.argumentName == ar) {
                        if (f is TemplateForm) {
                            if (next.isEmpty()) {
                                insertItem(b.dropLast(1), item)
                            } else {
                                removeInner(next, f)
                            }
                        } else {
                            insertItem(b.dropLast(1), item)
                        }
                    } else {
                        f
                    }
                } + listOfNotNull(insertItem(b.dropLast(1), item))
            )
        }

        val t = removeInner(a, this)
        return t.copy(list = t.list)
    }

    override val argumentCount: Int
        get() = 1
}

data class TemplateItemMulti(
    override val title: String,
    override val argumentName: String,
    val data: TemplateItem,
) : TemplateItem {

    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val inner = TortoiseParserStackBlock('(', "item", listOf(data.print()))
        val tp = TortoiseParserStackBlock()
        tp.add("multi")
        tp.add("title", "[$title]")
        tp.add("arg", argumentName)
        tp.add(inner)
        return tp
    }
}

data class TemplateItemInt(
    override val title: String,
    override val argumentName: String
) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("int")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemNumeric(
    override val title: String,
    override val argumentName: String
) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("1")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemAngle(
    override val title: String,
    override val argumentName: String
) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("angle")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemSize(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 2

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("2")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemTriple(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 3

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("3")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemRect(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 4

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("4")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemCheck(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("check")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemString(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 1

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("text")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}

data class TemplateItemLabel(
    override val title: String,
    override val argumentName: String

) : TemplateItem {
    override val argumentCount: Int
        get() = 0

    override fun print(): TortoiseParserStackItem {
        val tp = TortoiseParserStackBlock()
        tp.add("label")
        tp.add(argumentName)
        tp.add("[$title]")
        return tp
    }
}