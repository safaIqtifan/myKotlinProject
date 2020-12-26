package com.example.finalproject

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Prevalent.Prevalent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.gms.tasks.Continuation
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask


class SettinsActivity : AppCompatActivity() {

    lateinit var profileImageView:CircleImageView
    lateinit var fullNameEditText:EditText
    lateinit var userPhoneEditText:EditText
    lateinit var addressEditText:EditText
    lateinit var profileChangeTextBtn:TextView
    lateinit var closeTextBtn:TextView
    lateinit var saveTextButton:TextView
    lateinit var securityQuestionBtn: Button
    private var imageUri: Uri? = null
    private var myUrl = ""
    //lateinit var uploadTask: StorageTask
    lateinit var storageProfilePrictureRef: StorageReference
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settins)


        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures")
        profileImageView = findViewById(R.id.settings_profile_image) as CircleImageView
        fullNameEditText = findViewById(R.id.settings_full_name) as EditText
        userPhoneEditText = findViewById(R.id.settings_phone_number) as EditText
        addressEditText = findViewById(R.id.settings_address) as EditText
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn) as TextView
        closeTextBtn = findViewById(R.id.close_settings_btn) as TextView
        saveTextButton = findViewById(R.id.update_account_settings_btn) as TextView
        //securityQuestionBtn = findViewById(R.id.security_questions_btn)
        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText)


        closeTextBtn.setOnClickListener {

                finish()
        }


//        securityQuestionBtn.setOnClickListener {
//
//                val intent = Intent(this@SettinsActivity, ResetPasswordActivity::class.java)
//                intent.putExtra("check", "settings")
//                startActivity(intent)
//
//        }


        saveTextButton.setOnClickListener {

                if (checker == "clicked")
                {
                    userInfoSaved()
                }
                else
                {
                    updateOnlyUserInfo()
                }

        }


        profileChangeTextBtn.setOnClickListener {

                checker = "clicked"
                CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this@SettinsActivity)

        }

    }


    private fun updateOnlyUserInfo() {

        val ref = FirebaseDatabase.getInstance().getReference().child("Users")
        val userMap = HashMap<String, Any>()
        userMap.put("name", fullNameEditText.getText().toString())
        userMap.put("address", addressEditText.getText().toString())
        userMap.put("phoneOrder", userPhoneEditText.getText().toString())
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap)
        startActivity(Intent(this@SettinsActivity, HomeActivity::class.java))
        Toast.makeText(this@SettinsActivity, "Profile Info update successfully.", Toast.LENGTH_SHORT).show()
        finish()

    }


    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.getUri()
            profileImageView.setImageURI(imageUri)
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SettinsActivity, SettinsActivity::class.java))
            finish()
        }
    }
    private fun userInfoSaved() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is address.", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show()
        }
        else if (checker == "clicked")
        {
            uploadImage()
        }
    }
    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Update Profile")
        progressDialog.setMessage("Please wait, while we are updating your account information")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        if (imageUri != null)
        {
            val fileRef = storageProfilePrictureRef
                .child(Prevalent.currentOnlineUser.getPhone() + ".jpg")
            val uploadTask = fileRef.putFile(imageUri!!)
            val urlTask = uploadTask.continueWithTask(object:
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                @Throws(Exception::class)

                override fun then(@NonNull task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!task.isSuccessful())
                    {
                        throw task.getException()!!
                    }
                    return fileRef.getDownloadUrl()
                }
            })
                .addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(@NonNull task:Task<Uri>) {
                        if (task.isSuccessful())
                        {
                            val downloadUrl = task.getResult()
                            myUrl = downloadUrl.toString()
                            val ref = FirebaseDatabase.getInstance().getReference().child("Users")
                            val userMap = HashMap<String, Any>()
                            userMap.put("name", fullNameEditText.getText().toString())
                            userMap.put("address", addressEditText.getText().toString())
                            userMap.put("phoneOrder", userPhoneEditText.getText().toString())
                            userMap.put("image", myUrl)
                            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap)
                            progressDialog.dismiss()
                            startActivity(Intent(this@SettinsActivity, HomeActivity::class.java))
                            Toast.makeText(this@SettinsActivity, "Profile Info update successfully.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else
                        {
                            progressDialog.dismiss()
                            Toast.makeText(this@SettinsActivity, "Error.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun userInfoDisplay(profileImageView:CircleImageView, fullNameEditText:EditText, userPhoneEditText:EditText, addressEditText:EditText) {
        val UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone())
        UsersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        val image = dataSnapshot.child("image").getValue().toString()
                        val name = dataSnapshot.child("name").getValue().toString()
                        val phone = dataSnapshot.child("phone").getValue().toString()
                        val address = dataSnapshot.child("address").getValue().toString()
                        Picasso.get().load(image).into(profileImageView)
                        fullNameEditText.setText(name)
                        userPhoneEditText.setText(phone)
                        addressEditText.setText(address)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}