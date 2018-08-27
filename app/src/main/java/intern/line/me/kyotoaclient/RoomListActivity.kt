package intern.line.me.kyotoaclient

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.support.design.widget.FloatingActionButton
import intern.line.me.kyotoaclient.adapter.RoomListAdapter
import intern.line.me.kyotoaclient.lib.Room
import intern.line.me.kyotoaclient.lib.RoomList
import java.sql.Timestamp
import java.util.*
import android.app.Activity
import android.content.Intent
import intern.line.me.kyotoaclient.lib.api.GetRooms



class RoomListActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context): Intent =
                Intent(context,RoomListActivity::class.java)
    }

    lateinit var adapter: RoomListAdapter

//    private var rooms: RoomList = RoomList(mutableListOf(
//            Room(
//                    id = 1,
//                    name = "Room1",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 2,
//                    name = "Room2",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 3,
//                    name = "Room3",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 4,
//                    name = "Room4",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 5,
//                    name = "Room5",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 6,
//                    name = "Room6",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 7,
//                    name = "Room7",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 8,
//                    name = "Room8",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            ),
//            Room(
//                    id = 9,
//                    name = "Room9",
//                    createdAt = Timestamp(439208349),
//                    updatedAt = Timestamp(439208349),
//                    latestMessage = "最新のメッセージだよー",
//                    messageTime = Timestamp(439208349)
//            )
//    ))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        setResult(Activity.RESULT_CANCELED)

        val list = findViewById<ListView>(R.id.room_list)

        adapter = RoomListAdapter(this)
        list.adapter = adapter

        //非同期でユーザー取得
        GetRooms(this).start()

        list.setOnItemClickListener{_, _, position, _ ->
            val selectedRoom = adapter.getItem(position)
            val result = Intent()
            result.putExtra("selectedUserId", selectedRoom)
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        val createButton = findViewById(R.id.room_create_button) as FloatingActionButton
        createButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(application, RoomCreateActivity::class.java)
            startActivity(intent)
        })

//        val adapter = RoomListAdapter(this)
//        adapter.setRooms(rooms)
//        val listView: ListView = this.findViewById(R.id.room_list)
//        listView.adapter = adapter
//        registerForContextMenu(listView)
//        listView.setOnItemClickListener { parent, view, position, id ->
//            val intent = Intent(this@RoomListActivity, MessageActivity::class.java)
//            intent.putExtra("room", rooms.roomAt(position))
//            startActivity(intent)
//        }
    }

    fun setRooms(rooms : List<Room>){
        adapter.setRooms(rooms)
    }
}