package intern.line.me.kyotoaclient.activity

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_room_member.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.UnsupportedEncodingException
import intern.line.me.kyotoaclient.lib.util.FileUtils
import intern.line.me.kyotoaclient.presenter.room.GetMembers


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

        val roomId = intent.getSerializableExtra("room_id") as Long
		room = room_repo.getById(roomId)!!

        val roomName: EditText = findViewById(R.id.room_name_text)
        roomName.setText(room.name)
        roomName.setSelection(roomName.text.length)

		adapter = UserSelectListAdapter(this,presenter.getUsersListFromDb())
		val listView: ListView = this.findViewById(R.id.user_select_list)
		listView.adapter = adapter
		registerForContextMenu(listView)

		launch(job + UI) {
			presenter.getUsersList()
		}

        launch(job + UI) {
            val members = GetMembers().getMembers(roomId)
            val memberIdList = members.map { it.id }
            val users = user_repo.getAll()

            adapter.checkList = users.map{ it -> memberIdList.contains(it.id)} as MutableList<Boolean>
            adapter.notifyDataSetChanged()
        }

		launch(job + UI) {
			GetRoomIcon().getRoomIcon(roomId).let {
				val image = BitmapFactory.decodeStream(it)
				edit_room_icon_view.setImageBitmap(image)
			}
		}

        edit_room_icon_view.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try{
            if(requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data!=null){
                val uri = Uri.parse(data.dataString)
                file = FileUtils(this).getFile(uri, "room_icon_id_" + room.id.toString()) ?: throw Exception("no file found")
                val file = file ?: throw Exception("invalid file")

				val image = BitmapFactory.decodeStream(file.inputStream())
				edit_room_icon_view.setImageBitmap(image)
            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEditRoom(v: View) {
        val room = room
        val roomName = (findViewById<TextView>(R.id.room_name_text)).text.toString()
        var selectedUsers = adapter.getCheckedUserList()
        val me = me
        if (me != null){
            selectedUsers = (selectedUsers as MutableList<User>)
            selectedUsers.add(me)
        }

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

    private fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}
