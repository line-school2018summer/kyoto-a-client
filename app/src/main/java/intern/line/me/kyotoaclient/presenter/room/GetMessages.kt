package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import intern.line.me.kyotoaclient.model.entity.MessageRealm
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await


class GetMessages: API() {
    val api = retrofit.create(RoomsAPI::class.java)
	private val repo = MessageRepository()

    private suspend fun getAsyncMessages(token: String, room_id: Long): List<MessageRealm> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    suspend fun getMessages(room_id: Long) : List<MessageRealm>{
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            val messages =  getAsyncMessages(token, room_id)
			Log.d("GetMessages","start writing DB")

			launch(UI){ repo.updateAll(messages) }

			return messages
        } catch (t: HttpException) {
			Log.v("ROOM_MESSAGES_GETTER", "API failed: 403 forbibdden")
			throw t
		}
    }

	fun getMessagesFromDb(room_id: Long) : List<MessageRealm>{
		return repo.getAll(room_id)
	}
}




