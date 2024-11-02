package com.kos.boxdrawer.detal.grid

import com.kos.figure.complex.model.CubikDirection
import vectors.Vec2

class GridPolygoninfo(val start:Coordinates, val sides: List<CubikDirection>, val isHole: Boolean, val parentCoordinate: Coordinates?, val offset: Vec2) {
}