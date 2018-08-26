package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.lib.Message
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
}
