package turtoise.rect

class RectBlock(
    var width: Double,
    var height: Double,

    var edges: RectBlockEdges,
    val parentInfo: RectBlockParent,
) {
    var children: List<RectBlock> = emptyList()
       private set
    var parent: RectBlock? = null
        set(newValue){
           synchronized(this) {
               parent?.let { p ->
                   p.children -= this
               }

               newValue?.let { p ->
                   p.children += this
               }
               field = newValue
           }
    }
}

class RectBlockParent(
    val storona: EStorona,
    val inside: Boolean,
    val padding:Double,
    val bias:Double,
){

}

class RectBlockEdges(
    var topLeft: RectBlockEdge,
    var topRight: RectBlockEdge,
    var bottomLeft: RectBlockEdge,
    var bottomRight: RectBlockEdge,
){
    companion object{
        fun default() = RectBlockEdges(
            topLeft = RectBlockEdgeSimple(),
            topRight = RectBlockEdgeSimple(),
            bottomLeft = RectBlockEdgeSimple(),
            bottomRight = RectBlockEdgeSimple(),
        )
    }
}

enum class EStorona{
    LEFT, RIGHT, TOP, BOTTOM
}

