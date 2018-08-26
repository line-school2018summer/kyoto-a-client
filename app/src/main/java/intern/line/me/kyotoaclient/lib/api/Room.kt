package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.MessagesAPI
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Thread.sleep
import java.net.ConnectException
import java.net.SocketTimeoutException


class GetMessages (private val context: MessagesAdapter, private val room_id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    val util = FirebaseUtil()


    private suspend fun getAsyncMessages(token: String, room_id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    private suspend fun getMessages(room_id: Long) {
        val token: String? = util.getIdToken()
        if (token == null) {
            Log.v("ROOM_MESSAGES_GETTER", "API failed: i have no token")
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return
        }
        try {
            val resMessages = getAsyncMessages(token, room_id)
            val messages = resMessages as MutableList<Message>?
            context.responseCode = 200
            context.messages = messages
            context.handler.post {
                context.doMessagesAction()
            }
        } catch (t: HttpException) {
            Log.v("ROOM_MESSAGES_GETTER", "API failed: 403 forbibdden")
            context.responseCode = t.response().code()
            context.handler.post {
                context.makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: SocketTimeoutException) {
            Log.v("ROOM_MESSAGES_GETTER", "API failed: timeout")
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_timeout, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: IOException) {
            Log.v("ROOM_MESSAGES_GETTER", "API failed: unknown reason")
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                context.goBack()
            }
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            util.startWithGettingToken(user) {
                launch(this.job + UI) { getMessages(room_id) }
            }
        }
    }

    private suspend fun poolMessages(user: FirebaseUser) {
        while (context.running) {
            util.startWithGettingToken(user) {
                launch(UI) {
                    getMessages(room_id)
                }
            }
            while (util.ret == null) {
                sleep(10)
            }
            (util.ret as Job).join()
            // 0.2秒ごとに終了指示がないか調べる
            for (i in 1..5) {
                if (!context.running) {
                    return
                }
                sleep(200)
            }
        }
    }

    fun startPool(): Job? {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user == null) {
            context.responseCode = 403
            context.handler.post {
                context.makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
                context.goBack()
            }
            return null
        }
        return launch { poolMessages(user) }
    }
}

class CreateMessage (private val context: MessagesAdapter, private val room_id:Long, private val text: String): API() {
    val api = retrofit.create(RoomsAPI::class.java)
    val util = FirebaseUtil()

    private suspend fun createAsyncMessage(token: String, room_id: Long, text: String):Message = withContext(CommonPool) {
        api.createMessage(token, room_id, hashMapOf("text" to text)).await()
    }

    private suspend fun createMessage(room_id: Long, text: String) {
        val token: String? = util.getIdToken()
        if (token == null) {
            Log.v("ROOM_MESSAGES_CREATER", "API failed: i have no token")
            context.responseCode = 500
            context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
            context.goBack()
            return
        }
        try {
            val resMessage = createAsyncMessage(token, room_id, text)
            context.responseCode = 200
            context.messages?.add(resMessage)
            context.handler.post {
                context.doMessagesAction(-1)
            }
        } catch (t: HttpException) {
            Log.v("ROOM_MESSAGES_CREATER", "API failed: 403 forbidden")
            context.responseCode = t.response().code()
            context.handler.post {
                context.makeToast(R.string.api_forbidden, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: SocketTimeoutException) {
            Log.v("ROOM_MESSAGES_CREATER", "API failed: timeout")
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_timeout, Toast.LENGTH_LONG)
                context.goBack()
            }
        } catch (t: IOException) {
            Log.v("ROOM_MESSAGES_CREATER", "API failed: unknown reason")
            context.responseCode = 500
            context.handler.post {
                context.makeToast(R.string.api_failed, Toast.LENGTH_LONG)
                context.goBack()
            }
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            util.startWithGettingToken(user) {
                launch(this.job) { createMessage(room_id, text) }
            }
        }
    }
}