package com.example.shortly

data class ShortLink(
    val long_url: String,
    var short_url:String = "api not implemented yet",
    var delete_check:Boolean = false
)
