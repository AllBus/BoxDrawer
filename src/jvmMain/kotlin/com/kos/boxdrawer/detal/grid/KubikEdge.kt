package com.kos.boxdrawer.detal.grid

data class KubikEdge(val start: Coordinates, val end: Coordinates) {
    val direction: Direction = when {
        start.x != end.x -> Direction.X
        start.y != end.y -> Direction.Y
        start.z != end.z -> Direction.Z
        else -> throw IllegalArgumentException("Invalid KubikEdge: start and end coordinates must be adjacent.")
    }

    init {
        require(
            (start.x == end.x && start.y == end.y && Math.abs(start.z - end.z) == 1) ||
                    (start.x == end.x && start.z == end.z && Math.abs(start.y - end.y) == 1) ||
                    (start.y == end.y&& start.z == end.z && Math.abs(start.x - end.x) == 1)
        ) { "Invalid KubikEdge: start and end coordinates must be adjacent." }
    }


    fun isCollinearWith(other: KubikEdge): Boolean {
        return direction== other.direction &&
                (start == other.start || start == other.end || end == other.start || end == other.end)
    }
}