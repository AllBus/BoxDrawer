package turtoise

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TurtoiseParserStackBlockTest {

    fun parseLine(text:String): TurtoiseParserStackItem{
        return TortoiseParser.parseSkobki(text).inner[0]
    }

    @Test
    fun testParseSkobki() {
        val a=parseLine("(a(b c) (d e(f)(g)) h)")
        assertIs<TurtoiseParserStackBlock>(a)
        assertEquals(2, a.blocks.size )
        assertEquals(4, a.inner.size )
        assertEquals("d", a.blocks[1].name )
        assertEquals("f", a.blocks[1].blocks[0].name )
    }

    @Test
    fun get() {
        val a = parseLine("(a 5(b c) (d e(f)(g 8 (ro (bo (to 234) 45) 90))) h)")

        assertEquals(null, a.get("."))
        assertEquals("a", a.get(".0"))
        assertEquals(null, a.get(".6"))
        assertEquals("h", a.get(".4"))
        assertEquals("c", a.get(".2"))
        assertEquals("c", a.get(".b"))
        assertEquals("e", a.get(".d"))
        assertEquals("e", a.get(".d.1"))
        assertEquals("d", a.get(".d.0"))
        assertEquals("8", a.get(".d.g"))
        assertEquals("", a.get(".d.f"))
        assertEquals("90", a.get(".d.g.ro"))
        assertEquals("45", a.get(".d.g.ro.bo"))
        assertEquals("234", a.get(".d.g.ro.bo.to"))
    }

    @Test
    fun getArgument() {
        val a = parseLine("72")
        assertEquals("72", a.get("."))
        assertEquals("72", a.get("0"))
        assertEquals("72", a.get(""))
        assertEquals(null, a.get("1"))
        assertEquals(null, a.get("other"))
    }
}