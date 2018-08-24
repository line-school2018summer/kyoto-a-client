package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
import intern.line.me.kyotoaclient.lib.api.interfaces.GetMessagesInActivity
import java.sql.Time
import java.sql.Timestamp
import java.util.*

class MessageActivity : GetMessagesInActivity() {
    private val MESSAGE_EDIT_EVENT = 0
    private val MESSAGE_DELETE_EVENT = 1
    private var editingMessagePosition: Int? = null
    override var messages: MessageList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val roomId = intent.getLongExtra("roomId", -1)
        val room: Room = getRoom(roomId)
        this.title = room.name
        MessagesAdapter(this).execute(roomId)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val adapterInfo: AdapterView.AdapterContextMenuInfo = menuInfo as AdapterView.AdapterContextMenuInfo
        val listView: ListView = v as ListView
        val messageObj = listView.getItemAtPosition(adapterInfo.position) as Message
        val myId: Long = 4
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
        this.messages?.removeAt(id)
        this.drawMessagesList()
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
        messages.updateAt(editingMessagePosition, message)
        originMessage.update(message)
        this.drawMessagesList()
        this.toSendMode()
    }

    fun onSend(v: View) {
        val sendText: EditText = findViewById(R.id.message_send_text)
        if (sendText.text.isBlank()) {
            return
        }
        val created_at = Timestamp(System.currentTimeMillis())
        val newMessage = Message(
                id = Random().nextLong(),
                room_id = 1,
                user_id = 4,
                user = User(
                        id = 4,
                        name = "hoge",
                        created_at = Timestamp(1L),
                        updated_at = Timestamp(1L)
                ),
                text = sendText.text.toString(),
                created_at = created_at,
                updated_at = created_at
        )
        this.messages?.add(newMessage)
        this.drawMessagesList()
        this.toSendMode()
    }

    private fun drawMessagesList(scrollAt: Int? = null) {
        var messages = this.messages
        messages = messages ?: MessageList(mutableListOf(Message(
                id = 1L,
                room_id = 1L,
                user_id = 1L,
                text = "ss",
                user = User(
                        id = 4,
                        name = "hoge",
                        created_at = Timestamp(1L),
                        updated_at = Timestamp(1L)
                ),
                created_at = Timestamp(1L),
                updated_at = Timestamp(1L)
        )))
        val adapter = MessageListAdapter(this)
        adapter.setMessages(messages)
        val listView: ListView = this.findViewById(R.id.main_list)
        var scrollTo = 0
        if (scrollAt == null) {
            scrollTo = listView.count - 1
        } else {
            scrollTo = scrollAt
        }
        listView.adapter = adapter
        listView.setSelection(scrollTo)
        registerForContextMenu(listView)
        val progress: View = this.findViewById(R.id.message_loading)
        listView.visibility = View.VISIBLE
        progress.visibility = View.INVISIBLE
    }

    private fun getRoom(id: Long): Room{
        return Room(1, "my group", Timestamp(47389732489), Timestamp(47389732489), "message", Timestamp(47989732489))
    }

    override fun doMessagesAction() {
        drawMessagesList()
    }

}
