package me

data class AClass(
    val a:String,
    val b:Int,
) {
    companion object {
        val EMPTY = AClass(
            "",
            1
        )
    }
}

fun AClass.mapParrams(): Map<String, String> = mapOf(
    "text" to a,
    "value" to b.toString()
)

