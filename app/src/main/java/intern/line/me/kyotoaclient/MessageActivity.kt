package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.MessageListAdapter
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import java.sql.Timestamp
import java.util.*

class MessageActivity : AppCompatActivity() {
    private val MESSAGE_EDIT_EVENT = 0
    private val MESSAGE_DELETE_EVENT = 1
    private var editingMessagePosition: Int? = null

    private var messages: MessageList = MessageList(mutableListOf(
            Message(
                    id = 1,
                    room_id = 1,
                    user_id = 1,
                    text = "foundgfsauygfoeufiovbreyiaofgbysadrofvbywoabfvisobveiosbahgiovlsabuigpvp;bsauviprfbgeauvifbvuilaboggvuifdb",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 2,
                    room_id = 1,
                    user_id = 2,
                    text = "hello 1!",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 3,
                    room_id = 1,
                    user_id = 1,
                    text = "どうよ",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 4,
                    room_id = 1,
                    user_id = 4,
                    text = "わーい",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 5,
                    room_id = 1,
                    user_id = 4,
                    text = "たーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 6,
                    room_id = 1,
                    user_id = 4,
                    text = "かばんちゃん！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 7,
                    room_id = 1,
                    user_id = 5,
                    text = "サーバルちゃん！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 8,
                    room_id = 1,
                    user_id = 4,
                    text = "たーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 9,
                    room_id = 1,
                    user_id = 4,
                    text = "すごくたーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 10,
                    room_id = 1,
                    user_id = 4,
                    text = "めっちゃたーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 11,
                    room_id = 1,
                    user_id = 4,
                    text = "やばたーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            ),
            Message(
                    id = 8,
                    room_id = 1,
                    user_id = 4,
                    text = "たーのしー！",
                    createdAt = Timestamp(439208349),
                    updatedAt = Timestamp(352843758)
            )
    ))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        this.drawMessagesList()
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
        this.messages.removeAt(id)
        this.drawMessagesList()
        return true
    }

    private fun onUpdateMessage(id: Int): Boolean {
        this.toEditMode(id)
        val editInput: EditText = findViewById(R.id.message_edit_text)
        val message: Message = this.messages.messageAt(id)
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
        val editText: EditText = findViewById(R.id.message_edit_text)
        var message: Message = this.messages.messageAt(editingMessagePosition)
        message.text = editText.text.toString()
        this.messages.updateAt(editingMessagePosition, message)
        this.drawMessagesList()
        this.toSendMode()
    }

    fun onSend(v: View) {
        val sendText: EditText = findViewById(R.id.message_send_text)
        val created_at = Timestamp(System.currentTimeMillis())
        val newMessage = Message(
                id = Random().nextLong(),
                room_id = 1,
                user_id = 4,
                text = sendText.text.toString(),
                createdAt = created_at,
                updatedAt = created_at
        )
        this.messages.add(newMessage)
        this.drawMessagesList()
        this.toSendMode()
    }

    private fun drawMessagesList() {
        val messages = this.messages
        val adapter = MessageListAdapter(this)
        adapter.setMessages(messages)
        val listView: ListView = this.findViewById(R.id.main_list)
        listView.adapter = adapter
        registerForContextMenu(listView)
    }
}
