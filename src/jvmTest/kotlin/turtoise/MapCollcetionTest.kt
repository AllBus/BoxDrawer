package turtoise


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assertions
import kotlin.test.Test


class MapCollcetionTest {

    @Test
    fun testa(){

        val a = mutableMapOf("79123344523" to 4, "76" to 7, "11" to 5)

        println(a)

       a.remove(a.keys.first())

        println(a)
        println(a.javaClass)
        a+="80" to 9
        a+="6" to 1
        println(a)

        val obj:String? = ""
        val mp = Gson().fromJson<Map<String, Int>>(obj, object : TypeToken<Map<String, Int>>() {}.type,
        )
        println(mp)
      //  Assertions.assertNull(mp)

        Assertions.assertEquals(a.keys.toList(), listOf("76", "11", "80", "6"))
    }
}