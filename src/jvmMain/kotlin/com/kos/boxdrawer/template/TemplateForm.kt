package com.kos.boxdrawer.template

interface TemplateItem{
    val title:String
    val argumentName:String
}

class TemplateForm(override val title: String,
                   override val argumentName: String,
                   val list: List<TemplateItem>,
): TemplateItem{
    fun isEmpty() : Boolean = list.isEmpty()
}

class TemplateItemInt(
    override val title: String,
    override val argumentName: String
) : TemplateItem

class TemplateItemNumeric(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemSize(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemTriple(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemRect(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemCheck(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemString(
    override val title: String,
    override val argumentName: String

) : TemplateItem

class TemplateItemLabel(
    override val title: String,
    override val argumentName: String

) : TemplateItem