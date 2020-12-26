package com.example.finalproject.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.finalproject.HomeActivity
import com.example.finalproject.MainActivity
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_admin_category.*

class AdminCategoryActivity : AppCompatActivity() {

    lateinit var tShirts: ImageView
    lateinit var sportsTShirts:ImageView
    lateinit var femaleDresses:ImageView
    lateinit var sweathers:ImageView
    lateinit var glasses:ImageView
    lateinit var hatsCaps:ImageView
    lateinit var walletsBagsPurses:ImageView
    lateinit var shoes:ImageView
    lateinit var headPhonesHandFree:ImageView
    lateinit var Laptops:ImageView
    lateinit var watches:ImageView
    lateinit var mobilePhones:ImageView
    lateinit var LogoutBtn: Button
    lateinit var CheckOrdersBtn:Button
    lateinit var maintainProductsBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_category)

        LogoutBtn = findViewById(R.id.admin_logout_btn)
        CheckOrdersBtn = findViewById(R.id.check_orders_btn)
        maintainProductsBtn = findViewById(R.id.maintain_btn)


        maintainProductsBtn.setOnClickListener {


                val intent = Intent(this@AdminCategoryActivity, HomeActivity::class.java)
                intent.putExtra("Admin", "Admin")
                startActivity(intent)
            }


        LogoutBtn.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }


        CheckOrdersBtn.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminNewOrdersActivity::class.java)
                startActivity(intent)
            }



        tShirts = findViewById(R.id.t_shirts)
        sportsTShirts = findViewById(R.id.sports_t_shirts)
        femaleDresses = findViewById(R.id.female_dresses)
        sweathers = findViewById(R.id.sweathers)
        glasses = findViewById(R.id.glasses)
        hatsCaps = findViewById(R.id.hats_caps)
        walletsBagsPurses = findViewById(R.id.purses_bags_wallets)
        shoes = findViewById(R.id.shoes)
        headPhonesHandFree = findViewById(R.id.headphones_handfree)
        Laptops = findViewById(R.id.laptop_pc)
        watches = findViewById(R.id.watches)
        mobilePhones = findViewById(R.id.mobilephones)


        tShirts.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "tShirts")
                startActivity(intent)
            }


        sportsTShirts.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Sports tShirts")
                startActivity(intent)
            }


        femaleDresses.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Female Dresses")
                startActivity(intent)
            }


        sweathers.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Sweathers")
                startActivity(intent)
            }


        glasses.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Glasses")
                startActivity(intent)
            }


        hatsCaps.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Hats Caps")
                startActivity(intent)
            }


        walletsBagsPurses.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Wallets Bags Purses")
                startActivity(intent)
            }


        shoes.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Shoes")
                startActivity(intent)
            }


        headPhonesHandFree.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "HeadPhones HandFree")
                startActivity(intent)
            }


        Laptops.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Laptops")
                startActivity(intent)
            }


        watches.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Watches")
                startActivity(intent)
            }


        mobilePhones.setOnClickListener {

                val intent = Intent(this@AdminCategoryActivity, AdminAddNewProductActivity::class.java)
                intent.putExtra("category", "Mobile Phones")
                startActivity(intent)
            }

    }
}