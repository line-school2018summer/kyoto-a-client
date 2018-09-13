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
import intern.line.me.kyotoaclient.model.Room
import intern.line.me.kyotoaclient.model.User
import intern.line.me.kyotoaclient.presenter.UpdateMember
import intern.line.me.kyotoaclient.presenter.GetMyInfo
import intern.line.me.kyotoaclient.presenter.GetUserList
import java.sql.Timestamp
import android.widget.EditText



class RoomMemberActivity : AppCompatActivity() {

    lateinit var room: Room

    private var users: List<User>? = null
        set(value) {
            field = value
            this.drawUsersList()
        }

    private var me: User? = null

    private var adapter: UserSelectListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_member)

        room = intent.getSerializableExtra("room") as Room

        val et = findViewById(R.id.room_name_text) as TextView
        et.setText(room.name)

        GetUserList{
            this.users = it
        }.start()
    }

    private fun drawUsersList() {
        val users = this.users
        val adapter = UserSelectListAdapter(this)
        if (users != null){
            adapter.setUsers(users)
        }
        val listView: ListView = this.findViewById(R.id.user_select_list)
        listView.adapter = adapter
        this.adapter = adapter
        registerForContextMenu(listView)
    }

    fun onEditRoom(v: View) {
        println("on click!")
        val room = room
        val roomName = (findViewById<TextView>(R.id.room_name_text)).text.toString()
        var selectedUsers = adapter?.getCheckedUserList()
        val me = me
        if (me != null){
            selectedUsers = (selectedUsers as MutableList<User>)
            selectedUsers.add(me)
        }
        println(selectedUsers)
        selectedUsers ?: return
        UpdateMember (roomName, selectedUsers, room) {
            goBack()
        }.start()
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}