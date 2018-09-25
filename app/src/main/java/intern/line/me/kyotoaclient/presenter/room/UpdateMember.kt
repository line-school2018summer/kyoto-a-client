package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.model.repository.RoomRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class UpdateMember(private  val name: String, val users: List<User>, val room: Room): API(){
	private val api = retrofit.create(RoomsAPI::class.java)
	private val firebase_cli = FirebaseUtil()
	private val repo = RoomRepository()

	private suspend fun updateAsyncMember(name: String, userIds: List<Long>, roomId: Long, token: String): Room = withContext(CommonPool){
		api.updateMember(token, roomId, hashMapOf("name" to name, "userIds" to userIds)).await()
	}

	private suspend fun updateAsyncRoomName(name: String, userIds: List<Long>, roomId: Long, token: String): Room = withContext(CommonPool){
		api.updateRoomName(token, roomId, hashMapOf("name" to name, "userIds" to userIds)).await()
	}

	suspend fun updateMember(){
		try {
			val token = firebase_cli.getToken()

			val userIds = mutableListOf<Long>()

			users.forEach{
				userIds.add(it.id)
			}

			println(userIds)

			if(token != null) {
				var room = updateAsyncMember(name, userIds, room.id, token)
				room = updateAsyncRoomName(name, userIds, room.id, token)
				repo.update(room)
			}
		} catch (t: HttpException) {
			Log.e("updateRoomName","Can't update Room Name ",t)
			throw Exception("Update failed.")
		}
	}
}