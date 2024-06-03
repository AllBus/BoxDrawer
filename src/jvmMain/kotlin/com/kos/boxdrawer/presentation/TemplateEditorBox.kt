package com.kos.boxdrawer.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.editor.TemplateEditorForm
import kotlin.reflect.KFunction3

@Composable
fun TemplateEditorBox(
    menu: State<TemplateInfo>,
    figureLine: MutableState<String>,
    figureName: MutableState<String>,
    templateGenerator: TemplateGeneratorListener,
) {

    Column(
    ) {

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

    Column {
        AnimatedVisibility(
            menu.value.edit,
        ) {

            val createItem = templateGenerator::editorAddItem
            val name = remember { mutableStateOf("") }
            val argument = remember { mutableStateOf("") }

            Row(
                Modifier.background(ThemeColors.editorBackground, ThemeColors.figureListItemShape)
            ) {
                TempleteEditorActionMenu(createItem, name, argument, figureLine, figureName)
            }
        }
    }
}

@Composable
private fun TempleteEditorActionMenu(
    createItem: KFunction3<String, String, String, Unit>,
    name: MutableState<String>,
    argument: MutableState<String>,
    figureLine: MutableState<String>,
    figureName: MutableState<String>,
) {
    Column(modifier = Modifier.width(200.dp)) {
        Label("Ввод чисел")
        Row {
            RunButton("1", Modifier.weight(1f)) {
                createItem("1", name.value, argument.value)
            }
            RunButton("2", Modifier.weight(1f)) {
                createItem("2", name.value, argument.value)
            }
            RunButton("3", Modifier.weight(1f)) {
                createItem("3", name.value, argument.value)
            }
            RunButton("4", Modifier.weight(1f)) {
                createItem("4", name.value, argument.value)
            }
        }
        Row {
            RunButton("целое", Modifier.weight(1f)) {
                createItem("int", name.value, argument.value)
            }
            RunButton("галка", Modifier.weight(1f)) {
                createItem("check", name.value, argument.value)
            }
        }
        Row {
            RunButton("текст", Modifier.weight(1f)) {
                createItem("string", name.value, argument.value)
            }
            RunButton("надпись", Modifier.weight(1f)) {
                createItem("label", name.value, argument.value)
            }
        }
//                RunButton("форма") {
//                    createItem("form", name.value, argument.value)
//                }
//                RunButton("множество") {
//                    createItem("multi", name.value, argument.value)
//                }
    }
    Column(modifier = Modifier.width(300.dp)) {
        Label("Новое поле:")
        SimpleEditText("Название", "", name) { v ->
            name.value = v
        }
        SimpleEditText("Аргумент", "", argument) { v ->
            argument.value = v
        }
        Spacer(modifier = Modifier.height(16.dp))
        Label("Рисование:")
        SimpleEditText("Название", "", figureName) { v ->
            figureName.value = v
        }
        SimpleEditText("Фигура", "", figureLine) { v ->
            figureLine.value = v
        }
    }
}

