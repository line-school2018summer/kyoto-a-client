package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.MessageListAdapter
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import intern.line.me.kyotoaclient.lib.Room
import intern.line.me.kyotoaclient.lib.User
import intern.line.me.kyotoaclient.lib.api.GetMessages
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import kotlinx.coroutines.experimental.Job
import java.sql.Time
import java.sql.Timestamp
import java.util.*

class MessageActivity : AppCompatActivity() {
    private val MESSAGE_EDIT_EVENT = 0
    private val MESSAGE_DELETE_EVENT = 1
    private var editingMessagePosition: Int? = null
    lateinit var room: Room
    private var messagePool: MessagesAdapter? = null
    private var listAdapter: MessageListAdapter? = null

    var messages: MessageList? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        room = intent.getSerializableExtra("room") as Room


        if(room.name.isBlank()){
            this.title = "Room"
        } else {
            this.title = room.name
        }
        
        val listView: ListView = this.findViewById(R.id.main_list)
        listView.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if ((totalItemCount - visibleItemCount) == firstVisibleItem) {
                    val notifyView = findViewById<View>(R.id.message_new_notify)
                    notifyView.visibility = View.INVISIBLE
                }
            }
        })
        registerForContextMenu(listView)

        if(room != null) messagePool = MessagesAdapter(this).getPool(room.id)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val adapterInfo: AdapterView.AdapterContextMenuInfo = menuInfo as AdapterView.AdapterContextMenuInfo
        val listView: ListView = v as ListView
        val messageObj = listView.getItemAtPosition(adapterInfo.position) as Message
        // TODO("myIdをちゃんと取得する")
        val myId: Long = 1
        if (messageObj.user_id == myId){
            menu?.setHeaderTitle(messageObj.text)
            menu?.add(0, MESSAGE_EDIT_EVENT, 0, getString(R.string.edit))
            menu?.add(0, MESSAGE_DELETE_EVENT, 0, getString(R.string.delete))
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info: AdapterView.AdapterContextMenuInfo = item?.getMenuInfo() as AdapterView.AdapterContextMenuInfo

        when(item.itemId){
            MESSAGE_EDIT_EVENT -> onUpdateMessage(info.position)
            MESSAGE_DELETE_EVENT -> onDeleteMessage(info.position)
        }
        return super.onContextItemSelected(item)
    }

    private fun onDeleteMessage(id: Int): Boolean {
        val messages = this.messages ?: return false
        MessagesAdapter(this).delete(id, messages.messageAt(id))
        return true
    }

    fun onUpdate(v: View) {
        println(this.messages)
        drawMessagesList()
    }

    private fun onUpdateMessage(id: Int): Boolean {
        this.toEditMode(id)
        val editInput: EditText = findViewById(R.id.message_edit_text)
        val message: Message = this.messages?.messageAt(id) ?: return false
        editInput.setText(message.text)

        return true
    }

    private fun toEditMode(messagePosition: Int): Boolean {
        this.editingMessagePosition = messagePosition
        val editView: View = findViewById(R.id.message_edit_layout)
        editView.visibility = View.VISIBLE
        val sendView: View = findViewById(R.id.message_send_layout)
        sendView.visibility = View.INVISIBLE
        return true
    }

    private fun toSendMode(): Boolean {
        val editView: View = findViewById(R.id.message_edit_layout)
        editView.visibility = View.INVISIBLE
        val sendView: View = findViewById(R.id.message_send_layout)
        sendView.visibility = View.VISIBLE
        val editText: EditText = findViewById(R.id.message_edit_text)
        editText.setText("")
        val sendText: EditText = findViewById(R.id.message_send_text)
        sendText.setText("")
        this.editingMessagePosition = null
        return true
    }

    fun onEdit(v: View) {
        val editingMessagePosition = this.editingMessagePosition
        if (editingMessagePosition == null) {
            this.toSendMode()
            return
        }
        val messages: MessageList? = this.messages
        if (messages == null) {
            this.toSendMode()
            return
        }
        val editText: EditText = findViewById(R.id.message_edit_text)
        val originMessage: Message = messages.messageAt(editingMessagePosition)
        if (originMessage.text == editText.text.toString()){
            this.toSendMode()
            return
        }
        if (editText.text.isBlank()) {
            return
        }
        var message = originMessage
        message.text = editText.text.toString()
        message.updated_at = Timestamp(System.currentTimeMillis())
        this.messages?.updateAt(editingMessagePosition, message)
        doMessagesAction()
        MessagesAdapter(this).update(editingMessagePosition, originMessage, message)
        this.toSendMode()
    }

    fun onSend(v: View) {
        val sendText: EditText = findViewById(R.id.message_send_text)
        if (sendText.text.isBlank()) {
            return
        }
        val room = room
        if (room == null) {
            this.toSendMode()
            return
        }
        MessagesAdapter(this).create(room.id, sendText.text.toString())
        this.toSendMode()
    }

    fun drawMessagesList(scrollAt: Int? = null) {
        val messages = this.messages
        val listView: ListView = this.findViewById(R.id.main_list)
        val progress: View = this.findViewById(R.id.message_loading)
        if (messages == null) {
            listView.visibility = View.INVISIBLE
            progress.visibility = View.VISIBLE
            return
        }
        val listAdapter = listAdapter
        var scrollTo: Int? = null
        if (listAdapter == null) {
            val newListAdapter = MessageListAdapter(this)
            newListAdapter.setMessages(messages)
            listView.adapter = newListAdapter
            this.listAdapter = newListAdapter
            scrollTo = listView.count - 1
        } else {
            val oldMessages = listAdapter.getMessages()
            if (oldMessages != null) {
                if (messages.getLast().id > oldMessages.getLast().id) {
                    val newMessageNotify: View = this.findViewById(R.id.message_new_notify)
                    newMessageNotify.visibility = View.VISIBLE
                }
            }
            listAdapter.setMessages(messages)
            listAdapter.notifyDataSetChanged()
        }
        if (scrollTo == null) {
            if (scrollAt == null) {
                scrollTo = null
            } else if (scrollAt < 0) {
                scrollTo = listView.count - 1
            } else {
                scrollTo = scrollAt
            }
        }
        if (scrollTo != null) {
            listView.setSelection(scrollTo)
        }
        listView.visibility = View.VISIBLE
        progress.visibility = View.INVISIBLE
    }
    
    fun doMessagesAction(scrollAt: Int? = null) {
        drawMessagesList(scrollAt)
    }

    override fun onStop() {
        super.onStop()
        val messagePool = messagePool
        messagePool ?: return
        messagePool.stopMessagePool()
    }

    override fun onRestart() {
        super.onRestart()
        val room = room
        val messagePool = messagePool
        room ?: return
        messagePool ?: return
        messagePool.getPool(room.id)
    }

    fun scrollToEnd(v: View) {
        val listView: ListView = this.findViewById(R.id.main_list)
        val messages = messages
        messages ?: return
        val last: Int = messages.count.toInt() - 1
        listView.setSelection(last)
    }
}
