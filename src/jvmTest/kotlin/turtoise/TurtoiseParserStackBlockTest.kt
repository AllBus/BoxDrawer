package turtoise

import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TurtoiseParserStackBlockTest {

    fun parseLine(text:String): TortoiseParserStackItem {
        return TortoiseParser.parseSkobki(text).inner[0]
    }

    @Test
    fun testParseSkobki() {
        val a=parseLine("(a(b c) (d e(f)(g)) h)")
        assertIs<TortoiseParserStackBlock>(a)
        assertEquals(2, a.blocks.size )
        assertEquals(4, a.inner.size )
        assertEquals("d", a.blocks[1].name.name )
        assertEquals("f", a.blocks[1].blocks[0].name.name )
    }

    @Test
    fun get() {
        val a = parseLine("(a 5(b c) (d e(f)(g 8 (ro (bo (to 234) 45) 90))) h)")

        assertEquals(null, a.get(".")?.name)
        assertEquals("a", a.get(".0")?.name)
        assertEquals(null, a.get(".6")?.name)
        assertEquals("h", a.get(".4")?.name)
        assertEquals("c", a.get(".2")?.name)
        assertEquals("c", a.get(".b")?.name)
        assertEquals("e", a.get(".d")?.name)
        assertEquals("e", a.get(".d.1")?.name)
        assertEquals("d", a.get(".d.0")?.name)
        assertEquals("8", a.get(".d.g")?.name)
        assertEquals("", a.get(".d.f")?.name)
        assertEquals("(bo (to 234) 45)", a.get(".d.g.ro")?.name)
        assertEquals("(to 234)", a.get(".d.g.ro.bo")?.name)
        assertEquals("234", a.get(".d.g.ro.bo.to")?.name)
    }

    @Test
    fun getArgument() {
        val a = parseLine("72")
        assertEquals("72", a.get(".")?.name)
        assertEquals("72", a.get("0")?.name)
        assertEquals("72", a.get("")?.name)
        assertEquals(null, a.get("1")?.name)
        assertEquals(null, a.get("other")?.name)
    }
}