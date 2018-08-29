package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.model.Message
import intern.line.me.kyotoaclient.model.Room
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface RoomsAPI {
    @GET("/rooms/{id}/messages")
    fun getMessages(
        @Header("Token") token : String,
        @Path("id") id: Long
    ): Deferred<List<Message>>

    @POST("/rooms/{id}/messages")
    fun createMessage(
        @Header("Token") token : String,
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Deferred<Message>

    @GET("/rooms")
    fun getRooms(
            @Header("Token") token : String
    ): Deferred<List<Room>>

    @POST("/rooms")
    fun createRoom(
            @Header("Token") token : String,
            @Body body: HashMap<String, Any>
    ): Deferred<Room>
}
