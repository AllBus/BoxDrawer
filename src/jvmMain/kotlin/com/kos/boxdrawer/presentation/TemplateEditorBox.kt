package com.kos.boxdrawer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.editor.TemplateEditorForm

@Composable
fun TemplateEditorBox(
    menu: State<TemplateInfo>,
    templateGenerator: TemplateGeneratorListener,
) {

    Column {
        TemplateBox(
            modifier = Modifier.padding(8.dp)
                .width(250.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            menu = menu,
            templateGenerator = templateGenerator,
        )

//        if (menu.value.edit){
//
//        }
    }

    if (menu.value.edit) {

        val createItem = templateGenerator::editorAddItem

        val name = remember { mutableStateOf("") }
        val argument = remember { mutableStateOf("") }

        Row {
            Column(modifier = Modifier.width(100.dp)) {
                RunButton("1 число") {
                    createItem("1", name.value, argument.value)
                }
                RunButton("2 числа") {
                    createItem("2", name.value, argument.value)
                }
                RunButton("3 числа") {
                    createItem("3", name.value, argument.value)
                }
                RunButton("4 числа") {
                    createItem("4", name.value, argument.value)
                }
                RunButton("1 целое") {
                    createItem("int", name.value, argument.value)
                }
                RunButton("галочка") {
                    createItem("check", name.value, argument.value)
                }
                RunButton("текст") {
                    createItem("string", name.value, argument.value)
                }
                RunButton("надпись") {
                    createItem("label", name.value, argument.value)
                }
                RunButton("форма") {
                    createItem("form", name.value, argument.value)
                }
                RunButton("множество") {
                    createItem("multi", name.value, argument.value)
                }
            }
            Column(modifier = Modifier.width(300.dp)) {
                SimpleEditText("Название", "", name) { v ->
                    name.value = v
                }
                SimpleEditText("Аргумент", "", argument) { v ->
                    argument.value = v
                }
            }
        }
    }
}

