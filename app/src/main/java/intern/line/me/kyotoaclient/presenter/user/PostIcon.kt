package intern.line.me.kyotoaclient.presenter.user

import android.util.Log
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.lib.util.IconFiles
import intern.line.me.kyotoaclient.presenter.API
import okhttp3.*
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.File

class PostIcon(file: File): API() {
    private val api = retrofit.create(UserAPI::class.java)

    private val mime = IconFiles(). getMimeTypeOfFile(file.absolutePath)
    private val requestBody: RequestBody = RequestBody
			.create(MediaType.parse(mime), file)

    val body: MultipartBody.Part = MultipartBody.Part
            .createFormData("file", file.name, requestBody)

    private suspend fun postAsyncIcon(token: String): Response<Boolean>{
        return api.uploadIcon(token, body).awaitResponse()
    }

    suspend fun postIcon(){
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            val res = postAsyncIcon(token)
            Log.d("postIcon",res.toString())
            if(res.isSuccessful){
                Log.d("postIcon","Success")
            }else{
                Log.d("postIcon",res.message() + res.code())
            }
         } catch (t: HttpException){
            Log.e("postIcon","catch Error",t)
            throw Exception("upload failed.")
        }
    }
}
