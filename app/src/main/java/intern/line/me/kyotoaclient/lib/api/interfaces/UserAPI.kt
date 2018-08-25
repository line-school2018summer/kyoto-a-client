package intern.line.me.kyotoaclient.lib.api.interfaces

import intern.line.me.kyotoaclient.lib.User
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface UserAPI{

    @POST("/users")
    fun createUser(
            @Query("name") name: String,
            @Header("Token") token : String
    ) : Deferred<User>

    @GET("/users")
    fun getUsers(): Deferred<List<User>>

    @GET("/users/{id}")
    fun getUserInfoById(@Path("id")id: Long): Deferred<User>

    @GET("/users/me")
    fun getMyInfo(@Header("Token") Token: String): Deferred<User>

    @PUT("/users/me")
    fun changeUserInfo(@Header("Token") Token: String, @Query("name")name: String): Deferred<User>
}