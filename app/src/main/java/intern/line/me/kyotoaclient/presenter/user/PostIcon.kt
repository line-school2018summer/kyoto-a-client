package intern.line.me.kyotoaclient.presenter.user

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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gildor.coroutines.retrofit.await
import java.io.File
import java.util.concurrent.TimeUnit

class PostIcon(file: File): MultiPartAPI() {
    val api = retrofit.create(UserAPI::class.java)
    val img = file

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

open class MultiPartAPI {
    protected val job = Job()
    //var debug = true
    private val gson = GsonBuilder().create()
    private var client = OkHttpClient.Builder()
    val auth = FirebaseAuth.getInstance()
/*

    init {
        client = client.addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()

            val request = original.newBuilder()
                    .header("Content-Type", "multipart/form-data")
                    .method(original.method(), original.body())
                    .build()

            return@Interceptor chain.proceed(request)
        })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
    }*/

    val retrofit = Retrofit.Builder()
            .baseUrl("https://kyoto-a-api.pinfort.me/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            //.client(client.build())
            .build()
}



/*
class APIInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val request: Request = original.newBuilder()
                .header("Content-Type", "multipart/form-data")
                .method(original.method(), original.body())
                .build()

        val response: Response? = chain.proceed(request)

        response ?: throw Exception("api connection failed")

        return response
    }
}

open class API {
    protected val job = Job()
    var debug = true
    private val gson = GsonBuilder().create()
    private var client = OkHttpClient.Builder()
    val auth = FirebaseAuth.getInstance()


    init {
        client = client.addInterceptor(APIInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .apply {
                    if(debug) {
                        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    }
                }
    }

    val retrofit = Retrofit.Builder()
            .baseUrl("https://kyoto-a-api.pinfort.me/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .build()
}
*/

/*
class ServiceGenerator(){
    public val API_BASE_URL = "https://kyoto-a-api.pinfort.me/"

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    private val builder: Retrofit.Builder = Retrofit.Builder().baseUrl(API_BASE_URL)

    fun <T> createService(serviceClass: Class<T>): T{
        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}*/