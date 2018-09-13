package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.model.entity.Message
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.model.entity.User
import retrofit2.Call
import retrofit2.http.*

interface RoomsAPI {
    @GET("/rooms/{id}/messages")
    fun getMessages(
        @Header("Token") token : String,
        @Path("id") id: Long
    ): Call<List<Message>>

    @POST("/rooms/{id}/messages")
    fun createMessage(
        @Header("Token") token : String,
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Call<Message>

    @GET("/rooms")
    fun getRooms(
            @Header("Token") token : String
    ): Call<List<Room>>

    @POST("/rooms")
    fun createRoom(
            @Header("Token") token : String,
            @Body body: HashMap<String, Any>
    ): Call<Room>

    @GET("/rooms/{id}/members")
    fun getMembers(
            @Header("Token") token : String,
            @Path("id") id: Long
    ): Call<List<User>>

    @PUT("/rooms/{id}/members")
    fun updateMember(
            @Header("Token") token : String,
            @Path("id") id: Long,
            @Body body: HashMap<String, Any>
    ): Call<Room>

    @PUT("/rooms/{id}/name")
    fun updateRoomName(
            @Header("Token") token : String,
            @Path("id") id: Long,
            @Body body: HashMap<String, Any>
    ): Call<Room>
}
