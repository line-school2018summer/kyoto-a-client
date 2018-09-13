package intern.line.me.kyotoaclient.lib.api.interfaces
import intern.line.me.kyotoaclient.model.entity.Message
import retrofit2.Call
import retrofit2.http.*

interface MessagesAPI {
    @PUT("/messages/{id}")
    fun updateMessage(
        @Header("Token") token : String,
        @Path("id") id: Long,
        @Body body: HashMap<String, String>
    ): Call<Message>

    @DELETE("/messages/{id}")
    fun deleteMessage(
        @Header("Token") token : String,
        @Path("id") id: Long
    ): Call<HashMap<String, Boolean>>
}
