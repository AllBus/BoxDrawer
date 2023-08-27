package figure

import vectors.Vec2
import vectors.Vec2.Companion.calcXPosition
import vectors.Vec2.Companion.calcYPosition
import vectors.Vec2.Companion.coordForX
import vectors.Vec2.Companion.coordForY

class FigurePolyline(points: List<Vec2>) : FigurePolygon(points) {
    override fun create(points: List<Vec2>): FigurePolygon {
        return FigurePolyline(points)
    }

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        if (points.size < 2) {
            return Empty
        }
        return cropPolyline(k, cropSide);
    }


    fun cropPolyline(k: Double, cropSide: CropSide): IFigure {


        val figures = mutableListOf<IFigure>()
        var result = mutableListOf<Vec2>()

        fun saveFigure() {
            if (result.size >= 2) {
                figures.add(
                    if (result.size == 2)
                        FigureLine(result[0], result[1])
                    else
                        FigurePolyline(result.toList())
                )
            }
            result = mutableListOf<Vec2>()
        }

        fun crops(predicate: (Vec2, Double) -> Boolean,  coord: (Vec2, Vec2, Double) -> Vec2){
            var a = points.first();
            var predV =predicate(a,k)

            for (b in points) {
                if (predicate(b,k)) {
                    if (!predV) {
                        result.add(coord(a, b, k))
                    }
                    result.add(b);
                    predV = true
                } else {
                    if (predV) {
                        result.add(coord(a, b, k))
                        saveFigure()
                        predV = false
                    }
                }
                a = b;
            }
            saveFigure()
        }

        when (cropSide) {
            CropSide.LEFT ->
                crops( {a, x -> a.x > x}, ::coordForX)
            CropSide.RIGHT ->
                crops( {a, x -> a.x < x}, ::coordForX)
            CropSide.BOTTOM ->
                crops( {a, y -> a.y > y}, ::coordForY)
            CropSide.TOP ->
                crops( {a, y -> a.y < y}, ::coordForY)
        }//end when


        return if (figures.size == 1){
            figures.first()
        }else{
            FigureList(figures.toList())
        }
    }
}