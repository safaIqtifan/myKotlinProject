package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.finalproject.Prevalent.Prevalent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ConfirmFinalOrderActivity : AppCompatActivity() {

    lateinit var nameEditText: EditText
    lateinit var phoneEditText:EditText
    lateinit var addressEditText:EditText
    lateinit var cityEditText:EditText
    lateinit var confirmOrderBtn: Button
    private var totalAmount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_final_order)

        totalAmount = getIntent().getStringExtra("Total Price")
        Toast.makeText(this, "Total Price = $ " + totalAmount, Toast.LENGTH_LONG).show()

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn)
        nameEditText = findViewById(R.id.shippment_name)
        phoneEditText = findViewById(R.id.shippment_phone_number)
        addressEditText = findViewById(R.id.shippment_address)
        cityEditText = findViewById(R.id.shippment_city)

        confirmOrderBtn.setOnClickListener {

                Check()
        }
    }


    private fun Check() {

        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full name.", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your address.", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your city name.", Toast.LENGTH_SHORT).show()
        }
        else
        {
            ConfirmOrder()
        }
    }


    private fun ConfirmOrder() {

        val saveCurrentDate:String
        val saveCurrentTime:String
        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calForDate.getTime())
        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentDate.format(calForDate.getTime())

        val ordersRef = FirebaseDatabase.getInstance().getReference()
            .child("Orders")
            .child(Prevalent.currentOnlineUser.getPhone())

        val ordersMap = HashMap<String, Any>()
        ordersMap.put("totalAmount", totalAmount)
        ordersMap.put("name", nameEditText.getText().toString())
        ordersMap.put("phone", phoneEditText.getText().toString())
        ordersMap.put("address", addressEditText.getText().toString())
        ordersMap.put("city", cityEditText.getText().toString())
        ordersMap.put("date", saveCurrentDate)
        ordersMap.put("time", saveCurrentTime)
        ordersMap.put("state", "not shipped")

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(object: OnCompleteListener<Void> {
            override fun onComplete(@NonNull task: Task<Void>) {

                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(object:OnCompleteListener<Void> {
                            override fun onComplete(@NonNull task:Task<Void>) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(this@ConfirmFinalOrderActivity, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@ConfirmFinalOrderActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        })
                }
            }
        })
    }
}
