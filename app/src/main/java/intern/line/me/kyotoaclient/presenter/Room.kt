package intern.line.me.kyotoaclient.presenter

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import intern.line.me.kyotoaclient.activity.RoomListActivity
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.Message
import intern.line.me.kyotoaclient.model.Room
import intern.line.me.kyotoaclient.lib.api.adapters.MessagesAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.RoomsAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.User
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import java.lang.Thread.sleep
import java.net.SocketTimeoutException


class GetMessages (private val context: MessagesAdapter, private val room_id:Long): API() {
    val api = retrofit.create(RoomsAPI::class.java)


    private suspend fun getAsyncMessages(token: String, room_id: Long): List<Message> = withContext(CommonPool) {
        api.getMessages(token, room_id).await()
    }

    private suspend fun getMessages(room_id: Long) {
        val token = FirebaseUtil().getToken()

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

    fun start() {
        launch(this.job + UI) {
            getMessages(room_id)
        }

    }

    private suspend fun poolMessages() {

        withContext(CommonPool){

            //裏で常に取得してる
            while (context.running) {
                getMessages(room_id)

                // 0.2秒ごとに終了指示がないか調べる
                for (i in 1..5) {
                    if (!context.running) {
                        return@withContext
                    }
                    sleep(200)
                }
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

        return launch { poolMessages() }
    }
}

class CreateMessage (private val context: MessagesAdapter, private val room_id:Long, private val text: String): API() {
    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun createAsyncMessage(token: String, room_id: Long, text: String): Message = withContext(CommonPool) {
        api.createMessage(token, room_id, hashMapOf("text" to text)).await()
    }

    private suspend fun createMessage(room_id: Long, text: String) {
        val token = FirebaseUtil().getToken()

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

    fun start() {
        launch(this.job) {
            createMessage(room_id, text)
        }

    }
}

class GetRooms(val activity: RoomListActivity): API(){

    val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun getAsyncRooms(token: String): List<Room> = withContext(CommonPool){
        api.getRooms(token).await()
    }

    private suspend fun getRooms(){
        val token = FirebaseUtil().getToken()

        if (token == null) {
            Log.v("ROOMS_GETTER", "API failed: i have no token")
            return
        }
        try {
            val rooms = getAsyncRooms(token)
            activity.setRooms(rooms)

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }

    fun start() {
        launch(this.job + UI) {
            getRooms()
        }

    }
}

class CreateRoom(val name: String, val users: List<User>, val callback:(Room) -> Unit): API(){
    private val api = retrofit.create(RoomsAPI::class.java)

    private suspend fun createAsyncRoom(name: String, userIds: List<Long>, token: String): Room = withContext(CommonPool) {
        api.createRoom(token, hashMapOf("name" to name, "userIds" to userIds)).await()
    }

    private suspend fun createRoom() {
        val token = FirebaseUtil().getToken()

        val userIds = mutableListOf<Long>()

        users.forEach({
            userIds.add(it.id)
        })

        try {
            if (token != null){
                createAsyncRoom(name, userIds, token).let {
                    callback(it)
                }
            } else {
                throw Exception("can't get token.")
            }
        } catch (t: HttpException) {
            throw Exception("can't access server.")
        }
    }

    fun start() {
        launch (this.job + UI) {
            createRoom()
        }
    }
}
