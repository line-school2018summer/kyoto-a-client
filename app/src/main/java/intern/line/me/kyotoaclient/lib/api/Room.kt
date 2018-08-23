package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException

class GetMessages(private val id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncMessages(id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(id).await()
    }

    private suspend fun getMessages(id: Long) {
//        try {
//            val resMessages = getAsyncMessages(id)
//            message = resMessages
//        } catch (t: HttpException) {
//            throw Exception("message update failed")
//        }
    }

    override fun start() {
//        launch(this.job) { updateMessage(newMessage) }
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
