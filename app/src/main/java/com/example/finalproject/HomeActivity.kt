package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Admin.AdminMaintainProductsActivity
import com.example.finalproject.Model.Products
import com.example.finalproject.Prevalent.Prevalent
import com.example.finalproject.ViewHolder.ProductViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var ProductsRef:DatabaseReference
    lateinit var recyclerView:RecyclerView
    internal lateinit var layoutManager:RecyclerView.LayoutManager
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val intent = getIntent()
        val bundle = intent.getExtras()
        if (bundle != null)
        {
            type = getIntent().getExtras()?.get("Admin").toString()
        }
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products")
        Paper.init(this)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle("Home")
        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {

                if (type != "Admin")
                {
                    val intent = Intent(this@HomeActivity, CartActivity::class.java)
                    startActivity(intent)
                }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.user_profile_name
        val profileImageView = headerView.user_profile_image
        if (type != "Admin")
        {
            userNameTextView.setText(Prevalent.currentOnlineUser.getName())
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView)
        }
        recyclerView = findViewById(R.id.recycler_menu)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)
    }
     override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(ProductsRef, Products::class.java)
            .build()

        val adapter = object:FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {

             override fun onBindViewHolder(@NonNull holder:ProductViewHolder, position:Int, @NonNull model:Products) {
                holder.txtProductName.setText(model.getPname())
                holder.txtProductDescription.setText(model.getDescription())
                holder.txtProductPrice.setText("Price = " + model.getPrice() + "$")
                Picasso.get().load(model.getImage()).into(holder.imageView)

                holder.itemView.setOnClickListener {

                        if (type == "Admin")
                        {
                            val intent = Intent(this@HomeActivity, AdminMaintainProductsActivity::class.java)
                            intent.putExtra("pid", model.getPid())
                            startActivity(intent)
                        }
                        else
                        {
                            val intent = Intent(this@HomeActivity, ProductDetailsActivity::class.java)
                            intent.putExtra("pid", model.getPid())
                            startActivity(intent)
                        }

                }
            }
            @NonNull
            override fun onCreateViewHolder(@NonNull parent:ViewGroup, viewType:Int):ProductViewHolder {
                val view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false)
                val holder = ProductViewHolder(view)
                return holder
            }
        }
        recyclerView.setAdapter(adapter)
        adapter.startListening()
    }
    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu:Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu)
        return true
    }
    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        val id = item.getItemId()
         if (id == R.id.action_settings)
         {
             if (type != "Admin")
             {
                 val intent = Intent(this@HomeActivity, SettinsActivity::class.java)
                 startActivity(intent)
             }
         return true
         }
        return super.onOptionsItemSelected(item)
    }
    override fun onNavigationItemSelected(item:MenuItem):Boolean {
        // Handle navigation view item clicks here.
        val id = item.getItemId()
        if (id == R.id.nav_cart)
        {
            if (type != "Admin")
            {
//                val intent = Intent(this@HomeActivity, CartActivity::class.java)
//                startActivity(intent)
            }
        }
//        else if (id == R.id.nav_search)
//        {
//            if (type != "Admin")
//            {
//                val intent = Intent(this@HomeActivity, SearchProductsActivity::class.java)
//                startActivity(intent)
//            }
//        }
        else if (id == R.id.nav_categories)
        {
        }
        else if (id == R.id.nav_logout)
        {
            if (type != "Admin")
            {
                Paper.book().destroy()
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}