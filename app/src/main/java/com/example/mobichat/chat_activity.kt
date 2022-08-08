package com.example.mobichat

//import com.bumptech.glide.Glide
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import io.agora.rtc.RtcEngine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.widget.Toolbar;


private var mRtcEngine:RtcEngine?=null

class chat_activity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var testext : TextView
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var attachments:ImageView
    private lateinit var imageUri:ImageView
    var receiverRoom:String? = null
    var senderRoom:String? = null
    val PICK_PDF_FILE = 2
    private lateinit var document:ImageView
    private var imageMessageCreated : Boolean = false
    private lateinit var imageMessageUri : String
    private lateinit var senderUid : String
    private lateinit var contact: ImageView

    @SuppressLint("Range", "UseSupportActionBar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        // using toolbar as ActionBar

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser!!.uid
        senderRoom = receiverUid +senderUid
        receiverRoom = senderUid + receiverUid
        supportActionBar?.title = name

        mDbRef = FirebaseDatabase.getInstance().getReference()
        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        attachments = findViewById(R.id.attachment)
        sendButton = findViewById(R.id.sendButton)
        testext = findViewById(R.id.testtext)
        testext.visibility = View.INVISIBLE
        messageList = ArrayList()
        imageUri = findViewById(R.id.textimage)
        imageUri.visibility = View.INVISIBLE
        messageAdapter = MessageAdapter(this, messageList)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent())  { uri: Uri? ->
            if(uri != null)
            {
                //Toast.makeText(this, "created", Toast.LENGTH_SHORT).show()
                imageMessageCreated = true
                imageMessageUri = uri.toString()
                //Toast.makeText(this, ""+uri,Toast.LENGTH_LONG).show()

            }

        }


        //Toast.makeText(this, ""+imageMessageCreated,Toast.LENGTH_LONG).show()

        // Logic to add data to dabase
        mDbRef.child("chats").child(senderRoom!!).child("Messages").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(Message::class.java)
                    //message.imageMessage.toUri()
                    messageList.add(message!!)
                    //Toast.makeText(this@chat_activity, message.imageMessage,Toast.LENGTH_SHORT).show()

                    //imageUri.setImageURI(Uri.parse(message.imageMessage))
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        //Add the message to the database
        sendButton.setOnClickListener {

                val message = messageBox.text.toString()
                val messageObject = Message(message = message, senderId = senderUid)

               // val messageImage = senderRoom + getFileExtentsion(imageMessageUri!!)
                //val messageObject = Message( message,senderUid)

                if(!imageMessageCreated)
                    createMessage(messageObject)
                else{
                    Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show()
                    imageMessageCreated = false
                    createImageMessage(imageMessageUri)}



        }

        var num:String?=null
        /*
        val getResult =
            registerForActivityResult(
                ActivityResultContracts.GetContent()) {
                    val contactUri =it
                   // val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var cursor: Cursor? = contentResolver.query(
                    contactUri, projection,
                    null, null, null
                )
                val numberIndex =
                    cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val number = cursor.getString(numberIndex)
                num = number
            }


         */

        var ans:String?=null
        val getPerson = registerForActivityResult(PickContact()) {
            it?.also { contactUri ->
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER

                )

                val cursor: Cursor? = contentResolver?.query(contactUri, projection, null, null, null)?.apply {
                    moveToFirst()
                }
                val id = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val n = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneIndex =
                    cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val n2 =cursor.getString(phoneIndex)
               // testext.text = n
                val contactMessage = n+n2
                val messageObject = Message(message = contactMessage, senderId = senderUid)
                createMessage(messageObject)
                cursor?.close()
            }
        }


        /*
       val openContacts = registerForActivityResult(ActivityResultContracts.PickContact()) {

            val contactData: Uri = it
            val phone: Cursor? = contentResolver.query(contactData!!, null, null, null, null)
            if (phone!!.moveToFirst()) {
                val contactName: String = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                // To get number - runtime permission is mandatory.
                val id: String = phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID))
                if (phone.getString(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                    val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                    while (phones!!.moveToNext()) {
                        val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    }
                    phones!!.close()
                }

                testext.text =  contactName
            }

        }

         */
        attachments.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view: View = layoutInflater.inflate(R.layout.attachments,null)
            //Attachments Buttons
            document = view.findViewById(R.id.document)
            document.setOnClickListener {
                getContent.launch("image/*")
                dialog.dismiss()
            }
            contact= view.findViewById(R.id.contact)
            contact.setOnClickListener {
                getPerson.launch(0)
                /*
                val intent = Intent(Intent.ACTION_DEFAULT, ContactsContract.Contacts.CONTENT_URI)
                getResult.launch(ContactsContract.Contacts.CONTENT_URI.toString())
                dialog.dismissWithAnimation
                Toast.makeText(this, num, Toast.LENGTH_SHORT).show()

                 */


            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }



    }


    private fun getFileExtentsion(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun createMessage(messageObject:Message){
        mDbRef.child("chats").child(senderRoom!!).child("Messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("Messages").push()
                    .setValue(messageObject)
            }

        messageBox.setText("")
    }
    private fun createImageMessage(imageMessageUri:String)
    {
        /*
        val file = Uri.fromFile(File(imageMessageUri))
        val riversRef = Firebase.storage.reference.child("Images/now")
        val uploadTask = riversRef.putFile(file)
        var value:String?=null
        uploadTask.addOnSuccessListener {
           value =  it.storage.downloadUrl.toString()
        }

         */

        val file:File =File(imageMessageUri)
        val storage : FirebaseStorage= FirebaseStorage.getInstance()
        var downloadUri: UploadTask.TaskSnapshot? = null
        var ur:Task<Uri>?=null
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        var uri:String?=null
        val storageRef:StorageReference = storage.reference.child("images")
        val imageName = currentDate+""+".jpeg"
        val urlTask : StorageTask<UploadTask.TaskSnapshot> = storageRef.child(imageName).putFile(Uri.parse(imageMessageUri)).addOnSuccessListener {
           it.storage.downloadUrl
        }.addOnCompleteListener{
            //uri = it.result.metadata?.reference?.downloadUrl.toString()
        }
        val ref = storageRef.child(imageName)
        val uploadTask = ref.putFile(Uri.parse(imageMessageUri))
        var downUri:String?=null
        val urlask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downUri = task.result.toString()
                val  messageObject = Message(imageMessage = downUri, senderId = senderUid)
                createMessage(messageObject)
                /*
                Picasso
                    .get()
                    .load(downUri)
                    .into(imageUri);

                 */
            } else {
                // Handle failures
                // ...
            }
        }

        //imageUri.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/chatapplication-2c2fa.appspot.com/o/images%2FImage02%3A10%3A04?alt=media&token=b4bdbc3b-8163-4d60-9935-17e6bce3650b"))
        //val  messageObject = Message(imageMessage = imageMessageUri, senderId = senderUid)

    }
    private fun retrieveImage(image:String)
    {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$image")
        //storageReference.getFile()
    }

    @SuppressLint("Range")
    private fun getPhoneNumber(contentResolver: ContentResolver, id: String?): String {
        var phoneNumber = ""

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.Contacts._ID + " = ?",
            arrayOf(id),
            null
        ) ?: return phoneNumber

        if (cursor.moveToFirst()) {
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))
        }

        cursor.close()
        return phoneNumber
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat,menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
             R.id.videocall->{
                 startVideoCall()
             }
            R.id.PhoneCall->{
                startPhoneCall()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startVideoCall():Unit{
        Toast.makeText(this,"Starting Video Call", Toast.LENGTH_SHORT).show()
    }

    private fun startPhoneCall():Unit{
        Toast.makeText(this,"Starting Phone Call", Toast.LENGTH_SHORT).show()
    }



    private fun sendImageUri(imageUri: Uri) :Unit{
        val timestamp:String = ""+System.currentTimeMillis()
        val filePathName:String = "ChatImages/Post$timestamp"

    }

    class PickContact : ActivityResultContract<Int, Uri?>() {
        override fun createIntent(context: Context, input: Int?) =
            Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
                it.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == RESULT_OK) intent?.data else null
        }
    }
}