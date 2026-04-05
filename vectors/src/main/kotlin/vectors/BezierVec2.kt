package vectors

class BezierVec2(val points: List<Vec2>) {

    companion object {
        fun create(a: Vec2, b: Vec2, c: Vec2, d: Vec2): BezierVec2{
            return BezierVec2(listOf(a, b, c, d))
        }
    }
}