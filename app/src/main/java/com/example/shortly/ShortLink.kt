package com.example.shortly

data class ShortLink(
    val long_url: String?,
    val short_url:String?,
    var delete_check:Boolean = false,
)
