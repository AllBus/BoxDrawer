package turtoise.example

import com.kos.figure.CropSide
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import vectors.Vec2

object FigureExample {

    fun cropTest(): IFigure {

        val dr = FigureList(listOf(
            FigureEllipse(Vec2(0.0, 0.0), 50.0, 40.0, 0.0),
            FigureEllipse(Vec2(120.0, 0.0), 50.0, 40.0, 0.0).crop(120-50+100*0.2, CropSide.LEFT),
            FigureEllipse(Vec2(240.0, 0.0), 50.0, 40.0, 0.0).crop(240-50+100*0.4, CropSide.LEFT).crop(100*0.4, CropSide.TOP),
            FigureEllipse(Vec2(360.0, 0.0), 50.0, 40.0, 0.0).crop(360-50+100*0.6, CropSide.LEFT),
            FigureEllipse(Vec2(480.0, 0.0), 50.0, 40.0, 0.0).crop(480-50+100*0.8, CropSide.LEFT),
            FigureEllipse(Vec2(600.0, 0.0), 50.0, 40.0, 0.0).crop(600-50+100*0.9, CropSide.LEFT),
            FigurePolyline(listOf(Vec2(70.0, -50.0), Vec2(170.0, -50.0), Vec2(170.0, 50.0), Vec2(70.0, 50.0)), true).crop(120-50+100*0.2,
                CropSide.LEFT),

            FigureEllipse(Vec2(120.0, 120.0), 50.0, 40.0, 0.0).crop(120-50+100*0.2, CropSide.TOP),
            FigureEllipse(Vec2(240.0, 120.0), 50.0, 40.0, 0.0).crop(120-50+100*0.4, CropSide.TOP),
            FigureEllipse(Vec2(360.0, 120.0), 50.0, 40.0, 0.0).crop(120-50+100*0.6, CropSide.TOP),
            FigureEllipse(Vec2(480.0, 120.0), 50.0, 40.0, 0.0).crop(120-50+100*0.8, CropSide.TOP),
            FigureEllipse(Vec2(600.0, 120.0), 50.0, 40.0, 0.0).crop(120-50+100*0.9, CropSide.TOP),
            FigurePolyline(listOf(Vec2(70.0, -50.0+120), Vec2(170.0, -50.0+120), Vec2(170.0, 50.0+120), Vec2(70.0, 50.0+120)), true).crop(120-50+100*0.2,
                CropSide.TOP),

            FigureCircle(Vec2(120.0, 240.0), 50.0).crop(120-50+100*0.2, CropSide.RIGHT),
            FigureCircle(Vec2(240.0, 240.0), 50.0).crop(240-50+100*0.4, CropSide.RIGHT),
            FigureCircle(Vec2(360.0, 240.0), 50.0).crop(360-50+100*0.6, CropSide.RIGHT),
            FigureCircle(Vec2(480.0, 240.0), 50.0).crop(480-50+100*0.8, CropSide.RIGHT),
            FigureCircle(Vec2(600.0, 240.0), 50.0).crop(600-50+100*0.9, CropSide.RIGHT),
            FigurePolyline(listOf(Vec2(70.0, -50.0+240), Vec2(170.0, -50.0+240), Vec2(170.0, 50.0+240), Vec2(70.0, 50.0+240)), true).crop(120-50+100*0.2,
                CropSide.RIGHT),

            FigureCircle(Vec2(120.0, 360.0), 50.0).crop(360-50+100*0.2, CropSide.BOTTOM),
            FigureCircle(Vec2(240.0, 360.0), 50.0).crop(360-50+100*0.4, CropSide.BOTTOM),
            FigureCircle(Vec2(360.0, 360.0), 50.0).crop(360-50+100*0.6, CropSide.BOTTOM),
            FigureCircle(Vec2(480.0, 360.0), 50.0).crop(360-50+100*0.8, CropSide.BOTTOM),
            FigureCircle(Vec2(600.0, 360.0), 50.0).crop(360-50+100*0.9, CropSide.BOTTOM),
            FigurePolyline(listOf(Vec2(70.0, -50.0+360), Vec2(170.0, -50.0+360), Vec2(170.0, 50.0+360), Vec2(70.0, 50.0+360)), true).crop(360-50+100*0.2,
                CropSide.BOTTOM),
        ))
        return dr
    }
}