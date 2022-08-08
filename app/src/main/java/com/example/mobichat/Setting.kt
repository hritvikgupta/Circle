package com.example.mobichat

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class Setting : AppCompatActivity() {

    private lateinit var name:TextView
    private lateinit var profilestat:TextView
    private lateinit var email:TextView

    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDB: DatabaseReference

    private lateinit var userList : ArrayList<User>
    private lateinit var save : Button
    private lateinit var cancel : Button
    private lateinit var editname: ImageView
    private lateinit var editStatus: ImageView
    private lateinit var editEmail: ImageView
    private lateinit var titleBar: TextView
    private lateinit var editDialog: EditText
    private lateinit var mDbRef : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val toolbar = findViewById<View>(R.id.toolbarSettings) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        name = findViewById<TextView>(R.id.profileSettingName)
        profilestat = findViewById<TextView>(R.id.ProfileSettingStatus)
        email = findViewById<TextView>(R.id.ProfileSettingEmail)

        editname = findViewById(R.id.editName)
        editStatus = findViewById(R.id.editStatus)
        editEmail = findViewById(R.id.editEmail)
        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseDatabase.getInstance().reference



        //Toast.makeText(this, "Hola"+intent.getStringExtra("name"), Toast.LENGTH_SHORT).show()
        email.text = mAuth.currentUser?.email
        val username = intent.getStringExtra("name")
        name.text = username.toString()
        val uid = mAuth.currentUser?.uid
        var stat:String
        mDbRef = FirebaseDatabase.getInstance().getReference()
        val value: DatabaseReference  = mDbRef.child("User").child(uid!!).child("Status")
        value.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               stat = snapshot.getValue().toString()
                profilestat.text = stat
                if(profilestat.text == "null")
                {
                    profilestat.text = "Profile Status"
                }
                //Toast.makeText(this@Setting, stat, Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(error: DatabaseError) {
                profilestat.text = "Profile Status"
            }

        })



        editname.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view : View = layoutInflater.inflate(R.layout.bottomeditprofiledialog,null)
            /*
            val bottomsheet  = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomsheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

             */

            cancel = view.findViewById(R.id.btn_cancel)
            save = view.findViewById(R.id.btn_save)
            editDialog = view.findViewById(R.id.editDialog)
            var newName :String?=null
            val uid = mAuth.currentUser?.uid
            //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()
            save.setOnClickListener {
                //dialog.dismiss()

                newName  = editDialog.text.toString()
                //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()
                mDB.child("User").child(uid!!).child("username").setValue(newName)
                dialog.dismiss()
                name.text = newName


            }
            //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        editStatus.setOnClickListener {
           // Toast.makeText(this,"Hola",Toast.LENGTH_SHORT).show()
            val dialog2 = BottomSheetDialog(this)
            val view2 : View = layoutInflater.inflate(R.layout.bottomeditprofiledialog,null)
            /*
            val bottomsheet  = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomsheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

             */
            titleBar = view2.findViewById(R.id.titleDialog)
            titleBar.text = "Set Status"
            editDialog = view2.findViewById(R.id.editDialog)
            editDialog.hint = "Set Status"
            cancel = view2.findViewById(R.id.btn_cancel)
            save = view2.findViewById(R.id.btn_save)
            var status :String? = null
            val uid = mAuth.currentUser?.uid
            save.setOnClickListener {
                status  = editDialog.text.toString()
                //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()
                mDB.child("User").child(uid!!).child("Status").setValue(status)
                dialog2.dismiss()
                profilestat.text = status
            }
            cancel.setOnClickListener {
                dialog2.dismiss()
            }

            dialog2.setCancelable(true)
            dialog2.setContentView(view2)
            dialog2.show()
        }

        editEmail.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view : View = layoutInflater.inflate(R.layout.bottomeditprofiledialog,null)
            /*
            val bottomsheet  = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomsheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

             */
            titleBar = view.findViewById(R.id.titleDialog)
            titleBar.text = "Edit Email"
            editDialog = view.findViewById(R.id.editDialog)
            editDialog.hint = "Edit Email"
            editDialog.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            cancel = view.findViewById(R.id.btn_cancel)
            save = view.findViewById(R.id.btn_save)
            editDialog = view.findViewById(R.id.editDialog)
            var newEmail :String?=null
            val uid = mAuth.currentUser?.uid
            val user = FirebaseAuth.getInstance().currentUser

            //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()
            save.setOnClickListener {
                //dialog.dismiss()
                newEmail = editDialog.text.toString()
                //Toast.makeText(this, newName,Toast.LENGTH_SHORT).show()
                mDB.child("User").child(uid!!).child("email").setValue(newEmail)
                mAuth.currentUser?.updateEmail(newEmail!!)
                dialog.dismiss()
                email.text = newEmail
                user!!.updateEmail(newEmail!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "The email updated.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                val intent = Intent(this@Setting, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


