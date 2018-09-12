package intern.line.me.kyotoaclient.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.RoomListAdapter
import intern.line.me.kyotoaclient.model.entity.Room
import android.app.Activity
import android.content.Intent
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.presenter.room.GetRooms
import kotlinx.android.synthetic.main.activity_room_list.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class RoomListActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context): Intent =
                Intent(context, RoomListActivity::class.java)
    }


    private val job = Job()
	private val presenter = GetRooms()

    lateinit var adapter: RoomListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        setResult(Activity.RESULT_CANCELED)

        adapter = RoomListAdapter(this,presenter.getRoomsFromDB())
        val listView: ListView = this.findViewById(R.id.room_list)
        listView.adapter = adapter
        registerForContextMenu(listView)

        //ルーム一覧を非同期で取得
        setAsyncRooms()

        //ルームをクリックしたらトークに飛ぶ
        listView.setOnItemClickListener { parent, view, position, id ->
            val selected_room_id = adapter.getItemId(position)
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("room_id", selected_room_id)
            startActivity(intent)
        }

        user_list_button.setOnClickListener{
            val intent = Intent(application, UserListActivity::class.java)
            startActivity(intent)
        }

        room_create_button.setOnClickListener{
            val intent = Intent(application, RoomCreateActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onRestart() {
        super.onRestart()
        setAsyncRooms()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //非同期でルーム取得
    private fun setAsyncRooms(){

        launch(job + UI){
             GetRooms().getRooms()
        }
    }
}
