package com.example.finalproject.Admin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Model.AdminOrders
import com.example.finalproject.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminNewOrdersActivity : AppCompatActivity() {

    lateinit var ordersList: RecyclerView
    lateinit var ordersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_new_orders)


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
        ordersList = findViewById(R.id.orders_list)
        ordersList.setLayoutManager(LinearLayoutManager(this))

    }


    override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<AdminOrders>()
            .setQuery(ordersRef, AdminOrders::class.java)
            .build()

        val adapter = object: FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {

            override fun onBindViewHolder(@NonNull holder:AdminOrdersViewHolder, position:Int, @NonNull model:AdminOrders) {
                holder.userName.setText("Name: " + model.getName())
                holder.userPhoneNumber.setText("Phone: " + model.getPhone())
                holder.userTotalPrice.setText("Total Amount = $" + model.getTotalAmount())
                holder.userDateTime.setText("Order at: " + model.getDate() + " " + model.getTime())
                holder.userShippingAddress.setText("Shipping Address: " + model.getAddress() + ", " + model.getCity())

                holder.ShowOrdersBtn.setOnClickListener(object: View.OnClickListener {
                    override fun onClick(view:View) {
                        val uID = getRef(position).getKey()
                        val intent = Intent(this@AdminNewOrdersActivity, AdminUserProductsActivity::class.java)
                        intent.putExtra("uid", uID)
                        startActivity(intent)
                    }
                })
                holder.itemView.setOnClickListener(object:View.OnClickListener {
                    override fun onClick(view:View) {
                        val options = arrayOf<CharSequence>("Yes", "No")
                        val builder = AlertDialog.Builder(this@AdminNewOrdersActivity)
                        builder.setTitle("Have you shipped this order products ?")
                        builder.setItems(options, object: DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface:DialogInterface, i:Int) {
                                if (i == 0)
                                {
                                    val uID = getRef(position)?.getKey()
                                    RemoverOrder(uID!!)
                                }
                                else
                                {
                                    finish()
                                }
                            }
                        })
                        builder.show()
                    }
                })
            }


            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType:Int):AdminOrdersViewHolder {
                val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false)
                return AdminOrdersViewHolder(view)
            }
        }
        ordersList.setAdapter(adapter)
        adapter.startListening()
    }


    class AdminOrdersViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        var userName: TextView
        var userPhoneNumber:TextView
        var userTotalPrice:TextView
        var userDateTime:TextView
        var userShippingAddress:TextView
        var ShowOrdersBtn: Button

        init{
            userName = itemView.findViewById(R.id.order_user_name)
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number)
            userTotalPrice = itemView.findViewById(R.id.order_total_price)
            userDateTime = itemView.findViewById(R.id.order_date_time)
            userShippingAddress = itemView.findViewById(R.id.order_address_city)
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn)
        }
    }

    private fun RemoverOrder(uID:String) {

        ordersRef.child(uID).removeValue()
    }
}