package turtoise

data class ZigzagInfo(
    val width: Double,
    val delta: Double,
    val height: Double = 0.0,
    val enable: Boolean = true,
){
    fun commandLine():String{
        return "$width $delta $height ${if (!enable) "false" else ""}"
    }
}