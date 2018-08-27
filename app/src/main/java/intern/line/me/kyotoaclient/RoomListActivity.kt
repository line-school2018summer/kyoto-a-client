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