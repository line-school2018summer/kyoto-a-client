package intern.line.me.kyotoaclient

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.support.design.widget.FloatingActionButton
import intern.line.me.kyotoaclient.adapter.RoomListAdapter
import intern.line.me.kyotoaclient.lib.model.Room
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

        adapter = RoomListAdapter(this)

        //非同期でユーザー取得
        GetRooms(this).start()

        val listView: ListView = this.findViewById(R.id.room_list)

        listView.adapter = adapter
        registerForContextMenu(listView)

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedRoom = adapter.getItem(position)
            val intent = Intent(this@RoomListActivity, MessageActivity::class.java)
            intent.putExtra("room", selectedRoom)
            startActivity(intent)
        }


        val userListButton = findViewById(R.id.user_list_button) as FloatingActionButton
        userListButton.setOnClickListener(View.OnClickListener {
//            val intent = Intent(application, UserListActivity::class.java)
            startActivity(intent)
        })

        val createButton = findViewById(R.id.room_create_button) as FloatingActionButton
        createButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(application, RoomCreateActivity::class.java)
            startActivity(intent)
        })

    }

    fun setRooms(rooms : List<Room>){
        adapter.setRooms(rooms)
    }
}
