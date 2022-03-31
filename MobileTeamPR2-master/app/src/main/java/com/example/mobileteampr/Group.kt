package com.example.mobileteampr

data class Group (var title:String, var Idlist:MutableList<String>, var place:String, var time:String) {
    constructor():this("noinfo", ArrayList(), "noinfo", "noinfo")
}