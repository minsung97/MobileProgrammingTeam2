package com.example.mobileteampr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel2 : ViewModel() {
    val curTitle = MutableLiveData<String>()
    val curId = MutableLiveData<String>()

    fun setLiveData(list:MutableList<String>){
        curTitle.value = list[0]
        curId.value = list[1]
    }

    fun getLiveData() : String? {
        return curTitle.value
    }
    fun getLiveData2() : String?{
        return curId.value
    }
}