package turtoise


class DrawerSettings {
    var boardWeight = 4.4

    /**
    /// Ширина отверстия
     */
    var holeWeight = 4.47

    /**
    ///  Уменьшение длины отверстия
     */
    var holeDrop = 0.5
    var holeOffset = 2.0
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