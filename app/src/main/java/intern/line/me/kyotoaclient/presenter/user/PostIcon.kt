package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await
import java.io.File

class PostIcon(file: File): API() {
    val api = retrofit.create(UserAPI::class.java)

    val requestBody: RequestBody = RequestBody
            .create(MediaType.parse("multipart/form-data"), file)

    val body: MultipartBody.Part = MultipartBody.Part
            .createFormData("file", file.getName(), requestBody)

    private suspend fun postAsyncIcon(token: String): Boolean{
        return api.uploadIcon(token, body).await()
    }

    suspend fun postIcon(){
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")
        try {
            postAsyncIcon(token)
        } catch (t: HttpException){
            throw Exception("upload failed.")
        }
    }
}