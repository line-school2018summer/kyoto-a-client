package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.InputStream

class GetRoomIcon: API()	 {
	val api = retrofit.create(RoomsAPI::class.java)

	private suspend fun getAsyncRoomIcon(id : Long): Response<ResponseBody> = withContext(CommonPool){
		api.getRoomIcon(id).awaitResponse()
	}

	suspend fun getRoomIcon(id : Long): InputStream?{
		try{
			val res =  getAsyncRoomIcon(id)
			if(res.isSuccessful){
				return  res.body()!!.byteStream()
			}else{
				Log.d("getRoomIcon","Can't get RoomIcon")
			}
		} catch (t: HttpException){
			Log.e("getRoomIcon","Can't get RoomIcon",t)
		}
		return null
	}
}