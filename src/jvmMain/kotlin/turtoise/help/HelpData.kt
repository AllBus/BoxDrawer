package turtoise.help

import androidx.compose.runtime.Immutable
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.editor.TemplateField

@Immutable
data class HelpData(
    val argument: String,
    val description: String,
    val params: List<HelpDataParam> = emptyList(),
    val creator : TemplateForm? = null,
)

@Immutable
data class HelpDataParam(
    val name: String,
    val description: String,
    val kind: String = TemplateField.FIELD_1,
    val variants : List<String>? = null,
)