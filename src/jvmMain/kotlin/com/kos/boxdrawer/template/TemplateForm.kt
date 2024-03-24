package com.kos.boxdrawer.template

interface TemplateItem{
    val title:String
    val argumentName:String

    val argumentCount : Int
}

data class TemplateForm(override val title: String,
                   override val argumentName: String,
                   val list: List<TemplateItem>,
): TemplateItem{
    fun isEmpty() : Boolean = list.isEmpty()

    override val argumentCount: Int
        get() = 1
}

data class TemplateItemMulti(override val title: String,
                        override val argumentName: String,
                        val data: TemplateItem,
): TemplateItem{

    override val argumentCount: Int
        get() = 1
}

data class TemplateItemInt(
    override val title: String,
    override val argumentName: String
) : TemplateItem{
    override val argumentCount: Int
        get() = 1
}

data class TemplateItemNumeric(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 1
}

data class TemplateItemSize(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 2
}

data class TemplateItemTriple(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 3
}

data class TemplateItemRect(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 4
}

data class TemplateItemCheck(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 1
}

data class TemplateItemString(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 1
}

data class TemplateItemLabel(
    override val title: String,
    override val argumentName: String

) : TemplateItem{
    override val argumentCount: Int
        get() = 0
}