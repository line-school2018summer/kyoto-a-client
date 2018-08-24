package intern.line.me.kyotoaclient.lib.api.interfaces

import android.support.v7.app.AppCompatActivity
import intern.line.me.kyotoaclient.lib.MessageList

abstract class GetMessagesInActivity: AppCompatActivity() {
    abstract var messages: MessageList?
    abstract fun doMessagesAction ()
}