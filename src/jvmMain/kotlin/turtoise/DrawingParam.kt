package turtoise

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class DrawerSettings(
    val boardWeight : Double = 4.0,

    /**
    /// Ширина отверстия
     */
    val holeWeight : Double = 4.05,

    /**
    ///  Уменьшение длины зигзага
     */
    val zigDrop : Double = 0.5,
    /**
    ///  Уменьшение длины отверстия
     */
    val holeDrop : Double = 0.5,
    /**
    ///  Уменьшение высоты отверстия
     */
    val holeDropHeight : Double = 0.0,
    val holeOffset : Double = 2.0,

    val name:String = ""
) {

}

enum class Orientation {
    Horizontal,
    Vertical,
}

data class DrawingParam(
    val orientation: Orientation = Orientation.Horizontal,
    val reverse: Boolean = false,
    val back: Boolean = false,
) {

}


@Serializable()
data class DrawerSettingsList(
    val group: List<DrawerSettings>

){

}

@Serializable
data class FullSettings(
    @SerializedName("name")
    val properties:DrawerSettingsList
)