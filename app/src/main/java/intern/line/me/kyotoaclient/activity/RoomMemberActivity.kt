package intern.line.me.kyotoaclient.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.CheckedTextView
import android.widget.ListView
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
import intern.line.me.kyotoaclient.model.entity.*
import intern.line.me.kyotoaclient.presenter.room.UpdateMember
import intern.line.me.kyotoaclient.presenter.user.*
import java.sql.Timestamp
import android.widget.EditText
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.model.repository.UserRepository
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class RoomMemberActivity : AppCompatActivity() {

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

		adapter = UserSelectListAdapter(this,user_repo.getAll())
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
            println(users.map{ it -> member_id_list.contains(it.id)} as MutableList<Boolean>)

        }
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
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
			goBack()
		}
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}