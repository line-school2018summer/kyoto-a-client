package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.lib.User
import intern.line.me.kyotoaclient.lib.UserList
import java.sql.Timestamp
import java.util.*

class RoomCreateActivity : AppCompatActivity() {

    private var users = listOf<User>(
            User(
                    id = 1,
                    name = "User1",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 2,
                    name = "User2",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 3,
                    name = "User3",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 4,
                    name = "User4",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 5,
                    name = "User5",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 6,
                    name = "User6",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 7,
                    name = "User7",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 8,
                    name = "User8",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            ),
            User(
                    id = 9,
                    name = "User9",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(439208349)
            )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_create)
        this.drawUsersList()
    }

    private fun drawUsersList() {
        val users = this.users
        val adapter = UserListAdapter(this)
        adapter.setUsers(users)
        val listView: ListView = this.findViewById(R.id.user_select_list)
        listView.adapter = adapter
        registerForContextMenu(listView)
    }
}