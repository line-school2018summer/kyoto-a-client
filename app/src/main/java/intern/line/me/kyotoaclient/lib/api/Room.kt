package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
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


class GetMessages (private val activity: MessageActivity, private val room_id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncMessages(token: String, room_id: Long): Response<MutableList<Message>> = withContext(CommonPool) {
        api.getMessages(token, room_id).awaitResponse()
    }

    private suspend fun getMessages() {

        val token: String? = FirebaseUtil().getIdToken()

        //トークンを取得できなかった場合
        if (token == null) {
            return
        }else{
            try {
                val res = getAsyncMessages(token, room_id)

                if (res.isSuccessful) {
                    activity.setMessages(res.body()!!)
                }else{
                    Log.d("Status Code",res.code().toString())

                }
            } catch (t: HttpException) {

            } catch (t: SocketTimeoutException) {

            } catch (t: IOException) {

            }
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job + UI) { getMessages() }
            }
        }
    }
}


class CreateMessage (private val activity: MessageActivity, private val room_id:Long, private val text: String): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun createAsyncMessage(token: String):Response<Message> = withContext(CommonPool) {
        api.createMessage(token, room_id, hashMapOf("text" to text)).awaitResponse()
    }

    private suspend fun createMessage() {
        val token: String? = FirebaseUtil().getIdToken()
        if (token == null) {
            //トークンを取得できなかった場合
            return
        } else {
            try {
                val res = createAsyncMessage(token)
                if (res.isSuccessful) {
                    activity.addMessages(mutableListOf(res.body()!!))
                } else {
                    Log.d("Status Code", res.code().toString())

                }
            } catch (t: HttpException) {

            } catch (t: SocketTimeoutException) {

            } catch (t: IOException) {

            }
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            FirebaseUtil().startWithGettingToken(user) {
                launch(this.job + UI) { createMessage() }
            }
        }
    }
}
