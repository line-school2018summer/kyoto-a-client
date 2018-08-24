package intern.line.me.kyotoaclient.lib.api.adapters

import android.os.AsyncTask
import android.view.KeyEvent
import android.widget.Toast
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import intern.line.me.kyotoaclient.lib.api.GetMessages
import java.lang.Thread.sleep

class MessagesAdapter(var activity: MessageActivity): AsyncTask<Long, Int, Unit>() {
    var messages: MutableList<Message>? = null
    var responseCode: Int? = null

    override fun doInBackground(vararg params: Long?) {
        val roomId = params[0] ?: throw Exception("no room id")
        GetMessages(this, roomId).start()
        for (i in 1..100) {
            // 100 * 100ミリ秒間まつ
            sleep(100)
            // APIリクエストの動作が完了していればresponsecodeがある
            if (responseCode != null) {
                // 200の場合messagesがnot null
                if (messages != null){
                    val mes = messages
                    activity.messages = MessageList(mes ?: break)
                }
                break
            }
        }
        return
    }

    override fun onPostExecute(res: Unit) {
        super.onPostExecute(res)
        if (responseCode == null) {
            makeToast(R.string.api_timeout, Toast.LENGTH_LONG)
            goBack()
            return
        }
        if (responseCode == 400) {
            makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
            goBack()
            return
        } else if (responseCode != 200) {
            makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            goBack()
            return
        }
        activity.drawMessagesList()
    }

    private fun makeToast(string_id: Int, show: Int) {
        Toast.makeText(activity, string_id, show).show()
    }

    private fun goBack() {
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}
