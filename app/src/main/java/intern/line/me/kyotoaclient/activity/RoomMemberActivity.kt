package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.graphics.BitmapFactory
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


class RoomMemberActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777
    lateinit var file: File

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
            intent.setType("file/*")
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
                var filePath = data.getDataString()
                filePath=filePath.substring(filePath.indexOf("storage"))
                val decodedPath = URLDecoder.decode(filePath, "utf-8")
                //val decodedPath = "/sdcard/P.jpg"
                Toast.makeText(this, decodedPath, Toast.LENGTH_LONG).show()

                //TODO(file選択方法)
                file =  File(decodedPath)

				val image = BitmapFactory.decodeStream(file.inputStream())
				edit_room_icon_view.setImageBitmap(image)

            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
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
			UpdateMember(roomName, selectedUsers, room).updateMember()
			PostRoomIcon().postRoomIcon(room.id,file)
			goBack()
		}
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}
