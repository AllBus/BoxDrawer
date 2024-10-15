package com.kos.boxdrawe.widget

import androidx.compose.runtime.*
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import java.awt.FileDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.rememberDialogState
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

@Composable
fun FileDialog(
    title: String = "Open File",
    initialDirectory: String? = null,
    parent: Frame? = null,
    onCloseRequest: (String?) -> Unit
) = AwtWindow(
    create = {
        val dialog = object : FileDialog(/* parent = */ parent, /* title = */ title, /* mode = */ LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    file?.let { file ->
                        onCloseRequest(File(directory, file).absolutePath)
                    }
                }
            }
        }

        dialog.directory = initialDirectory
        dialog
    },
    dispose = FileDialog::dispose
)

@Composable
fun SaveFileDialog(
    title: String = "Save File",
    initialDirectory: String? = null,
    parent: Frame? = null,
    onCloseRequest: (String?) -> Unit
) = AwtWindow(
    create = {
        val dialog = object : FileDialog(/* parent = */ parent, /* title = */ title, /* mode = */ SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    file?.let { file ->
                        onCloseRequest(File(directory, file).absolutePath)
                    }
                }
            }
        }

        dialog.directory = initialDirectory
        dialog
    },
    dispose = FileDialog::dispose
)


@Composable
fun FileChooserForSingleFile(
    title: String = "Open File",
    initialDirectory: String? = null,
    fileFilter: FilenameFilter? = null,
    isDialogVisible: MutableState<Boolean>,
    onFileSelected: (File) -> Unit,
) {
    AwtWindow(
        visible = isDialogVisible.value,
        create = {
            val fileDialog = FileDialog(null as? Frame, title)
            fileDialog.isVisible = true
            fileDialog.directory = initialDirectory
            fileDialog.filenameFilter = fileFilter
            fileDialog
        },
        dispose = { fileDialog ->
            val file = if (fileDialog.file != null) {
                File(fileDialog.directory, fileDialog.file)
            } else {
                null
            }

            isDialogVisible.value = false
            if (file!= null)
                onFileSelected(file)
        }
    )
}