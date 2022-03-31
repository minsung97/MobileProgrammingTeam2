package com.example.mobileteampr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SetFragment : Fragment() {
    var data : ArrayList<Group> = ArrayList() // 사용?
    val myViewModel:MyViewModel by activityViewModels()
    val myViewModel2:MyViewModel2 by activityViewModels()

    lateinit var adapter : MyTimeAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var rdb: DatabaseReference
    lateinit var curId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_set, container, false)
        init(view)
        return view
    }

    private fun init(view:View){
        curId = myViewModel.curUserId.value!!

        recyclerView = view.findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        rdb = FirebaseDatabase.getInstance().getReference("All/test")   //  group leaf 안으로

        // 데이터 가져올 쿼리 : groups의 value에서 idList에 curId가 존재하는것만 가져옴
        // 최근 50개 데이터만 가져옴
        val query = rdb.child("id").child(curId).orderByValue()
        Log.d("idList", rdb.child("idList").toString())

        val option = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(query, Group::class.java)
            .build()

        adapter = MyTimeAdapter(option)
        adapter.itemClickListener = object : MyTimeAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {}
        }
        initAdapter()

        recyclerView.adapter = adapter

    }

    private fun initAdapter(){
        if(adapter!=null)
            adapter.startListening()

        val query = rdb.child("id").child(curId).orderByValue()
        val option = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(query, Group::class.java)
            .build()

        adapter = MyTimeAdapter(option)
        adapter.itemClickListener = object : MyTimeAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                var mutableList: MutableList<String> = ArrayList()
                mutableList.add(adapter.getItem(position).title)
                mutableList.add(curId)
                myViewModel2.setLiveData(mutableList)
                val intent = Intent(activity, SecondActivity::class.java)
                intent.putExtra("titleKey",myViewModel2.curTitle.value)
                intent.putExtra("idKey",myViewModel2.curId.value)
                //startActivity(intent)
                val intent2 = Intent(activity, SetMap::class.java)
                startActivity(intent2)
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening() // 자동 동기화
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}