package intern.line.me.kyotoaclient.presenter.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.Job
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.File
import java.util.concurrent.TimeUnit

class PostIcon(file: File): API() {
    val api = retrofit.create(UserAPI::class.java)
    val img = file

    val requestBody: RequestBody = RequestBody
            .create(MediaType.parse("multipart/form-data"), file)

    val body: MultipartBody.Part = MultipartBody.Part
            .createFormData("file", file.getName(), requestBody)

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

