package intern.line.me.kyotoaclient.presenter.message

import android.util.Log
import intern.line.me.kyotoaclient.model.entity.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException

class GetMessage: API() {

    private val api = retrofit.create(MessagesAPI::class.java)
    private val repo = MessageRepository()

    private suspend fun getAsyncMessage(token: String, id: Long): Response<Message> = withContext(CommonPool) {
        api.getMessage(token, id).awaitResponse()
    }

    suspend fun getMessage(id: Long): Response<Message> {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            val res =  getAsyncMessage(token, id)

            if(res.isSuccessful){
                launch(UI){repo.update(res.body()!!)}
            }

            return res

        } catch (t: HttpException) {
            Log.v("MESSAGE_UPDATER", "API failed: 403 forbidden")
            throw t

        } catch (t: SocketTimeoutException) {
            Log.v("MESSAGE_UPDATER", "API failed: timeout")
            throw t

        } catch (t: IOException) {
            Log.v("MESSAGE_UPDATER", "API failed: unknown reason")
            throw t

        }
    }
}


