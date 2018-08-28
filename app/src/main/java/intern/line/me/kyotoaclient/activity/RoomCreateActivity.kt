package intern.line.me.kyotoaclient.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
import intern.line.me.kyotoaclient.model.User
import java.sql.Timestamp

class RoomCreateActivity : AppCompatActivity() {

    private var users = listOf<User>(
            User(
                    id = 1,
                    name = "User1",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 2,
                    name = "User2",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 3,
                    name = "User3",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 4,
                    name = "User4",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 5,
                    name = "User5",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 6,
                    name = "User6",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 7,
                    name = "User7",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 8,
                    name = "User8",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            ),
            User(
                    id = 9,
                    name = "User9",
                    created_at = Timestamp(439208349),
                    updated_at = Timestamp(439208349)
            )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_create)
        this.drawUsersList()
    }

    private fun drawUsersList() {
        val users = this.users
        val adapter = UserSelectListAdapter(this)
        adapter.setUsers(users)
        val listView: ListView = this.findViewById(R.id.user_select_list)
        listView.adapter = adapter
        registerForContextMenu(listView)
    }
}