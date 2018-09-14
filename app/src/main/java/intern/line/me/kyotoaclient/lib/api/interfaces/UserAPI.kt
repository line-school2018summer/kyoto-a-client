package intern.line.me.kyotoaclient.lib.api.interfaces


import intern.line.me.kyotoaclient.model.entity.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface UserAPI{

    @POST("/users")
    fun createUser(
            @Query("name") name: String,
            @Header("Token") token : String
    ) : Call<User>

    @GET("/users")
    fun getUsers(): Call<List<User>>

    @GET("/users/{id}")
    fun getUserInfoById(
            @Path("id")id: Long
    ): Call<User>

    @GET("/users/me")
    fun getMyInfo(
            @Header("Token") Token: String
    ): Call<User>

    @PUT("/users/me")
    fun changeUserInfo(
            @Header("Token") Token: String,
            @Query("name")name: String
    ): Call<User>

    @GET("/users/search")
    fun searchUsers(
            @Query("name")name: String
    ): Call<List<User>>

    @Multipart
    @POST("/upload/icon")
    fun uploadIcon(
            @Header("Token") token: String,
            @Body file:  MultipartBody.Part
    ): Call<Boolean>

    @DELETE("/upload/icon")
    fun deleteIcon(
            @Header("Token") token: String
    ): Call<Boolean>

    @GET("/download/icon/{id}")
    fun getIcon(
            @Path("id") id: Long
    ): Call<ResponseBody>
}
