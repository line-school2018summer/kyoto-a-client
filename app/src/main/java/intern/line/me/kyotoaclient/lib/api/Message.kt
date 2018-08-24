package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class UpdateMessage(private val context: MessagesAdapter, private val position: Int, private var message: Message, private val newMessage: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun updateAsyncMessage(token: String, newMessage: Message): Message = withContext(CommonPool) {
        api.updateMessage(token, message.id, hashMapOf("text" to newMessage.text)).await()
    }

    private suspend fun updateMessage(newMessage: Message) {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return
        }
        try {
            val resMessage = updateAsyncMessage(token, newMessage)
            context.responseCode = 200
            val messages = context.messages
            if (messages == null) {
                context.handler.post {
                    context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                    context.goBack()
                }
                return
            }
            // update
            messages[position] = resMessage
            context.messages = messages
            context.handler.post {
                context.doMessagesAction()
            }
        } catch (t: HttpException) {
            context.responseCode = t.response().code()
            context.handler.post {
                context.makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: SocketTimeoutException) {
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_timeout, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: IOException) {
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                context.goBack()
            }
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job) { updateMessage(newMessage) }
            }
        }
    }
}

class DeleteMessage(private val context: MessagesAdapter, private val position: Int, private var message: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun deleteAsyncMessage(token: String, message: Message): HashMap<String, Boolean> = withContext(CommonPool) {
        api.deleteMessage(token, message.id).await()
    }

    private suspend fun deleteMessage(message: Message): Boolean {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return false
        }
        try {
            val res = deleteAsyncMessage(token, message)
            context.responseCode = 200
            val messages = context.messages
            if (messages == null) {
                context.handler.post {
                    context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                    context.goBack()
                }
                return false
            }
            val resResult = res["result"]
            resResult ?: throw IOException()
            if (resResult) {
                messages.removeAt(position)
            }
            context.messages = messages
            context.handler.post {
                context.doMessagesAction()
            }
            return true
        } catch (t: HttpException) {
            context.responseCode = t.response().code()
            context.handler.post {
                context.makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: SocketTimeoutException) {
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_timeout, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: IOException) {
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                context.goBack()
            }
        }
        return false
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job) { deleteMessage(message) }
            }
        }
    }
}
