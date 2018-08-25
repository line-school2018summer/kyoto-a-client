package intern.line.me.kyotoaclient.lib.api.interfaces

import intern.line.me.kyotoaclient.lib.User
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Header

interface UserAPI{

    @POST("/users")
    fun createUser(
            @Query("name") name: String,
            @Header("Token") token : String
    ) : Call<User>
}