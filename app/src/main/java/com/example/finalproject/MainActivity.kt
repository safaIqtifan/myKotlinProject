package com.example.finalproject

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.finalproject.Model.Users
import com.example.finalproject.Prevalent.Prevalent
import com.example.finalproject.Prevalent.Prevalent.UserPasswordKey
import com.example.finalproject.Prevalent.Prevalent.UserPhoneKey
import com.google.firebase.database.*
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    lateinit var joinNowButton: Button
    lateinit var loginButton: Button
    lateinit var loadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        joinNowButton = findViewById(R.id.main_join_now_btn)
        loginButton = findViewById(R.id.main_login_btn)
        loadingBar = ProgressDialog(this)

        Paper.init(this)

        loginButton.setOnClickListener {

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

        }


        joinNowButton.setOnClickListener {

            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)

        }


        val UserPhoneKey: String? = Paper.book().read(UserPhoneKey)
        val UserPasswordKey: String? = Paper.book().read(UserPasswordKey)
        if (UserPhoneKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                if (UserPhoneKey != null && UserPasswordKey != null) {
                    AllowAccess(UserPhoneKey, UserPasswordKey)
                }
                loadingBar.setTitle("Already Logged in")
                loadingBar.setMessage("Please wait.....")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
            }
        }


        }


    private fun AllowAccess(phone: String, password: String) {

        val RootRef: DatabaseReference
        RootRef = FirebaseDatabase.getInstance().getReference()
        RootRef.addListenerForSingleValueEvent(object: ValueEventListener {

            override
            fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child("Users").child(phone).exists()) {

                    val usersData: Users? = dataSnapshot.child("Users").child(phone).getValue(Users::class.java)
                    if (usersData?.getPhone().equals(phone)) {

                        if (usersData?.getPassword().equals(password)) {
                            Toast.makeText(this@MainActivity, "Please wait, you are already logged in...", Toast.LENGTH_SHORT).show()
                            loadingBar.dismiss()
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            Prevalent.currentOnlineUser = usersData
                            startActivity(intent)
                        } else {
                            loadingBar.dismiss()
                            Toast.makeText(this@MainActivity, "Password is incorrect.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show()
                    loadingBar.dismiss()
                }
            }

            override
            fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
