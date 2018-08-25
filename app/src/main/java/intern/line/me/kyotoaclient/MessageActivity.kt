package intern.line.me.kyotoaclient

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
import android.widget.AdapterView
import android.widget.ListView
import intern.line.me.kyotoaclient.adapter.MessageListAdapter
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.Room
import intern.line.me.kyotoaclient.lib.api.CreateMessage
import intern.line.me.kyotoaclient.lib.api.GetMessages
import kotlinx.android.synthetic.main.activity_message.*
import java.sql.Timestamp
import android.view.inputmethod.InputMethodManager
import intern.line.me.kyotoaclient.lib.api.DeleteMessage
import intern.line.me.kyotoaclient.lib.api.UpdateMessage


class MessageActivity : AppCompatActivity() {
    private val MESSAGE_EDIT_EVENT = 0
    private val MESSAGE_DELETE_EVENT = 1
    private var editingMessagePosition: Int? = null
    lateinit var room: Room

    lateinit var adapter: MessageListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        //表示するルームを取得
        val roomId = intent.getLongExtra("roomId", -1)
        room = getRoom(roomId)

        if (room != null) {
            this.title = room.name
        } else {
            this.title = "Room"
        }

        //アダプターを設定
        adapter = MessageListAdapter(this)
        main_list.adapter = adapter


        val listView: ListView = this.findViewById(R.id.main_list)
        registerForContextMenu(listView)


        //キーボードを閉じてスタート
        this.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        //メッセージを取得
        message_loading.visibility = View.VISIBLE
        GetMessages(this,roomId).start()
        main_list.visibility = View.VISIBLE


        //送信ボタンをクリック
        message_send_button.setOnClickListener{
            Log.d("on Send Message","start onSend()")
            onSend(it)
        }


        //編集ボタンをクリック
        message_edit_button.setOnClickListener{
            onEdit(it)
        }

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

    private fun onDeleteMessage(position: Int): Boolean {
        val message = adapter.messages[position]
        DeleteMessage(this,position,message).start()
        return true
    }


    private fun onUpdateMessage(position: Int): Boolean {
        this.toEditMode(position)

        val message = adapter.messages[position]
        message_edit_text.setText(message.text)

        return true
    }

    private fun toEditMode(messagePosition: Int): Boolean {
        this.editingMessagePosition = messagePosition

        message_edit_layout.visibility = View.VISIBLE
        message_send_layout.visibility = View.INVISIBLE
        return true
    }

    private fun toSendMode(): Boolean {
        message_edit_layout.visibility = View.INVISIBLE
        message_send_layout.visibility = View.VISIBLE
        message_edit_text.setText("")
        message_send_text.setText("")

        this.editingMessagePosition = null
        return true
    }

    fun onEdit(v: View) {
        val editingMessagePosition = this.editingMessagePosition

        if (editingMessagePosition == null) {
            this.toSendMode()
            return
        }

        val originMessage: Message = adapter.messages[editingMessagePosition]

        //メッセージに変更がない場合
        if (originMessage.text == message_edit_text.toString()){
            this.toSendMode()
            return
        }

        if (message_edit_text.text.isBlank()) return

        UpdateMessage(this,editingMessagePosition,originMessage,message_edit_text.text.toString()).start()

        closeKeyboard(v)
        this.toSendMode()

    }

    fun onSend(v: View) {
        if (message_send_text.text.isBlank()) {
            return
        }

        Log.d("onSend", "start CreateMessage")
        CreateMessage(this, room.id, message_send_text.text.toString()).start()

        closeKeyboard(v)
    }


    fun setMessages(messages: MutableList<Message>){
        adapter.setMessages(messages)
        scrollTo()
        message_loading.visibility = View.INVISIBLE
    }

    fun addMessages(messages: MutableList<Message>){
        adapter.addMessages(messages)
        scrollTo()
        message_loading.visibility = View.INVISIBLE

    }


    fun updateMessage(position: Int, updated_message:Message){
        adapter.updateMessage(position,updated_message)
    }

    fun deleteMessage(position: Int){
        adapter.deleteMessage(position)
        scrollTo()
        message_loading.visibility = View.INVISIBLE
    }

    private fun getRoom(id: Long): Room{
        return Room(1, "my group", Timestamp(47389732489), Timestamp(47389732489), "message", Timestamp(47989732489))
    }


    fun scrollTo(scrollAt: Int? = null){

        var scrollTo = 0
        if (scrollAt == null) {
            scrollTo = main_list.count - 1
        } else {
            scrollTo = scrollAt
        }

        main_list.setSelection(scrollTo)
    }


    fun closeKeyboard(v:View){
        message_send_text.text.clear()
        message_edit_text.text.clear()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }
}
