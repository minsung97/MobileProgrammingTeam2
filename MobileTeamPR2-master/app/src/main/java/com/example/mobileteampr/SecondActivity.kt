package com.example.mobileteampr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.mobileteampr.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class SecondActivity : AppCompatActivity() {
    lateinit var curTitle : String
    lateinit var curId: String
    lateinit var binding : ActivityMainBinding
    val myViewModel2 : MyViewModel2 by viewModels() // owner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        if(intent.hasExtra("titleKey")){
            curTitle = intent.getStringExtra("titleKey").toString()
            Log.d("model2- if",curTitle)
        }
        if(intent.hasExtra("idKey")){
            curId = intent.getStringExtra("idKey").toString()
            Log.d("model2- if",curId)
        }

        var list:MutableList<String> = ArrayList()
        list.add(curTitle)
        list.add(curId)
        myViewModel2.setLiveData(list)
        Log.d("model2",myViewModel2.getLiveData().toString())
        Log.d("model2",myViewModel2.getLiveData2().toString())
    }
}