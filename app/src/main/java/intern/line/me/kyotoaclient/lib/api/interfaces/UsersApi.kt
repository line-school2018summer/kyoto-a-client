package intern.line.me.kyotoaclient.lib.api.interfaces

//import android.media.session.MediaSession
import intern.line.me.kyotoaclient.NonUidUser
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*
import rx.Observable

interface UsersApi {

    @GET("/users")
    fun getUsers(): Deferred<List<NonUidUser>>

    @GET("/users/{id}")
    fun getUserInfoById(@Path("id")id: Long): Deferred<NonUidUser>

    @GET("/users/me")
    fun getMyInfo(@Body Token: String): Deferred<NonUidUser>

    @PUT("/users/me")
    fun changeUserInfo(@Body Token: String, @Query("name")name: String): Deferred<NonUidUser>

}