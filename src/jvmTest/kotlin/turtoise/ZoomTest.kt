package turtoise

import com.kos.boxdrawer.presentation.ZoomUtils
import kotlin.test.assertEquals
import kotlin.test.Test

class ZoomTest {
    @Test
    fun testZoomValues(){
        for (i in 0 .. 300){
            val iz = ZoomUtils.indexToZoom(i)
            val rz = ZoomUtils.calcZoom(iz)
            assertEquals(i, rz)
        }
    }
}