package intern.line.me.kyotoaclient.presenter

import android.util.Log
import intern.line.me.kyotoaclient.model.Message
import intern.line.me.kyotoaclient.model.Room
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.User
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException


class GetMessages:API() {
    val api = retrofit.create(RoomsAPI::class.java)


    private suspend fun getAsyncMessages(token: String, room_id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    suspend fun getMessages(room_id: Long) : List<Message>{
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            return getAsyncMessages(token, room_id)
        } catch (t: HttpException) {
			Log.v("ROOM_MESSAGES_GETTER", "API failed: 403 forbibdden")
			throw t
		}
    }
}

class CreateMessage: API() {
	val api = retrofit.create(RoomsAPI::class.java)

	private suspend fun createAsyncMessage(token: String, room_id: Long, text: String): Response<Message> = withContext(CommonPool) {
		api.createMessage(token, room_id, hashMapOf("text" to text)).awaitResponse()
	}

	suspend fun createMessage(room_id: Long, text: String): Message {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val res = createAsyncMessage(token, room_id, text)

			if (res.isSuccessful) return res.body()!!


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
		throw Exception("Can't create Message.")
	}
}

class GetRooms: API(){

    private val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncRooms(token: String): List<Room> = withContext(CommonPool){
        api.getRooms(token).await()
    }

    suspend fun getRooms(): List<Room>{
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            return getAsyncRooms(token)

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }
}

class CreateRoom(val name: String, val users: List<User>): API(){
    private val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun createAsyncRoom(name: String, userIds: List<Long>, token: String): Room = withContext(CommonPool) {
        api.createRoom(token, hashMapOf("name" to name, "userIds" to userIds)).await()
    }

    suspend fun createRoom(): Room {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        val userIds = mutableListOf<Long>()

        users.forEach{
            userIds.add(it.id)
        }

        try {
            return createAsyncRoom(name, userIds, token)

        } catch (t: HttpException) {
            throw Exception("can't access server.")
        }
    }

}
