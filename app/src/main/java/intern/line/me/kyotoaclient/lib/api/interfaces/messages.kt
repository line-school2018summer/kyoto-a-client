package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.lib.Message
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface MessagesAPI {
    @PUT("/messages/{id}")
    fun updateMessage(
        @Header("Token") token : String,
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Deferred<Message>

    @DELETE("/messages/{id}")
    fun deleteMessage(
        @Header("Token") token : String,
        @Path("id") id: Long
    ): Deferred<HashMap<String, Boolean>>
}
