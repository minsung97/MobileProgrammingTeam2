package com.example.mobileteampr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileteampr.databinding.ActivityJoinBinding
import com.google.firebase.database.*

class JoinActivity : AppCompatActivity() {
    lateinit var binding : ActivityJoinBinding
    lateinit var rdb:DatabaseReference
    var isSame = false
    var isCan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        rdb = FirebaseDatabase.getInstance().getReference("All/users") // All DB안에 users 테이블
        binding.apply{

            checkIdBtn.setOnClickListener {
                // 닉네임 중복확인
                isSame = false
                val id = joinId.text.toString()
                rdb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (user in snapshot.children) {
                            if (user.key.toString() == id) { // 이미 존재
                                isSame = true
                            }
                        }
                        if(isSame){
                            checkIdView.setText(R.string.id_exist)
                            joinId.text.clear()
                            Log.d("isSame", "$isSame")
                        }
                        else {
                            checkIdView.setText(R.string.id_done)
                            Log.d("isSame", "$isSame")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@JoinActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            checkPwBtn.setOnClickListener {
                val pw = joinPw.text.toString()
                isCan = false
                isCan = checkPw(pw)
                if(isCan){
                    noticePwView.setText(null)
                    checkPwView.setText(R.string.pw_done)
                    Log.d("isCan", "$isCan")
                }
                else{
                    checkPwView.setText(R.string.pw_check)
                    joinPw.text.clear()
                    Log.d("isCan", "$isCan")
                }
            }

            approvedBtn.setOnClickListener {
                val id = joinId.text.toString()
                val pw = joinPw.text.toString()
                //if((checkIdView.text.equals(R.string.id_done)) && isPw) {
                if(!isSame && isCan){
                    insertNewUser(id, pw)
                    Toast.makeText(this@JoinActivity, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@JoinActivity, "닉네임 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    /*
        private fun checkId(id:String):Boolean{
            var flag = true // default : true (가능)
            Log.d("1", "input = $id")
            rdb = FirebaseDatabase.getInstance().getReference("All/users")
            rdb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        if (user.key.toString() == id) { // 이미 존재
                            flag = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@JoinActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
            if(!flag)
                Log.d("2", " false")
            else
                Log.d("2", "true")
            return flag
        }
    */
    private fun checkPw(pw:String):Boolean{ // 비밀번호 조건 : 영소문자+숫자로 4 ~ 10 글자 입력
        val pwReg = Regex("^(?=.*[0-9])(?=.*[a-z]).{4,10}$")
        Log.d("pw.matches", "${pw.matches(pwReg)}")
        return pw.matches(pwReg)
    }

    private fun insertNewUser(id:String, pw:String){
        val user = User(id, pw)
        rdb.child(id).setValue(user) // key 값 : id
    }

}