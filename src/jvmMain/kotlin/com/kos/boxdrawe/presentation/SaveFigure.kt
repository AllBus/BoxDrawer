package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.FigureTransferable
import com.kos.figure.IFigure
import java.awt.datatransfer.Transferable
import java.io.File

interface SaveFigure {

    suspend fun createFigure():IFigure

    val tools: ITools

    suspend  fun save(fileName: String) {
        tools.saveFigures(File(fileName), createFigure())
        tools.updateChooserDir(fileName)
    }

    suspend fun copy(): Transferable {
        val tmp = File.createTempFile("boxDrawerFigure",".dxf")
        tools.saveFigures(tmp, createFigure())
        return FigureTransferable(tmp)
    }
}