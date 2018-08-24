package intern.line.me.kyotoaclient.lib.api

import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import java.net.ConnectException


class GetMessages (private val context: MessagesAdapter, private val room_id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncMessages(token: String, room_id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    private suspend fun getMessages(room_id: Long) {
        val token: String? = FirebaseUtil().getIdToken()
        token ?: throw Exception("message update failed")
        try {
            val resMessages = getAsyncMessages(token, room_id)
            context.responseCode = 200
            context.messages = resMessages as MutableList<Message>?
        } catch (t: HttpException) {
            context.responseCode = t.response().code()
            return
        } catch (t: ConnectException) {
            context.responseCode = 500
            return
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

class Delete(private var message: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun deleteAsyncMessage(token: String, message: Message): Boolean = withContext(CommonPool) {
        api.deleteMessage(token, message.id).await()
    }

    private suspend fun deleteMessage(message: Message): Boolean {
        val token: String? = FirebaseUtil().getIdToken()
        token ?: throw Exception("message update failed")
        try {
            val res = deleteAsyncMessage(token, message)
            return res
        } catch (t: Throwable) {
            throw Exception("message deletion failed")
        }
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
