package com.example.finalproject

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Model.Cart
import com.example.finalproject.Prevalent.Prevalent
import com.example.finalproject.ViewHolder.CartViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var NextProcessBtn: Button
    lateinit var txtTotalAmount: TextView
    lateinit var txtMsg1:TextView
    private var overTotalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cart_list)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)
        NextProcessBtn = findViewById(R.id.next_btn)
        txtTotalAmount = findViewById(R.id.total_price)
        txtMsg1 = findViewById(R.id.msg1)


        NextProcessBtn.setOnClickListener {

                txtTotalAmount.setText("Total Price = $" + (overTotalPrice).toString())
                val intent = Intent(this@CartActivity, ConfirmFinalOrderActivity::class.java)
                intent.putExtra("Total Price", (overTotalPrice).toString())
                startActivity(intent)
                finish()

        }
    }



    override fun onStart() {
        super.onStart()
        CheckOrderState()

        val cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
        val options = FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"), Cart::class.java)
            .build()


        val adapter = object: FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
             override fun onBindViewHolder(@NonNull holder:CartViewHolder, position:Int, @NonNull model:Cart) {

                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity())
                holder.txtProductPrice.setText("Price " + model.getPrice() + "$")
                holder.txtProductName.setText(model.getPname())

                 val oneTyprProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity())
                overTotalPrice = overTotalPrice + oneTyprProductTPrice

                 holder.itemView.setOnClickListener{

                        val options = arrayOf<CharSequence>("Edit", "Remove")
                        val builder = AlertDialog.Builder(this@CartActivity)
                        builder.setTitle("Cart Options:")
                        builder.setItems(options, object: DialogInterface.OnClickListener {

                            override fun onClick(dialogInterface:DialogInterface, i:Int) {

                                if (i == 0)
                                {
                                    val intent = Intent(this@CartActivity, ProductDetailsActivity::class.java)
                                    intent.putExtra("pid", model.getPid())
                                    startActivity(intent)
                                }
                                if (i == 1)
                                {
                                    cartListRef.child("User View")
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products")
                                        .child(model.getPid())
                                        .removeValue()
                                        .addOnCompleteListener(object: OnCompleteListener<Void> {

                                            override fun onComplete(@NonNull task: Task<Void>) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(this@CartActivity, "Item removed successfully.", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(this@CartActivity, HomeActivity::class.java)
                                                    startActivity(intent)
                                                }
                                            }
                                        })
                                }
                            }
                        })
                        builder.show()

                }
            }


            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType:Int):CartViewHolder {

                val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false)
                val holder = CartViewHolder(view)
                return holder
            }
        }

        recyclerView.setAdapter(adapter)
        adapter.startListening()
    }



    private fun CheckOrderState() {

        val ordersRef: DatabaseReference
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone())

        ordersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists())
                {
                    val shippingState = dataSnapshot.child("state").getValue().toString()
                    val userName = dataSnapshot.child("name").getValue().toString()

                    if (shippingState == "shipped")
                    {
                        txtTotalAmount.setText("Dear " + userName + "\n order is shipped successfully.")
                        recyclerView.setVisibility(View.GONE)
                        txtMsg1.setVisibility(View.VISIBLE)
                        txtMsg1.setText("Congratulations, your final order has been Shipped successfully. Soon you will received your order at your door step.")
                        NextProcessBtn.setVisibility(View.GONE)
                        Toast.makeText(this@CartActivity, "you can purchase more products, once you received your first final order.", Toast.LENGTH_SHORT).show()
                    }
                    else if (shippingState == "not shipped")
                    {
                        txtTotalAmount.setText("Shipping State = Not Shipped")
                        recyclerView.setVisibility(View.GONE)
                        txtMsg1.setVisibility(View.VISIBLE)
                        NextProcessBtn.setVisibility(View.GONE)
                        Toast.makeText(this@CartActivity, "you can purchase more products, once you received your first final order.", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {}

        })
    }
}