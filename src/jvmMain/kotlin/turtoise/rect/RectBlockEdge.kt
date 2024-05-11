package turtoise.rect

interface RectBlockEdge{

}

class RectBlockEdgeRound(
    val radius: Double
):  RectBlockEdge{

}

class RectBlockEdgeSimple():  RectBlockEdge{

}

class RectBlockEdgeFaska(
    val offsetStart: Double,
    val offsetEnd: Double,
):  RectBlockEdge{

}