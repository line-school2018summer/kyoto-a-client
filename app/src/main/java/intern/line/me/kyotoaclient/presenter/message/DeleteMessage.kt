package intern.line.me.kyotoaclient.presenter.message

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.MessageRealm
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException

class DeleteMessage: API() {
	val api = retrofit.create(MessagesAPI::class.java)
	private val repo = MessageRepository()

	private suspend fun deleteAsyncMessage(token: String, message: MessageRealm): Response<HashMap<String, Boolean>> = withContext(CommonPool) {
		api.deleteMessage(token, message.id).awaitResponse()
	}

	suspend fun deleteMessage(message: MessageRealm): Boolean {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val res = deleteAsyncMessage(token, message)
			if(res.isSuccessful){
				repo.delete(message)
			}
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