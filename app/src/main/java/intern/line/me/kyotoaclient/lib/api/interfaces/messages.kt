package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.lib.Message
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface MessagesAPI {
    @PUT("/messages/{id}")
    fun updateMessage(
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Deferred<Message>

    @DELETE("/messages/{id}")
    fun deleteMessage(
        @Path("id") id: Long
    ): Deferred<Boolean>
}