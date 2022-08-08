package com.example.mobichat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var storiesBar: RecyclerView
    private lateinit var userList : ArrayList<User>
    private lateinit var storyList : ArrayList<Story>
    private lateinit var useradapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    var nameUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // supportActionBar?.setIcon(R.mipmap.circle)
        val toolbar = findViewById<View>(R.id.toolbarMainActivity) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        //supportActionBar?.setLogo(R.mipmap.greencircle);
        supportActionBar?.setDisplayUseLogoEnabled(true);
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userList = ArrayList()
        storyList = ArrayList()
        userRecyclerView = findViewById(R.id.userRecyclerView)
        storiesBar = findViewById(R.id.storiesbar)

        useradapter  = UserAdapter(this,userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = useradapter

        val adapterstories:storiesAdapter = storiesAdapter(this, storyList)
        storiesBar.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        storiesBar.adapter = adapterstories

        mDbRef.child("User").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //snapshot is used to get the data from the database
                //userList.clear() // This on DataChange method is implement if there is change in data
                //Therefore for that we need to clear the old list every time and create a new one
                for(postSnapShot in snapshot.children){
                    val currentUser = postSnapShot.getValue(User::class.java)
                    //Toast.makeText(this@MainActivity, currentUser?.username, Toast.LENGTH_SHORT).show()
                    if(mAuth.currentUser?.uid != currentUser?.uid)
                    {
                        userList.add(currentUser!!)
                        storyList.add(Story(seen = true, userName = currentUser.username))
                       // storyList[0] = Story(seen = true, userName = currentUser.username)
                        //storyList.add(Story(seen = true, userName = currentUser.username))
                    }
                    else
                    {
                        nameUser = currentUser?.username
                        storyList.add(Story(seen = true, userName = currentUser?.username))

                    }
                    /*
                    val value = mAuth.currentUser?.uid !== currentUser?.uid
                    if(!value){
                        Toast.makeText(this@MainActivity, ""+currentUser?.username,Toast.LENGTH_SHORT).show()

                    }
                     */
                }
                useradapter.notifyDataSetChanged()
                adapterstories.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

/*
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = false))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = false))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = false))
        storyList.add(Story(seen = true))
        storyList.add(Story(seen = false))

 */



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if(item.itemId == R.id.logout)
        {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }

        if(item.itemId == R.id.ProfileSetting)
        {
            val intent  = Intent(this,Setting::class.java)
            intent.putExtra("name", nameUser)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }
}