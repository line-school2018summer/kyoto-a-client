package intern.line.me.kyotoaclient.lib.api.adapters

import android.os.Handler
import android.view.KeyEvent
import android.widget.Toast
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import intern.line.me.kyotoaclient.lib.api.GetMessages

class MessagesAdapter(var activity: MessageActivity) {
    var messages: MutableList<Message>? = null
    var responseCode: Int? = null
    val handler = Handler()

    fun get(roomId: Long) {
        GetMessages(this, roomId).start()
    }

    fun makeToast(string_id: Int, show: Int) {
        Toast.makeText(activity, string_id, show).show()
    }

    fun goBack() {
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }

    fun doMessagesAction() {
        val messages =  this.messages
        if (messages == null) {
            activity.messages = null
        } else {
            activity.messages = MessageList(messages)
        }
        activity.doMessagesAction()
    }
}
