package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.EditText
import com.google.gson.Gson
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.MessageListAdapter
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.entity.Message
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.presenter.event.GetMessageEvent
import intern.line.me.kyotoaclient.presenter.event.UpdateModel
import intern.line.me.kyotoaclient.presenter.message.DeleteMessage
import intern.line.me.kyotoaclient.presenter.message.UpdateMessage
import intern.line.me.kyotoaclient.presenter.room.CreateMessage
import intern.line.me.kyotoaclient.presenter.room.GetMessages
import intern.line.me.kyotoaclient.presenter.room.GetRooms
import intern.line.me.kyotoaclient.presenter.user.GetMyInfo

import io.realm.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import ua.naiksoftware.stomp.LifecycleEvent
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader
import ua.naiksoftware.stomp.client.StompClient;







class MessageActivity : AppCompatActivity() {
    private val MESSAGE_EDIT_EVENT = 0
    private val MESSAGE_DELETE_EVENT = 1

    private var editingMessagePosition: Int? = null
    private lateinit var room: Room
    private lateinit var listAdapter: MessageListAdapter

    private var myId: Long? = null

    private val job = Job()

	private val presenter = GetMessages()
	private val event_presenter = GetMessageEvent()
	private val update_event_presenter = UpdateModel()
	private val gson = Gson()
	private val repo = EventRespository()

	private var message_size = 0

	lateinit var client : StompClient

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_message)

		val room_id = intent.getSerializableExtra("room_id") as Long
		room = RoomRepository().getById(room_id)!!

		val messages = presenter.getMessagesFromDb(room.id)
		listAdapter = MessageListAdapter(this, messages)
		main_list.adapter = listAdapter

		drawMessagesList()
		//最新のメッセージまでスクロール
		scrollToEnd()


		launch(job + UI) {
			GetMyInfo().getMyInfo().let { myId = it.id }
			presenter.getMessages(room_id)
		}


		//ルームの名前がない場合はデフォルトを指定
		if (room.name.isBlank()) {
			this.title = "Room"
		} else {
			this.title = room.name
		}

		//メッセージが増えたら新着メッセージ通知を表示
		messages.addChangeListener(RealmChangeListener<RealmResults<Message>>{
			if(it.size - message_size > 0){
				message_new_notify.visibility = View.VISIBLE
				message_size = it.size
			}
		})


		main_list.setOnScrollListener(object : AbsListView.OnScrollListener {
			override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}

			override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
				if ((totalItemCount - visibleItemCount) == firstVisibleItem) {
					message_new_notify.visibility = View.INVISIBLE
				}
			}
		})
		registerForContextMenu(main_list)


		val memberEditButton = findViewById(R.id.member_edit_button) as FloatingActionButton
		memberEditButton.setOnClickListener(View.OnClickListener {
			val intent = Intent(application, RoomMemberActivity::class.java)
			intent.putExtra("room_id", room.id)
			startActivity(intent)
		})

		//ここでポーリングを開始
//		startPool(job, room.id)

		//websocketに接続
		connectStomp()

		//websocketに接続したらREST APIを叩く
		launch(job + UI) {
			val events = event_presenter.getMessageEvent(room_id, (repo.getLatest()?.id ?: 0 )+ 1)
			update_event_presenter.updateAllModel(events)
		}
	}


	//送信ボタンを押した時
	fun onSend(v: View) {
		val sendText: EditText = findViewById(R.id.message_send_text)
		if (sendText.text.isBlank()) {
			return
		}
		val room = room

		launch(job + UI) {
			CreateMessage().createMessage(room.id, sendText.text.toString())
			scrollToEnd()
			toSendMode()
		}

	}


	//アイテムを長押しした時の処理
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val adapterInfo: AdapterView.AdapterContextMenuInfo = menuInfo as AdapterView.AdapterContextMenuInfo

        val messageObj = listAdapter.getItem(adapterInfo.position)!!
        val myId: Long = myId ?: 0

        if (messageObj.user_id == myId){
            menu?.setHeaderTitle(messageObj.text)
            menu?.add(0, MESSAGE_EDIT_EVENT, 0, getString(R.string.edit))
            menu?.add(0, MESSAGE_DELETE_EVENT, 0, getString(R.string.delete))
        }
    }


	//選んだ選択肢によって処理を分岐
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info: AdapterView.AdapterContextMenuInfo = item?.menuInfo as AdapterView.AdapterContextMenuInfo

        when(item.itemId){
            MESSAGE_EDIT_EVENT -> onUpdateMessage(info.position)
            MESSAGE_DELETE_EVENT -> onDeleteMessage(info.position)
        }
        return super.onContextItemSelected(item)
    }


	//削除を選んたとき
    private fun onDeleteMessage(position: Int): Boolean {

		launch(job  + UI) {

			//positionはクリックした場所を表すので存在が保証されていると考える
			val result = DeleteMessage().deleteMessage(listAdapter.getItem(position)!!)

			if(result) {
//				drawMessagesList()
			}else{
				//TODO(削除に失敗した時)
			}
		}
		return true
    }


	//編集を選んだ時
    private fun onUpdateMessage(position: Int): Boolean {
        toEditMode(position)

		//positionはクリックした場所を表すので存在が保証されていると考える
        val message: Message = listAdapter.getItem(position)!!
		message_edit_text.setText(message.text)

        return true
    }


    private fun toEditMode(position: Int){
        editingMessagePosition = position

		message_edit_layout.visibility = View.VISIBLE
		message_send_layout.visibility = View.INVISIBLE
    }


    private fun toSendMode() {
		this.editingMessagePosition = null

		message_edit_layout.visibility = View.INVISIBLE
		message_send_layout.visibility = View.VISIBLE
		message_edit_text.setText("")
		message_send_text.setText("")
    }


	//編集を実行したとき
    fun onEdit(v: View) {

		val position = editingMessagePosition ?:throw Exception("no message found")

        if (editingMessagePosition == null) {
            this.toSendMode()
            return
        }

        if (listAdapter.count == 0) {
            this.toSendMode()
            return
        }

		//positionはクリックした場所を表すので存在が保証されていると考える
		val message: Message = listAdapter.getItem(position)!!

		//何も編集されてなかった時
        if (message.text == message_edit_text.text.toString()){
            this.toSendMode()
            return
        }
		//編集して空欄にした時
        if (message_edit_text.text.isBlank()) {
            return
        }

		//非同期で更新
		launch(job + UI) {
			val res = UpdateMessage().updateMessage(message.id,message_edit_text.text.toString())

			if(res.isSuccessful) {
				toSendMode()
//				drawMessagesList()
			}else{
				//TODO(200以外が返ってきた時)
			}
		}
    }



    private fun drawMessagesList(scrollAt: Int? = null) {
		

		main_list.visibility = View.VISIBLE
		message_loading.visibility = View.INVISIBLE
    }


	//アクティビティが終わるときにポーリングを辞める
    override fun onStop() {
        super.onStop()
		job.cancel()
//		client.disconnect()
    }

    override fun onRestart() {
        super.onRestart()
		room = RoomRepository().getById(room.id)!!
		//ルームの名前がない場合はデフォルトを指定
		if (room.name.isBlank()) {
			this.title = "Room"
		} else {
			this.title = room.name
		}
		startPool(job,room.id)
	}


	fun startPool(job: Job,room_id: Long) {

		val pool_job = Job()

		//結果をUIスレッドで受け取れるように
		launch(job + UI) {

			while (true) {
				//別スレッドで常に取得してる
				withContext(pool_job + CommonPool) {
					// 1秒ごとに取得
					Thread.sleep(1000)
					presenter.getMessages(room_id)
				}
				drawMessagesList()
			}
		}
	}

	//最後までスクロール
    fun scrollToEnd(view:View? = null) {
		val last = (listAdapter.count) - 1
		main_list.setSelection(last)
	}


	fun connectStomp(){
		val util = FirebaseUtil()
		val presenter = GetRooms()

		launch(job + UI) {
			client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://localhost:8080/hello", mapOf("Token" to util.getToken()))

			//
			client.lifecycle()
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe { lifecycleEvent ->
				when (lifecycleEvent.type) {
					LifecycleEvent.Type.OPENED -> Log.d("stomp", "Stomp connection opened")
					LifecycleEvent.Type.CLOSED -> Log.d("stomp", "Stomp connection closed")
					LifecycleEvent.Type.ERROR -> Log.e("stomp", "Stomp connection error", lifecycleEvent.exception)
				}
			}


			client.topic("/topic/rooms/${room.id}/messages",mutableListOf(StompHeader("Token",util.getToken())))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe() {
						Log.d("rooms${room.id}",it.payload) //Stringで渡されるのでGsonなどでクラスに変換する必要がある
						val event = gson.fromJson<Event>(it.payload,Event::class.java)

						launch(job + UI) {
							update_event_presenter.updateModel(event)
						}
			}


			client.connect()

		}
	}

}
