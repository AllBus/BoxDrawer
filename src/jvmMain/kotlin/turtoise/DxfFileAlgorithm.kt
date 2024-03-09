package turtoise

import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.memory.TortoiseMemory
import vectors.Vec2
import java.io.File
import java.io.FileInputStream

class DxfFileAlgorithm(
    val fileName: String
) : TortoiseAlgorithm {

    private val figure: IFigure = try {
        val f = File(fileName)
        if (f.isFile()) {
            val parser = ParserBuilder.createDefaultParser()

            parser.parse(FileInputStream(f), DXFParser.DEFAULT_ENCODING)
            val doc: DXFDocument = parser.getDocument()

            val extractor = FigureExtractor()
            extractor.extractFigures(doc)
        } else {
            FigureEmpty
        }
    } catch (e: Exception) {
        e.printStackTrace()
        FigureEmpty
    }


    private val _names = listOf("_")
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String>
        get() = _names

    override fun draw(
        name: String,
        ds: DrawerSettings,
        state: TortoiseState,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
        maxStackSize: Int
    ): IFigure {
        return FigureTranslate(
            FigureRotate(
                figure,
                state.a,
                Vec2.Zero
            ),
            state.xy
        )
    }

    companion object {

        fun help(): AnnotatedString {
            val sb = AnnotatedString.Builder()
            sb.append(TortoiseParser.helpTitle("Рисование содержимое dxf файла"))
            sb.appendLine()
            sb.append(
                TortoiseParser.helpName(
                    "dxf@",
                    "(path/file.dxf)",
                    "Название файла внути скобок"
                )
            )
            sb.appendLine()

            return sb.toAnnotatedString()
        }
    }
}