package com.example.shortly

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private lateinit var sharedPref: SharedPreferences
private lateinit var gson: Gson

class HistoryHelper {

    constructor(context: Context) {
        sharedPref = context.getSharedPreferences("UrlHistory", Context.MODE_PRIVATE)
        gson = Gson()
    }

    fun saveHistory(urls: ArrayList<ShortLink>) {
        val editor = sharedPref.edit()
        editor.putString("links", gson.toJson(urls))
        editor.apply()
    }

    fun loadHistory(): ArrayList<ShortLink> {
        val emptyList = Gson().toJson(ArrayList<ShortLink>())
        val historyString = sharedPref.getString("links", emptyList)
        val urlList: ArrayList<ShortLink> =
            gson.fromJson(historyString, object : TypeToken<ArrayList<ShortLink>>() {
            }.type)

        return if (urlList != null) {
            urlList
        } else {
            ArrayList<ShortLink>()
        }
    }

}