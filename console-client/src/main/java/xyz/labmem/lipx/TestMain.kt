package xyz.labmem.lipx

import cn.hutool.core.convert.Convert

fun main(){

    Convert.toSet(String::class.java,"[123,333]").apply {
        add("12312dcasd")
        println(toString())
    }

}