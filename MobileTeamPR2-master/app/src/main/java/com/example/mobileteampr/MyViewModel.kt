package com.example.mobileteampr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val curUserId = MutableLiveData<String>()

    fun setLiveData(id:String){
        curUserId.value = id
    }

    fun getLiveData() : String? {
        return curUserId.value
    }
}