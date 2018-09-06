package intern.line.me.kyotoaclient.presenter

import android.util.Log
import intern.line.me.kyotoaclient.model.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException

class UpdateMessage: API() {

    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun updateAsyncMessage(token: String, newMessage: Message): Response<Message> = withContext(CommonPool) {
        api.updateMessage(token, newMessage.id, hashMapOf("text" to newMessage.text)).awaitResponse()
    }

    suspend fun updateMessage(newMessage: Message): Response<Message> {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            return updateAsyncMessage(token, newMessage)

        } catch (t: HttpException) {
            Log.v("MESSAGE_UPDATER", "API failed: 403 forbidden")
            throw t

        } catch (t: SocketTimeoutException) {
            Log.v("MESSAGE_UPDATER", "API failed: timeout")
            throw t

        } catch (t: IOException) {
            Log.v("MESSAGE_UPDATER", "API failed: unknown reason")
            throw t

        }
    }
}

class DeleteMessage: API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun deleteAsyncMessage(token: String, message: Message): Response<HashMap<String, Boolean>> = withContext(CommonPool) {
        api.deleteMessage(token, message.id).awaitResponse()
    }

    suspend fun deleteMessage(message: Message): Boolean {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            val res = deleteAsyncMessage(token, message)
            return res.isSuccessful

        } catch (t: HttpException) {
            Log.v("MESSAGE_DELETER", "API failed: 403 forbidden")
            return false

        } catch (t: SocketTimeoutException) {
            Log.v("MESSAGE_DELETER", "API failed: timeout")
            return false

        } catch (t: IOException) {
            Log.v("MESSAGE_DELETER", "API failed: unknown reason")
            return false

        }
        return false
    }
}
