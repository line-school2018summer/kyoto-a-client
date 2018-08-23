package intern.line.me.kyotoaclient

//import android.media.session.MediaSession
import retrofit2.http.*
import rx.Observable

interface UsersApi {

    @GET("/users")
    fun getUsers(): Observable<List<NonUidUser>>

    @GET("/users/{id}")
    fun getUserInfoById(@Path("id")id: Long): Observable<NonUidUser>

    @GET("/users/me")
    fun getUserInfoById(): Observable<NonUidUser>

    @PUT("/users/me")
    fun changeUserInfo(@Body Token: String, @Query("name")name: String): Observable<NonUidUser>

}