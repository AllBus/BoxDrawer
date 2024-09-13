package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.LabelLight
import com.kos.boxdrawer.template.TemplateItemLabel

@Composable
fun TemplateLabelBox(form: TemplateItemLabel) {
    Row() {
        LabelLight(
            form.title,
            singleLine = true,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}