package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
import intern.line.me.kyotoaclient.model.entity.*
import intern.line.me.kyotoaclient.presenter.room.UpdateMember
import intern.line.me.kyotoaclient.presenter.user.*
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.room.GetRoomIcon
import intern.line.me.kyotoaclient.presenter.room.PostRoomIcon
import kotlinx.android.synthetic.main.activity_room_create.*
import kotlinx.android.synthetic.main.activity_room_member.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import com.google.android.gms.common.util.IOUtils
import java.io.FileOutputStream


class RoomMemberActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777
    var file: File? = null

    lateinit var room: Room

    private var me: User? = null

    lateinit var adapter: UserSelectListAdapter

	private	val room_repo = RoomRepository()
	private  val user_repo = UserRepository()
	private  val job = Job()
	private val presenter = GetUserList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_member)

        val room_id = intent.getSerializableExtra("room_id") as Long
		room = room_repo.getById(room_id)!!

        val room_name = findViewById(R.id.room_name_text) as EditText
        room_name.setText(room.name)
        room_name.setSelection(room_name.text.length)

		adapter = UserSelectListAdapter(this,presenter.getUsersListFromDb())
		val listView: ListView = this.findViewById(R.id.user_select_list)
		listView.adapter = adapter
		registerForContextMenu(listView)

		launch(job + UI) {
			presenter.getUsersList()
		}

        launch(job + UI) {
            val members = GetMembers().getMembers(room_id)
            val member_id_list = members.map { it.id }
            val users = user_repo.getAll()

            adapter.checkList = users.map{ it -> member_id_list.contains(it.id)} as MutableList<Boolean>
            adapter.notifyDataSetChanged()
        }

		launch(job + UI) {
			GetRoomIcon().getRoomIcon(room_id).let {
				val image = BitmapFactory.decodeStream(it)
				edit_room_icon_view.setImageBitmap(image)
			}
		}

        edit_room_icon_view.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = this

        try{
            if(requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data!=null){
                val uri = Uri.parse(data.dataString)
                val file = getFile(uri) ?: throw Exception("no file found")

				val image = BitmapFactory.decodeStream(file.inputStream())
				edit_room_icon_view.setImageBitmap(image)
            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFile(uri: Uri): File? {
        val path: String? = getPath(uri)
        if (path != null) {
            file = File(path)
            return file
        }
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        val filename = "room_icon_id_" + room.id.toString() + "." + ext

        file = File(cacheDir, filename)
        val fos = FileOutputStream(file)
        fos.write(IOUtils.toByteArray(contentResolver.openInputStream(uri)))
        fos.close()
        return file
    }

    fun getPath(uri: Uri): String? {
        val context = this

        if (DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.getAuthority()) {// ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    "/stroage/" + type + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == uri.getAuthority()) {// DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == uri.getAuthority()) {// MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                contentUri = MediaStore.Files.getContentUri("external")
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        if ("content".equals(uri.getScheme(), ignoreCase = true)) {//MediaStore
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {// File
            return uri.getPath()
        }
        return null
    }

    fun getDataColumn(context: Context?, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        try {
            cursor = context!!.contentResolver.query(
                    uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val cindex = cursor!!.getColumnIndexOrThrow(projection[0])
                return cursor.getString(cindex)
            }
        } catch (e: Exception) {
            // no op
        } finally {
            cursor?.close()
        }
        return null
    }

    fun onEditRoom(v: View) {
        println("on click!")
        val room = room
        val roomName = (findViewById<TextView>(R.id.room_name_text)).text.toString()
        var selectedUsers = adapter.getCheckedUserList()
        val me = me
        if (me != null){
            selectedUsers = (selectedUsers as MutableList<User>)
            selectedUsers.add(me)
        }
        println(selectedUsers)
        selectedUsers ?: return

		launch(UI) {
			try {
				UpdateMember(roomName, selectedUsers, room).updateMember()
			}catch (e : Throwable){
				goBack()
			}
			if(file != null) {
				PostRoomIcon().postRoomIcon(room.id, file!!)
				file!!.delete()
			}
			goBack()
		}
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}
