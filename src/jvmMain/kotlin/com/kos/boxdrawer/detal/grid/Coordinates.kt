package com.kos.boxdrawer.detal.grid

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(val x: Int, val y: Int, val z: Int){
    operator fun minus (other : Coordinates): Coordinates {
        return Coordinates(x - other.x, y - other.y, z - other.z)
    }

    operator fun plus (other : Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y, z + other.z)
    }

    fun dotProduct(other: Coordinates): Int {
        return x * other.x + y * other.y + z * other.z
    }

    fun crossProduct(other: Coordinates): Coordinates {
        return Coordinates(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}