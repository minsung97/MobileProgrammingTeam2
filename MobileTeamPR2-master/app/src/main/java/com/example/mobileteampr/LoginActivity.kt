package com.example.mobileteampr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileteampr.databinding.ActivityLoginBinding
import com.google.firebase.database.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    lateinit var actionBar: ActionBar
    lateinit var rdb: DatabaseReference
    val myViewModel : MyViewModel by viewModels() // owner
    var successLogin = false
    lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar = supportActionBar!!
        actionBar?.hide()
        setContentView(binding.root)
        init()
    }

    private fun init(){
        rdb = FirebaseDatabase.getInstance().getReference("All/users")
        binding.apply{
            loginBtn.setOnClickListener {
                val iid = inputId.text.toString()
                val ipw = inputPw.text.toString()
                rdb.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(user in snapshot.children) {
                            val id = user.child("id").value.toString() // id
                            val pw = user.child("pw").value.toString() // pw
                            // Log.d("test", "$id, $pw")
                            if (iid == id && ipw == pw) {
                                successLogin = true
                                userKey = id // MainActivity 에 보낼 key 정보
                                break // 로그인 성공
                            }
                        }

                        if(successLogin){ // 성공 후 액티비티 전환
                            // 문제 부분 : login 할때 현재 user id로 setting, myviewModel에 null 값 들어감
                            myViewModel.setLiveData(userKey)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("idkey",myViewModel.curUserId.value)
                            startActivity(intent)
                            Log.d("key", myViewModel.curUserId.value.toString())
                        }
                        else{
                            Toast.makeText(this@LoginActivity, "일치하지 않는 계정입니다.", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            joinBtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, JoinActivity::class.java)
                startActivity(intent)
            }
        }
    }


}