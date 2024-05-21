package com.kos.boxdrawer.presentation

import com.google.gson.Gson

fun <T> T.checkField(): T = this ?: throw Exception("Обязательное поле отсутствует")

class Mort(
    val a: String,
    val b:String,
)

class Cff {

    fun akt() {
        val m = Gson().fromJson<Mort>("""{"a":"One", "b":null}""", Mort::class.java)

        println(m.a.checkField())
        println(m.b.checkField())

    }
}