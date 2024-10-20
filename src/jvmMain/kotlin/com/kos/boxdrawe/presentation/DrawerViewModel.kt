package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.corutine.DispatcherList
import com.kos.boxdrawe.presentation.ImageUtils.bufferedImageToImageBitmap
import com.kos.boxdrawe.presentation.ImageUtils.collectImages
import com.kos.boxdrawe.presentation.ImageUtils.formatOfData
import com.kos.boxdrawe.presentation.ImageUtils.loadImageFromFile
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.model.ImageMap
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import vectors.Vec2
import java.awt.datatransfer.Transferable

@OptIn(ExperimentalCoroutinesApi::class)
class DrawerViewModel {

    private val noneFigure = MutableStateFlow(FigureEmpty).asStateFlow()
    private val _selectedItem = MutableStateFlow(emptyList<FigureInfo>())

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools, tortoise)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val template = TemplateData(tools, _selectedItem)
    val options = ToolsData(tools, template)
    val bezier = BezierData(tools)
    val bublik = BublikData(tools)
    val rectData = RekaToolsData(tools)
    val dxfData = DxfToolsData(tools)
    val imageData =  ImageToolsData(tools)
    val formulaData =  FormulaData(tools)
    val tabIndex = MutableStateFlow(BoxDrawerToolBar.TAB_TORTOISE)
    val calculatorData = CalculatorData()

    val figures = tabIndex.flatMapLatest { tab ->
        when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.figures
            BoxDrawerToolBar.TAB_SOFT -> softRez.figures
            BoxDrawerToolBar.TAB_BOX -> box.figures
            BoxDrawerToolBar.TAB_BUBLIK -> bublik.figures
            BoxDrawerToolBar.TAB_REKA -> rectData.figures
            BoxDrawerToolBar.TAB_TOOLS -> template.currentFigure
            BoxDrawerToolBar.TAB_DXF -> dxfData.figures
            BoxDrawerToolBar.TAB_FORMULA -> formulaData.figures
            else -> noneFigure
        }
    }

    @OptIn(FlowPreview::class)
    val imagesList = figures.debounce(100L).mapLatest{ f ->
        collectImages(f).toList()
    }.distinctUntilChanged()

    private var previousImages : ImageMap = ImageMap.EMPTY
    val images = imagesList.mapLatest { imageFigures ->
        withContext(DispatcherList.IO) {
            var changeImgList: Boolean = false
            val r = imageFigures.mapNotNull { f ->
            //    println(f.uri)
                val pv = previousImages[f.uri]
                if (pv != null) {
                    f.uri to pv
                } else {
                    changeImgList = true
                    loadImageFromFile(f.uri)?.let {
                        bufferedImageToImageBitmap(
                            it,
                            formatOfData(f.uri, it)
                        )
                    }?.let {
                        f.uri to it
                    }
                }
            }.toMap().let(::ImageMap)
            if (changeImgList) {
                previousImages = r
            }
            r
        }
    }

    val helpInfoList = tools.helpInfoList

    val selectedItem: StateFlow<List<FigureInfo>> get() = _selectedItem

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val selectedItem: Flow<List<IFigure>> = tabIndex.flatMapLatest { tab ->
//        when (tab) {
//            //        BoxDrawerToolBar.TAB_TORTOISE ->
//            //        BoxDrawerToolBar.TAB_SOFT ->
//            //        BoxDrawerToolBar.TAB_BOX ->
//            //        BoxDrawerToolBar.TAB_BUBLIK ->
//            //        BoxDrawerToolBar.TAB_REKA ->
//            BoxDrawerToolBar.TAB_TOOLS -> template.selectedItem
//            BoxDrawerToolBar.TAB_DXF -> dxfData.selectedItem
//            else -> noneSelectedFigure
//        }
//    }

    suspend fun copy(): Transferable? {
        val tab = tabIndex.value
        val tf: SaveFigure? = modelAtTab(tab)
        return tf?.copy()
    }

    suspend fun save(fileName: String) {
        val tab = tabIndex.value
        tools.updateChooserDir(fileName)
        val tf: SaveFigure? = modelAtTab(tab)
        tf?.save(fileName)
    }

    fun modelAtTab(tab: Int): SaveFigure? {
        val tf: SaveFigure? = when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise
            BoxDrawerToolBar.TAB_SOFT -> softRez
            BoxDrawerToolBar.TAB_BOX -> box
            BoxDrawerToolBar.TAB_BUBLIK -> bublik
            BoxDrawerToolBar.TAB_REKA -> rectData
            BoxDrawerToolBar.TAB_TOOLS -> template
            BoxDrawerToolBar.TAB_BEZIER -> bezier
            BoxDrawerToolBar.TAB_GRID -> grid
            BoxDrawerToolBar.TAB_DXF -> dxfData
            else -> null
        }
        return tf
    }

    suspend fun print(): String {
        val tab = tabIndex.value

        return when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.printCommand()
            BoxDrawerToolBar.TAB_SOFT -> ""
            BoxDrawerToolBar.TAB_BOX -> box.print()
            BoxDrawerToolBar.TAB_BUBLIK -> ""
            BoxDrawerToolBar.TAB_REKA -> rectData.print()
            BoxDrawerToolBar.TAB_TOOLS -> template.print()
            BoxDrawerToolBar.TAB_BEZIER -> bezier.print()
            BoxDrawerToolBar.TAB_GRID -> grid.print()
            BoxDrawerToolBar.TAB_DXF -> dxfData.print()
            else -> ""
        }
    }

    init {
        println("DrawerViewModel")
    }

    fun loadSettings() {
        tools.loadState()
        options.selectSettings(tools.ds())
        tortoise.loadState()
     }

    fun saveState(){
        tortoise.saveState()
        tools.saveState()
    }

    suspend fun onPress(point: Vec2, button: Int, scale: Float) {
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_REKA -> rectData.onPress(point, button, scale)
            BoxDrawerToolBar.TAB_TOOLS -> template.onPress(point, button, scale)
            BoxDrawerToolBar.TAB_DXF -> dxfData.onPress(point, button, scale, _selectedItem)
        }
    }

    suspend fun onMove(point: Vec2, button: Int, scale: Float) {
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_DXF -> dxfData.onMove(point, button, scale, _selectedItem)
        }
    }

    suspend fun onRelease(point: Vec2, button: Int, scale: Float) {
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_DXF -> dxfData.onRelease(point, button, scale, _selectedItem)
        }
    }

    fun setSelected(figures: List<FigureInfo>) {
        _selectedItem.value = figures
    }
}

