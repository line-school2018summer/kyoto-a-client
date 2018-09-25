package intern.line.me.kyotoaclient.presenter.event

import intern.line.me.kyotoaclient.lib.api.interfaces.EventAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.awaitResponse

class GetRoomEvent : API(){

	private val api = retrofit.create(EventAPI::class.java)
	private val repo = EventRespository()

	private suspend fun getAsyncRoomEvent(token : String, since_id : Long) = withContext(CommonPool){
		api.getRoomEvents(token,since_id.toString()).awaitResponse()
	}

	suspend  fun getRoomEvent(since_id : Long): List<Event>{
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val res = getAsyncRoomEvent(token, since_id)

			if(res.isSuccessful) {
				repo.updateAll(res.body()!!)
			}

			return res.body()!!
		} catch (t: HttpException) {
			throw Exception("can't access server.")
		}

	}
}