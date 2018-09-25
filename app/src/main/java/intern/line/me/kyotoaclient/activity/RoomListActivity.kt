package intern.line.me.kyotoaclient.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.RoomListAdapter
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.presenter.event.GetRoomEvent
import intern.line.me.kyotoaclient.presenter.event.UpdateModel
import intern.line.me.kyotoaclient.presenter.room.GetRooms
import intern.line.me.kyotoaclient.presenter.user.GetMyInfo
import kotlinx.android.synthetic.main.activity_room_list.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.rx1.openSubscription
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompHeader
import ua.naiksoftware.stomp.client.StompClient


class RoomListActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context): Intent =
                Intent(context, RoomListActivity::class.java)
    }


    private var job = Job()
	private val presenter = GetRooms()

    lateinit var adapter: RoomListAdapter
    private var client : StompClient? = null
    private val gson = Gson()
    private val updateEventPresenter = UpdateModel(this)
    private val repo = EventRespository()
    private val eventPresenter = GetRoomEvent()


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
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedRoomId = adapter.getItemId(position)
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("room_id", selectedRoomId)
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

        //websocketに接続
        connectStomp()

        //websocketに接続したらREST APIを叩く
        launch(job + UI) {

            val latest_event_id = repo.getLatestForRooms()?.id ?: 0
            Log.d("Event Rest",latest_event_id.toString())
            val events = eventPresenter.getRoomEvent(latest_event_id+ 1)
            updateEventPresenter.updateAllModel(events)
        }
    }

    private fun connectStomp(){
        val util = FirebaseUtil()

        launch(job + UI) {
            val token = util.getToken()

            try {
                if(token != null) {
                    var myId = 0L
                    GetMyInfo().getMyInfo().let {
                        myId = it.id
                    }
                    client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "https://kyoto-a-api.pinfort.me/hello", mapOf("Token" to token))
                    val res = client!!.topic("/topic/users/$myId/rooms", mutableListOf(StompHeader("Token", token)))
                            .openSubscription()
                    client!!.connect()

                    res.consumeEach {
                        val event = gson.fromJson<Event>(it.payload, Event::class.java)
                        updateEventPresenter.updateModel(event)
                    }
                }
            } catch (e: Throwable) {
                Log.e("connectStomp", "Catch Error", e)
                if(client != null && client!!.isConnected){
                    client!!.disconnect()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        job = Job()
        connectStomp()
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
