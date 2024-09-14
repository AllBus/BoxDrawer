package com.kos.boxdrawer.detal.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.kos.boxdrawer.presentation.display.colorList
import org.kabeja.dxf.helpers.Edge
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun Grid3DVisualizer(grid: Grid3D, rotationX: Float, rotationY: Float, rotationZ: Float, event: Int, current : Coordinates) {
    val groups = remember(event) {   grid.findConnectedGroups()}
    val planes = remember(groups) {
        groups.map { g ->
            val e = grid.convertToLongEdges(g.getExternalEdges())
            g.kubik to grid.edgesInPlanes(e)
        }
    }

    val edges = remember( planes) {
        planes.map { (kubik, g) ->
            val t = g.mapValues { (k, s) -> grid.createPolygon(s) }
                .flatMap { (k, s) -> s }
            PolygonGroup(kubik, t)
        }
    }

    val cur = remember(current) {
        with(current) {
            setOf(
                LongEdge(current, Coordinates(x + 1, y, z)),
                LongEdge(current, Coordinates(x, y + 1, z)),
                LongEdge(current, Coordinates(x, y, z + 1)),
                LongEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y + 1, z)),
                LongEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y, z + 1)),
                LongEdge(Coordinates(x, y + 1, z), Coordinates(x + 1, y + 1, z)),
                LongEdge(Coordinates(x, y + 1, z), Coordinates(x, y + 1, z + 1)),
                LongEdge(Coordinates(x, y, z + 1), Coordinates(x + 1, y, z + 1)),
                LongEdge(Coordinates(x, y, z + 1), Coordinates(x, y + 1, z + 1)),
                LongEdge(Coordinates(x + 1, y + 1, z), Coordinates(x + 1, y + 1, z + 1)),
                LongEdge(Coordinates(x + 1, y, z + 1), Coordinates(x + 1, y + 1, z + 1)),
                LongEdge(Coordinates(x, y + 1, z + 1), Coordinates(x + 1, y + 1, z + 1))
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cubeSize = 50f
        val spacing = 10f

        val m = Matrix()
        m.rotateX(rotationX)
        m.rotateY(rotationY)
        m.rotateZ(rotationZ)
//
//        rotate(45f) { // Rotate the canvas for a3D-like view
            translate(left = size.width/4, top = size.height/4) { // Translate for better positioning
//                for (group in groups) {
//                    val firstKubikColor = group.firstOrNull()?.let { grid[it.x, it.y, it.z]?.color }
//                    val groupColor = firstKubikColor?.let { Color(it) } ?: Color.Gray
//
//                    val polygons = grid.createPolygonsForGroup(group, grid)
//                    for (polygon in polygons) {
//                        drawPath(
//                            path = polygon,
//                            color = groupColor,
//                            style = Stroke(width = 2f)
//                        )
//                    }
//                }
//            }

//                planes.forEach { (k,p) ->
//
//                    p.values.forEach { e ->
//                        drawLongEdges(e, Color(k.color), m)
//                    }
//                }

                drawLongEdges(cur, Color.Red, m)

                for (e in edges) {

                    val c = colorList[e.kubik.color % colorList.size]
                    for (s in e.polygons) {
                        drawPolygons(s, c , m)
                    }
                }

            }
    }
}


fun DrawScope.drawPolygons(
    polygon: Polygon,
    color: Color,
    rotationMatrix:Matrix,
) {

    val cubeSize = 50f
    val spacing = 0f


    val path = Path()
    val rotatedVertices = polygon.vertices.map { rotatePoint(it, rotationMatrix) }

    val start = rotatedVertices.first()
    val startX = start.x * (cubeSize + spacing)
    val startY = start.y * (cubeSize + spacing)
    path.moveTo(startX, startY)

    for (vertex in rotatedVertices.drop(1)) {
        val x = vertex.x * (cubeSize + spacing)
        val y = vertex.y * (cubeSize + spacing)
        path.lineTo(x, y)
    }

    path.close()

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

fun DrawScope.drawLongEdges(longEdges: Set<LongEdge>,
                            color: Color,
                            m:Matrix,
                            ) {
    val cubeSize = 50f

    for (edge in longEdges) {

        val start = edge.start
        val end = edge.end
        val startX = (start.x*m[0,0]+ start.y*m[1,0]+start.z*m[2,0])*cubeSize  // start.x * (cubeSize + spacing)
        val startY = (start.x*m[0,1]+ start.y*m[1,1]+start.z*m[2,1])*cubeSize
        val endX = (end.x*m[0,0]+ end.y*m[1,0]+end.z*m[2,0])*cubeSize   // start.x * (cubeSize + spacing)
        val endY = (end.x*m[0,1]+ end.y*m[1,1]+end.z*m[2,1])*cubeSize

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