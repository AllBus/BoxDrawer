package com.kos.figure.complex.model

data class CubikDirection(
    val count:Int,
    val direction:Int,
    val isInnerCorner:Boolean,
    val isReverse:Boolean,
    val isFlat:Boolean,
) {

    val xSize = when(direction){
        DIRECTION_RIGHT -> count
        DIRECTION_LEFT -> -count
        else -> 0
    }

    val ySize = when(direction){
        DIRECTION_UP -> -count
        DIRECTION_DOWN -> count
        else -> 0
    }

    override fun toString(): String {
        return "[$count $direction ${if (isInnerCorner) 1 else 0} ${ if (isFlat) 0 else if (isReverse) -1 else 1 }]"
    }

    companion object {
        const val DIRECTION_RIGHT = 0
        const val DIRECTION_DOWN = 1
        const val DIRECTION_LEFT = 2
        const val DIRECTION_UP = 3

        fun reverse(direction:Int):Int{
            return when (direction){
                DIRECTION_RIGHT -> DIRECTION_LEFT
                DIRECTION_DOWN -> DIRECTION_UP
                DIRECTION_LEFT -> DIRECTION_RIGHT
                DIRECTION_UP -> DIRECTION_DOWN
                else -> direction
            }
        }
    }

    fun reverse():CubikDirection{
        return this.copy(direction = reverse(this.direction))
    }
}