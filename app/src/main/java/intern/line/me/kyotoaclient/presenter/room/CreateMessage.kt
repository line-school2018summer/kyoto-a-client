package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
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

class CreateMessage: API() {
	val api = retrofit.create(RoomsAPI::class.java)
	private val repo = MessageRepository()

	private suspend fun createAsyncMessage(token: String, room_id: Long, text: String): Response<MessageRealm> = withContext(CommonPool) {
		api.createMessage(token, room_id, hashMapOf("text" to text)).awaitResponse()
	}

	suspend fun createMessage(room_id: Long, text: String): MessageRealm {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val res = createAsyncMessage(token, room_id, text)

			if (res.isSuccessful) {
				repo.create(res.body()!!)
				return res.body()!!
			}


		} catch (t: HttpException) {
			Log.v("ROOM_MESSAGES_CREATER", "API failed: 403 forbidden")
			throw t

		} catch (t: SocketTimeoutException) {
			Log.v("ROOM_MESSAGES_CREATER", "API failed: timeout")
			throw t

		} catch (t: IOException) {
			Log.v("ROOM_MESSAGES_CREATER", "API failed: unknown reason")
			throw t

		}
		throw Exception("Can't create MessageRealm.")
	}
}