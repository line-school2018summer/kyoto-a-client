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

    lateinit var adapter: RoomListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        setResult(Activity.RESULT_CANCELED)

        adapter = RoomListAdapter(this)
        val listView: ListView = this.findViewById(R.id.room_list)
        listView.adapter = adapter
        registerForContextMenu(listView)

        //ルーム一覧を非同期で取得
        setAsyncRooms()

        //ルームをクリックしたらトークに飛ぶ
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedRoom = adapter.getItem(position)
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("room_id", selectedRoom.id)
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

    private fun setAsyncRooms(){
        //非同期でルーム取得
        launch(job + UI) {

            //DBから取得
            GetRooms().getRoomsFromDB().let {
                setRooms(it)
            }
        }

        launch(job + UI){
             GetRooms().getRooms().let{
                 setRooms(it)
            }
        }
    }

    private fun setRooms(rooms : List<Room>){
        adapter.setRooms(rooms)
        adapter.notifyDataSetChanged()
    }
}
