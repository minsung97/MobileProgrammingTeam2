package com.example.mobileteampr

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.mobileteampr.databinding.FragmentGroupDialogBinding
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class GroupDialogFragment : DialogFragment() {
    var binding : FragmentGroupDialogBinding?=null
    val myViewModel:MyViewModel by activityViewModels()
    val invitedUsers : ArrayList<String> = ArrayList()// User 아이디 담을 리스트
    lateinit var rdb: DatabaseReference
    lateinit var urdb : DatabaseReference
    lateinit var curId:String
    lateinit var result_time : String
    lateinit var userListView : ListView


    override fun onResume() {
        super.onResume()
        // full-screen
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false // 밖에 눌러도 dismiss 되지 않음
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // dialog?.window?.requestFeature(STYLE_NO_TITLE)
        binding = FragmentGroupDialogBinding.inflate(layoutInflater, container, false)
        //val view = inflater.inflate(com.example.mobileteampr.R.layout.fragment_group_dialog, container, false)
        init(binding!!.root)
        return binding!!.root
    }

    private fun init(view:View){
        curId = myViewModel.curUserId.value!!
        invitedUsers.add(curId) // 나도 포함
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        urdb = FirebaseDatabase.getInstance().getReference("All/users") // id 검색해서 초대
        rdb = FirebaseDatabase.getInstance().getReference("All/test") // groups 그룹 정보
        super.onViewCreated(view, savedInstanceState)
        val listAdapter = ArrayAdapter(activity?.applicationContext!!, R.layout.simple_list_item_1, invitedUsers)

        binding!!.apply{

            userListView.adapter = listAdapter // userListView에 어댑터 부착
            // userListView.choiceMode = ListView.CHOICE_MODE_SINGLE 초대 삭제 하기 구현?

            // 친구 검색
            inviteBtn.setOnClickListener {
                Log.d("초대버튼", "시작")
                urdb.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("검색", "시작")
                        for (user in snapshot.children) { // Users의 키값 탐색 성공 확인
                            if (user.key.toString() == inviteEdit.text.toString()) { // 해당 아이디 있으면
                                invitedUsers.add(inviteEdit.text.toString())
                                listAdapter.notifyDataSetChanged()
                                inviteEdit.text.clear()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            timedateEdit.setOnClickListener {
                showDatePicker()
            }

            timetimeEdit.setOnClickListener {
                showTimePicker()
            }

            submitBtn.setOnClickListener {
                val title = titleEdit.text.toString()
                val place = addressEdit.text.toString()
                createGroup(title, invitedUsers, place, result_time)
                dismiss()
            }

            cancelBtn.setOnClickListener {
                dismiss() // 취소
            }

        }
    }

    private fun createGroup(title: String, invitedUsers: ArrayList<String>, place:String, time:String){
        val group = Group(title, invitedUsers, place, time)
        for(id in invitedUsers)
            rdb.child("id").child(id).child(title).setValue(group)
    }

    fun showDatePicker(){
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var date = calendar.get(Calendar.DATE)

        var dateListener = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                binding!!.timedateEdit.text = "${year} 년 ${month + 1} 월 ${dayOfMonth} 일"
                result_time = "${year} ${month + 1} ${dayOfMonth}"
            }
        }
        var date_builder = DatePickerDialog(requireActivity()!!, dateListener,year,month,date)
        date_builder.show()
    }

    fun showTimePicker(){
        var time = Calendar.getInstance()
        var hour = time.get(Calendar.HOUR)
        var minute = time.get(Calendar.MINUTE)

        var timeListener = object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding!!.timetimeEdit.text = "${hourOfDay} 시  ${minute} 분"
                result_time = result_time + " ${hourOfDay} ${minute}"
            }
        }
        var time_builder = TimePickerDialog(requireActivity()!!, timeListener,hour,minute,false)
        time_builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}