package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException

class UpdateMessage(private val activity: MessageActivity, private val position: Int, private var message: Message, private val newMessage: String): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun updateAsyncMessage(token: String): Response<Message> = withContext(CommonPool) {
        api.updateMessage(token, message.id, hashMapOf("text" to newMessage)).awaitResponse()
    }

    private suspend fun updateMessage() {
        val token: String? = FirebaseUtil().getIdToken()

        if (token == null) return

        try {
            val res = updateAsyncMessage(token)
            if(res.isSuccessful){
                activity.updateMessage(position,res.body()!!)
            }else{
                Log.d("Status Code",res.code().toString())
            }

        } catch (t: HttpException) {

        } catch (t: SocketTimeoutException) {

        } catch (t: IOException) {

        }
    }


    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job + UI) { updateMessage() }
            }
        }
    }
}

class DeleteMessage(private val activity: MessageActivity, private val position: Int, private var message: Message): API() {
    val api = retrofit.create(MessagesAPI::class.java)

    private suspend fun deleteAsyncMessage(token: String): Response<HashMap<String, Boolean>> = withContext(CommonPool) {
        api.deleteMessage(token, message.id).awaitResponse()
    }

    private suspend fun deleteMessage(): Boolean {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            return false
        }

        try {
            val res = deleteAsyncMessage(token)

            if (res.isSuccessful) {
                activity.deleteMessage(position)
            } else {
                Log.d("Status Code", res.code().toString())

            }
        } catch (t: HttpException) {

        } catch (t: SocketTimeoutException) {

        } catch (t: IOException) {

        }

        return true
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job + UI) { deleteMessage() }
            }
        }
    }
}
