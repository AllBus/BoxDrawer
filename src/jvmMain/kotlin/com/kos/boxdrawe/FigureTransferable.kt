package com.kos.boxdrawe

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File

class FigureTransferable(val file: File) : Transferable {
    val supported = arrayOf(
        DataFlavor.javaFileListFlavor,
//        DataFlavor("application/dxf; charset=unicode; class=java.lang.String", "DXF")
    )


    override fun getTransferDataFlavors(): Array<DataFlavor> = supported

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        val flavors = getTransferDataFlavors()
        for (i in flavors.indices) {
            if (flavors[i].equals(flavor)) {
                return true
            }
        }
        return false
    }

    override fun getTransferData(flavor: DataFlavor): List<File> {
        return listOf(file)
    }
}