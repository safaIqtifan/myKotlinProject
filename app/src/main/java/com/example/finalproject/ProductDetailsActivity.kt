package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.finalproject.Model.Products
import com.example.finalproject.Prevalent.Prevalent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var addToCartButton: Button
    lateinit var productImage: ImageView
    lateinit var numberButton: ElegantNumberButton
    lateinit var productPrice: TextView
    lateinit var productDescription:TextView
    lateinit var productName:TextView
    private var productID = ""
    private var state = "Normal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)


        productID = getIntent().getStringExtra("pid")

        addToCartButton = findViewById(R.id.pd_add_to_cart_button)
        numberButton = findViewById(R.id.number_btn)
        productImage = findViewById(R.id.product_image_details)
        productName = findViewById(R.id.product_name_details)
        productDescription = findViewById(R.id.product_description_details)
        productPrice = findViewById(R.id.product_price_details)

        getProductDetails(productID)

        addToCartButton.setOnClickListener {

                if (state == "Order Placed" || state == "Order Shipped")
                {
                    Toast.makeText(this@ProductDetailsActivity,
                        "you can add purchase more products, once your order is shipped or confirmed.",
                        Toast.LENGTH_LONG).show()
                }
                else
                {
                    addingToCartList()
                }
        }

    }



     override fun onStart() {
        super.onStart()
        CheckOrderState()
    }


    private fun addingToCartList() {

        val saveCurrentTime:String
        val saveCurrentDate:String
        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calForDate.getTime())
        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentDate.format(calForDate.getTime())


        val cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
        val cartMap = HashMap<String, Any>()
        cartMap.put("pid", productID)
        cartMap.put("pname", productName.getText().toString())
        cartMap.put("price", productPrice.getText().toString())
        cartMap.put("date", saveCurrentDate)
        cartMap.put("time", saveCurrentTime)
        cartMap.put("quantity", numberButton.getNumber())
        cartMap.put("discount", "")


        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
            .child("Products").child(productID)
            .updateChildren(cartMap)

            .addOnCompleteListener(object:OnCompleteListener<Void> {
                override fun onComplete(@NonNull task:Task<Void>) {

                    if (task.isSuccessful())
                    {
                        cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                            .child("Products").child(productID)
                            .updateChildren(cartMap)
                            .addOnCompleteListener(object: OnCompleteListener<Void> {

                                override fun onComplete(@NonNull task: Task<Void>) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(this@ProductDetailsActivity, "Added to Cart List.", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@ProductDetailsActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            })
                    }
                }
            })
    }


    private fun getProductDetails(productID:String) {
        val productsRef = FirebaseDatabase.getInstance().getReference().child("Products")
        productsRef.child(productID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    val products = dataSnapshot.getValue(Products::class.java)
                    productName.setText(products?.getPname())
                    productPrice.setText(products?.getPrice())
                    productDescription.setText(products?.getDescription())
                    Picasso.get().load(products?.getImage()).into(productImage)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }


    private fun CheckOrderState() {
        val ordersRef: DatabaseReference
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone())
        ordersRef.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(dataSnapshot:DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    val shippingState = dataSnapshot.child("state").getValue().toString()
                    if (shippingState == "shipped")
                    {
                        state = "Order Shipped"
                    }
                    else if (shippingState == "not shipped")
                    {
                        state = "Order Placed"
                    }
                }
            }
            override fun onCancelled(databaseError:DatabaseError) {
            }
        })
    }
}