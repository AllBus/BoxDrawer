package com.kos.boxdrawer.formula

import com.kos.ariphmetica.Calculator
import com.kos.ariphmetica.math.algorithms.CopositeFunction
import com.kos.ariphmetica.math.algorithms.OutExpression
import com.kos.ariphmetica.math.algorithms.Replacement
import com.kos.ariphmetica.math.terms.MathTerm
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey

object FormulaUtils {

    fun calc(line: MathTerm, t: MathTerm, tvalue: Double): Double {
        return calc(line, t, parse("$tvalue"))
    }

    fun calc(line: MathTerm, t: MathTerm, tvalue: MathTerm): Double {
        val r =
            OutExpression.apply(Calculator.fullCalc(Replacement.replace(line, t, tvalue))) as String
        val s = if (r.startsWith("(") && r.endsWith(")")) {
            r.drop(1).dropLast(1)
        } else r
        // println(s)
        return s.toDoubleOrNull() ?: 0.0
    }

    fun calc(line: MathTerm): Double {
        val r =
            OutExpression.apply(Calculator.fullCalc(line)) as String
        val s = if (r.startsWith("(") && r.endsWith(")")) {
            r.drop(1).dropLast(1)
        } else r
        return s.toDoubleOrNull() ?: 0.0
    }

    fun parse(value: Double): MathTerm {
        return Calculator.parseWithSpace("$value")
    }

    fun parse(line: String): MathTerm {
        return Calculator.parseWithSpace(line)
    }

    fun calc(line: String): MathTerm {
        val dif = parse(line)
        return Calculator.fullCalc(CopositeFunction.compose(dif))
    }

    fun diff(line: String): MathTerm {
        val dif = parse(line)
        return Calculator.fullCalc(Calculator.diff(CopositeFunction.compose(dif)))
    }

    fun extractValues(values: String, memory: TortoiseMemory): List<Pair<MathTerm, MathTerm>>{
        try {
            val ss = values.split(';')
            val repl = ss.mapNotNull { t ->
                val ti = t.indexOf('=')
                if (ti > 0) {
                    val v = t.substring(0, ti)
                    val n = t.substring(ti + 1)
                    val n2 = if (n.startsWith("@")){
                        "${memory.value(MemoryKey(n.drop(1)), 0.0)}"
                    } else
                        n
                    parse(v) to parse(n2)
                } else
                    null
            }
            return repl
        }catch (e:Exception){
            return emptyList()
        }
    }

    fun podstanovka(line: String ,values:List<Pair<MathTerm, MathTerm>>): MathTerm {
        val dif = parse(line)
        return podstanovka(Calculator.fullCalc(dif), values)
    }

    fun podstanovka(line: MathTerm, values:List<Pair<MathTerm, MathTerm>>): MathTerm {
        return if (values.isNotEmpty()) {
            values.reversed().fold(line) { d, v ->
                //  println("${v.first} : ${d}")
                Replacement.replace(d, v.first, v.second) }
        } else
            line
    }
}