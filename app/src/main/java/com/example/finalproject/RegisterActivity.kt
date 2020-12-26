package com.example.finalproject

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    lateinit var CreateAccountButton: Button
    lateinit var InputName: EditText
    lateinit var InputPhoneNumber: EditText
    lateinit var InputPassword: EditText
    lateinit var loadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        CreateAccountButton = findViewById(R.id.register_btn)
        InputName = findViewById(R.id.register_username_input)
        InputPassword = findViewById(R.id.register_password_input)
        InputPhoneNumber = findViewById(R.id.register_phone_number_input)
        loadingBar = ProgressDialog(this)

        CreateAccountButton.setOnClickListener {

            CreateAccount()

        }
    }


    private fun CreateAccount() {

        val name = InputName.getText().toString()
        val phone = InputPhoneNumber.getText().toString()
        val password = InputPassword.getText().toString()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show()
        } else {
            loadingBar.setTitle("Create Account")
            loadingBar.setMessage("Please wait, while we are checking the credentials.")
            loadingBar.setCanceledOnTouchOutside(false)
            loadingBar.show()
            ValidatephoneNumber(name, phone, password)
        }
    }

    private fun ValidatephoneNumber(name: String, phone: String, password: String) {

        val RootRef: DatabaseReference
        RootRef = FirebaseDatabase.getInstance().getReference()

        RootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override
            fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    val userdataMap = HashMap<String, Any>()
                    userdataMap.put("phone", phone)
                    userdataMap.put("password", password)
                    userdataMap.put("name", name)

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                        .addOnCompleteListener(object : OnCompleteListener<Void> {

                            override
                            fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Congratulations, your account has been created.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadingBar.dismiss()
                                    val intent =
                                        Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    loadingBar.dismiss()
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Network Error: Please try again after some time...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "This " + phone + " already exists.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingBar.dismiss()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please try again using another phone number.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}
