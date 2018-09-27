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


class GetRoom: API(){

	private val api = retrofit.create(RoomsAPI::class.java)
	private val repo = RoomRepository()

	private suspend fun getAsyncRoom(room_id : Long): Room = withContext(CommonPool){
		api.getRoom(room_id).await()
	}

	suspend fun getRoom(room_id: Long): Room{

		try {
			val room =  getAsyncRoom(room_id)
			repo.update(room)
			return room

		} catch (t: HttpException) {
			throw Exception("update failed.")
		}
	}

	fun getRoomsFromDB() : RealmResults<Room>{
		return repo.getAll()
	}
}