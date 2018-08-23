package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException

class MessageUpdate(private var message: Message, private val newMessage: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun updateAsyncMessage(token: String, newMessage: Message): Message = withContext(CommonPool) {
        api.updateMessage(token, message.id, hashMapOf("text" to newMessage.text)).await()
    }

    private suspend fun updateMessage(newMessage: Message) {
        val token: String? = FirebaseUtil().getIdToken()
        token ?: throw Exception("message update failed")
        try {
            val resMessage = updateAsyncMessage(token, newMessage)
            message = resMessage
        } catch (t: HttpException) {
            throw Exception("message update failed")
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

class MessageDelete(private var message: Message): API() {
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
