package intern.line.me.kyotoaclient.presenter.room

import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.RoomRealm
import intern.line.me.kyotoaclient.model.entity.UserRealm
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await


class CreateRoom(val name: String, val users: List<UserRealm>): API(){
	private val api = retrofit.create(RoomsAPI::class.java)
	private val repo = RoomRepository()

	private suspend fun createAsyncRoom(name: String, userIds: List<Long>, token: String): RoomRealm = withContext(CommonPool) {
		api.createRoom(token, hashMapOf("name" to name, "userIds" to userIds)).await()
	}

	suspend fun createRoom(): RoomRealm {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		val userIds = mutableListOf<Long>()

		users.forEach{
			userIds.add(it.id)
		}

		try {
			val room =  createAsyncRoom(name, userIds, token)
			repo.create(room)
			return room

		} catch (t: HttpException) {
			throw Exception("can't access server.")
		}
	}

}