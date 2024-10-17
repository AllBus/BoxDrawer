package com.kos.boxdrawer.detal.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerButton
import com.kos.boxdrawer.presentation.display.colorList
import org.kabeja.dxf.helpers.Edge
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Grid3DVisualizer(edges: List<PolygonGroup>, rotationX: Float, rotationY: Float, rotationZ: Float, event: Int, current : Coordinates) {
    val cur = remember(current) {
        with(current) {
            setOf(
                KubikEdge(current, Coordinates(x + 1, y, z)),
                KubikEdge(current, Coordinates(x, y + 1, z)),
                KubikEdge(current, Coordinates(x, y, z + 1)),
                KubikEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y + 1, z)),
                KubikEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y, z + 1)),
                KubikEdge(Coordinates(x, y + 1, z), Coordinates(x + 1, y + 1, z)),
                KubikEdge(Coordinates(x, y + 1, z), Coordinates(x, y + 1, z + 1)),
                KubikEdge(Coordinates(x, y, z + 1), Coordinates(x + 1, y, z + 1)),
                KubikEdge(Coordinates(x, y, z + 1), Coordinates(x, y + 1, z + 1)),
                KubikEdge(Coordinates(x + 1, y + 1, z), Coordinates(x + 1, y + 1, z + 1)),
                KubikEdge(Coordinates(x + 1, y, z + 1), Coordinates(x + 1, y + 1, z + 1)),
                KubikEdge(Coordinates(x, y + 1, z + 1), Coordinates(x + 1, y + 1, z + 1))
            )
        }
    }

    val move = remember {  mutableStateOf(Offset(0f, 0f)) }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onDrag(
        matcher = PointerMatcher.Primary + PointerMatcher.mouse(PointerButton.Secondary) +
                PointerMatcher.mouse(PointerButton.Tertiary ) + PointerMatcher.stylus,
        onDrag = { offset ->
            move.value += offset
        }
    )) {

        val m = Matrix()
        m.rotateX(rotationX)
        m.rotateY(rotationY)
        m.rotateZ(rotationZ)


            drawLongEdges(cur, Color.Red, m, move.value)

            for (e in edges) {

                val c = colorList[e.kubik.color % colorList.size]
                for (s in e.polygons) {
                    drawPolygons(s, c , m, move.value)
                }
            }


    }
}


fun DrawScope.drawPolygons(
    polygon: Polygon,
    color: Color,
    rotationMatrix:Matrix,
    move:Offset,
) {

    val cubeSize = 50f
    val spacing = 0f


    val path = Path()
    val rotatedVertices = polygon.vertices.map { rotatePoint(it, rotationMatrix) }

    val start = rotatedVertices.first()
    val startX = start.x * (cubeSize + spacing)+move.x
    val startY = start.y * (cubeSize + spacing)+move.y
    path.moveTo(startX, startY)

    for (vertex in rotatedVertices.drop(1)) {
        val x = vertex.x * (cubeSize + spacing)+move.x
        val y = vertex.y * (cubeSize + spacing)+move.y
        path.lineTo(x, y)
    }

  //  path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2f)
    )


}

fun rotatePoint(start : Coordinates, m:Matrix): Offset {
    return Offset( (start.x*m[0,0]+ start.y*m[1,0]+start.z*m[2,0]),
     (start.x*m[0,1]+ start.y*m[1,1]+start.z*m[2,1])
    )
}

fun DrawScope.drawLongEdges(longEdges: Set<KubikEdge>,
                            color: Color,
                            m:Matrix,
                            move: Offset,
                            ) {
    val cubeSize = 50f

    for (edge in longEdges) {

        val start = edge.start
        val end = edge.end
        val startX = (start.x*m[0,0]+ start.y*m[1,0]+start.z*m[2,0])*cubeSize+move.x  // start.x * (cubeSize + spacing)
        val startY = (start.x*m[0,1]+ start.y*m[1,1]+start.z*m[2,1])*cubeSize+move.y
        val endX = (end.x*m[0,0]+ end.y*m[1,0]+end.z*m[2,0])*cubeSize+move.x   // start.x * (cubeSize + spacing)
        val endY = (end.x*m[0,1]+ end.y*m[1,1]+end.z*m[2,1])*cubeSize+move.y

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawCube(
    x: Int,
    y: Int,
    z: Int,
    cubeSize: Float,
    spacing: Float,
    color: Color
) {
    val startX = x * (cubeSize + spacing)
    val startY = y * (cubeSize + spacing)
    val startZ = z * (cubeSize + spacing)

    // Draw the front face
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(startX + cubeSize, startY)
    )
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(startX, startY + cubeSize)
    )
    drawLine(
        color = color,
        start = Offset(startX + cubeSize, startY),
        end = Offset(startX + cubeSize, startY + cubeSize)
    )
    drawLine(
        color = color,
        start = Offset(startX, startY +cubeSize),
        end = Offset(startX + cubeSize, startY + cubeSize)
    )

    // Draw the back face (offset by z)
    drawLine(
        color = color,
        start = Offset(startX + startZ, startY + startZ),
        end = Offset(startX + cubeSize + startZ, startY + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX + startZ, startY + startZ),
        end = Offset(startX + startZ, startY + cubeSize + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX + cubeSize + startZ, startY + startZ),
        end = Offset(startX + cubeSize + startZ, startY + cubeSize + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX + startZ, startY + cubeSize + startZ),
        end = Offset(startX + cubeSize + startZ, startY + cubeSize + startZ)
    )

    // Connect front and back faces
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(startX + startZ, startY + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX + cubeSize, startY),
        end = Offset(startX + cubeSize + startZ, startY + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX, startY + cubeSize),
        end = Offset(startX + startZ, startY + cubeSize + startZ)
    )
    drawLine(
        color = color,
        start = Offset(startX + cubeSize, startY + cubeSize),
        end = Offset(startX + cubeSize + startZ, startY + cubeSize + startZ)
    )
}