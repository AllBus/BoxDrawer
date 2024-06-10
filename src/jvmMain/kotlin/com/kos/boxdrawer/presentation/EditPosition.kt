package com.kos.boxdrawer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItemAngle
import com.kos.boxdrawer.template.TemplateItemNumeric
import com.kos.boxdrawer.template.TemplateItemSize

@Composable
fun EditPosition(listener: TemplateGeneratorSimpleListener) {
    Column(
    ) {
        Label("Добавить перемещение")
        TemplateSizeBox(
            TemplateItemSize("x y", "xy"),
            null, "xy",
            listener,
        )
        TemplateAngleBox(
            TemplateItemAngle("a", "a"),
            null, "a",
            listener,
        )
        TemplateSizeBox(
            TemplateItemSize("x y", "axy"),
            null, "axy",
            listener,
        )
    }
}