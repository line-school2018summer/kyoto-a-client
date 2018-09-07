package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.model.entity.MessageRealm
import intern.line.me.kyotoaclient.model.entity.RoomRealm
import retrofit2.Call
import retrofit2.http.*

interface RoomsAPI {
    @GET("/rooms/{id}/messages")
    fun getMessages(
        @Header("Token") token : String,
        @Path("id") id: Long
    ): Call<List<MessageRealm>>

    @POST("/rooms/{id}/messages")
    fun createMessage(
        @Header("Token") token : String,
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Call<MessageRealm>

    @GET("/rooms")
    fun getRooms(
            @Header("Token") token : String
    ): Call<List<RoomRealm>>

    @POST("/rooms")
    fun createRoom(
            @Header("Token") token : String,
            @Body body: HashMap<String, Any>
    ): Call<RoomRealm>
}
