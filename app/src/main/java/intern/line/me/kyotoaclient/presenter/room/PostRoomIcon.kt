package intern.line.me.kyotoaclient.presenter.room

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.File
import android.graphics.BitmapFactory
import intern.line.me.kyotoaclient.lib.util.IconFiles

class PostRoomIcon: API() {

	private val api = retrofit.create(RoomsAPI::class.java)


	private suspend fun postAsyncRoomIcon(room_id: Long, token: String, file: File): Response<Boolean> = withContext(CommonPool) {
		val mime = IconFiles().getMimeTypeOfFile(file.absolutePath)
		val requestBody: RequestBody = RequestBody
				.create(MediaType.parse(mime), file)

		val body: MultipartBody.Part = MultipartBody.Part
				.createFormData("file", file.name, requestBody)

		api.uploadRoomIcon(room_id, token, body).awaitResponse()
	}

	suspend fun postRoomIcon(room_id: Long, file : File): Boolean {
		val token = FirebaseUtil().getToken()

		if (token != null) {
			try {
				val res = postAsyncRoomIcon(room_id, token, file)

				if (res.isSuccessful) {
					return true
				} else {
					Log.d("postRoomIcon", "Can't post RoomIcon" + res.message() + res.code())
				}
			} catch (e: Throwable) {
				Log.d("postRoomIcon", "Can't post RoomIcon", e)
			}
		}
		return false
	}
}