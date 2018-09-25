package intern.line.me.kyotoaclient.presenter.room

import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class GetMembers: API() {
    private val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncMembers(token: String, room_id: Long): List<User> = withContext(CommonPool) {
        api.getMembers(token, room_id).await()
    }

    suspend fun getMembers(room_id: Long): List<User> {
        try {
            val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

            return  getAsyncMembers(token, room_id)

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }
}
