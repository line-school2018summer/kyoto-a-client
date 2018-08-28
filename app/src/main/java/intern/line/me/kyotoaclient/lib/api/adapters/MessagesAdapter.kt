package intern.line.me.kyotoaclient.lib.api.adapters

import android.os.Handler
import android.view.KeyEvent
import android.widget.Toast
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.lib.model.Message
import intern.line.me.kyotoaclient.lib.model.MessageList
import intern.line.me.kyotoaclient.lib.api.CreateMessage
import intern.line.me.kyotoaclient.lib.api.DeleteMessage
import intern.line.me.kyotoaclient.lib.api.GetMessages
import intern.line.me.kyotoaclient.lib.api.UpdateMessage
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class MessagesAdapter(private var activity: MessageActivity) {
    var messages: MutableList<Message>? = activity.messages?.messages
    var responseCode: Int? = null
    val handler = Handler()
    var running: Boolean = true
    var messagePool: Job? = null

    fun get(roomId: Long) {
        GetMessages(this, roomId).start()
    }

    fun getPool(roomId: Long): MessagesAdapter {
        running = true
        messagePool = GetMessages(this, roomId).startPool()
        return this
    }

    fun create(roomId: Long, text: String) {
        CreateMessage(this, roomId, text).start()
    }

    fun update(position: Int, originMessage: Message, newMessage: Message) {
        UpdateMessage(this, position, originMessage, newMessage).start()
    }

    fun delete(position: Int, message: Message) {
        DeleteMessage(this, position, message).start()
    }

    fun makeToast(string_id: Int, show: Int) {
        Toast.makeText(activity, string_id, show).show()
    }

    fun goBack() {
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }

    fun doMessagesAction(scrollAt: Int? = null) {
        val messages =  this.messages
        if (messages == null) {
            activity.messages = null
        } else {
            activity.messages = MessageList(messages)
        }
        activity.doMessagesAction(scrollAt)
    }

    fun stopMessagePool() {
        val messagePool = messagePool
        messagePool ?: return
        running = false
        launch {
            messagePool.join()
        }
    }
}
