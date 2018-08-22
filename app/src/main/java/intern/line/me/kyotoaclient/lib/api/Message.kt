package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException

class Update(private var message: Message, private val newMessage: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun updateAsyncMessage(newMessage: Message): Message = withContext(CommonPool) {
        api.updateMessage(message.id, hashMapOf("text" to message.text)).await()
    }

    private suspend fun updateMessage(newMessage: Message) {
        try {
            val resMessage = updateAsyncMessage(newMessage)
            message = resMessage
        } catch (t: HttpException) {
            throw Exception("message update failed")
        }
    }

    override fun start() {
        launch(this.job) { updateMessage(newMessage) }
    }
}

class Delete(private var message: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun deleteAsyncMessage(message: Message): Boolean = withContext(CommonPool) {
        api.deleteMessage(message.id).await()
    }

    private suspend fun deleteMessage(message: Message): Boolean {
        try {
            val res = deleteAsyncMessage(message)
            return res
        } catch (t: Throwable) {
            throw Exception("message deletion failed")
        }
    }

    override fun start() {
        launch(this.job) { deleteMessage(message) }
    }
}
