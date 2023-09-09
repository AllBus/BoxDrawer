package turtoise


data class DrawerSettings(
    val boardWeight : Double = 4.0,

    /**
    /// Ширина отверстия
     */
    val holeWeight : Double = 4.05,

    /**
    ///  Уменьшение длины отверстия
     */
    val holeDrop : Double = 0.5,
    val holeOffset : Double = 2.0,

) {

}

enum class Orientation {
    Horizontal,
    Vertical,
}


enum class PazForm {
    None,
    Paz,
    Hole,
    BackPaz,
    Flat
}

class DrawingParam {
    var orientation: Orientation = Orientation.Horizontal
    var reverse: Boolean = false
    var back: Boolean = false
}