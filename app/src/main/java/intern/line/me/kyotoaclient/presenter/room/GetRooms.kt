package intern.line.me.kyotoaclient.presenter.room

import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.presenter.API
import io.realm.RealmResults
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await


class GetRooms: API(){

	private val api = retrofit.create(RoomsAPI::class.java)
	private val repo = RoomRepository()

	private suspend fun getAsyncRooms(token: String): List<Room> = withContext(CommonPool){
		api.getRooms(token).await()
	}

	suspend fun getRooms(): List<Room>{
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val rooms =  getAsyncRooms(token)
			repo.updateAll(rooms)
			return rooms

		} catch (t: HttpException) {
			throw Exception("update failed.")
		}
	}

	fun getRoomsFromDB() : RealmResults<Room>{
		return repo.getAll()
	}
}