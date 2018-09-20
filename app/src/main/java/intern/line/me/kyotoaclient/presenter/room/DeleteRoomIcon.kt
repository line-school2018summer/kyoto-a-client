package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class DeleteRoomIcon: API() {

	val api = retrofit.create(RoomsAPI::class.java)

	suspend fun deleteAsyncRoomIcon(id : Long,token : String): Response<Boolean> = withContext(CommonPool){
		api.deleteRoomIcon(id,token).awaitResponse()
	}

	suspend fun deleteRoomIcon(id: Long): Boolean{
		val token = FirebaseUtil().getToken()

		if(token != null){
			try{
				val res = deleteAsyncRoomIcon(id,token)

				if(res.isSuccessful){
					return true
				}else{
					Log.d("deleteRoomIcon", "Can't delete RoomIcon" + res.message() + res.code())
				}
			}catch (e:Throwable){
				Log.e("deleteRoomIcon", "Can't delete RoomIcon",e)
			}
		}
		return false
	}
}