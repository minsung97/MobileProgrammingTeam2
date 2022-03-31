package com.example.mobileteampr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query


class GroupFragment : Fragment() {
    var data : ArrayList<Group> = ArrayList() // 사용?
    val myViewModel:MyViewModel by activityViewModels()
    lateinit var adapter : MyGroupAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var rdb: DatabaseReference
    lateinit var curId : String
    lateinit var query : Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group, container, false)
        init(view)
        return view
    }

    private fun init(view:View){
        curId = myViewModel.curUserId.value!!
        val addBtn = view.findViewById<ImageView>(R.id.addBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        rdb = FirebaseDatabase.getInstance().getReference("All/test")

        // All / Groups / key : id - title - group
        val query = rdb.child("id").child(curId).orderByValue()

        // 데이터 가져올 쿼리 : groups의 value에서 idList에 curId가 존재하는것만 가져옴
        // 최근 50개 데이터만 가져옴
        Log.d("idList", rdb.child("idList").toString())

        val option = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(query, Group::class.java)
            .build()

        adapter = MyGroupAdapter(option)
        adapter.itemClickListener = object : MyGroupAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                // 그룹 클릭 때마다?

            }
        }
        recyclerView.adapter = adapter
        initAdapter()

        addBtn.setOnClickListener {
            // 그룹 생성 다이얼로그 그룹이름, 유저리리스트
            GroupDialogFragment().show(activity?.supportFragmentManager!!, "GroupDialog")
            initAdapter()
        }
    }

    private fun initAdapter(){
        if(adapter!=null)
            adapter.startListening()

        val query = rdb.child("id").child(curId).orderByValue()
        val option = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(query, Group::class.java)
            .build()

        adapter = MyGroupAdapter(option)
        adapter.itemClickListener = object : MyGroupAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                // 그룹 클릭할때마다 호출되는 함수
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