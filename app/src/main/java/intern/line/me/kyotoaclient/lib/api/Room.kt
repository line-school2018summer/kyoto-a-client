package intern.line.me.kyotoaclient.lib.api

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException


class GetMessages (private val context: MessagesAdapter, private val room_id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncMessages(token: String, room_id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    private suspend fun getMessages(room_id: Long) {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return
        }
        try {
            val resMessages = getAsyncMessages(token, room_id)
            val messages = resMessages as MutableList<Message>?
            context.responseCode = 200
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
                launch(this.job) { getMessages(room_id) }
            }
        }
    }
}

class CreateMessage (private val context: MessagesAdapter, private val room_id:Long, private val text: String): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun createAsyncMessage(token: String, room_id: Long, text: String):Message = withContext(CommonPool) {
        api.createMessage(token, room_id, hashMapOf("text" to text)).await()
    }

    private suspend fun createMessage(room_id: Long, text: String) {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return
        }
        try {
            val resMessage = createAsyncMessage(token, room_id, text)
            context.responseCode = 200
            context.messages?.add(resMessage)
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
                launch(this.job) { createMessage(room_id, text) }
            }
        }
    }
}
