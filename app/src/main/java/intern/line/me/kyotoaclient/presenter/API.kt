package intern.line.me.kyotoaclient.presenter

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class APIInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val request: Request = original.newBuilder()
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build()

        val response: Response? = chain.proceed(request)

        response ?: throw Exception("api connection failed")

        return response
    }
}

open class API {
    protected val job = Job()
    private var debug = true
    private val gson = GsonBuilder().create()
    private var client = OkHttpClient.Builder()
    private val auth = FirebaseAuth.getInstance()


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
