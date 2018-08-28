package intern.line.me.kyotoaclient.lib.api

import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.ChangeMyProfileActivity
import intern.line.me.kyotoaclient.GetUserProfileActivity
import intern.line.me.kyotoaclient.UserListActivity
import intern.line.me.kyotoaclient.lib.model.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException

class GetUserList(val callback: (List<User>) -> Unit): API() {

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
        api.getUsers().await()
    }

    private suspend fun getUsersList() {
        try {
            getAsyncUsersList().let{
                callback(it)
            }

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job + UI) {
            getUsersList()
        }
    }
}

class GetUserInfo(val id: Long,val callback:(User) -> Unit): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUserInfo(): User = withContext(CommonPool){
        api.getUserInfoById(id).await()
    }

    private suspend fun getUserInfo(){
        try{
            getAsyncUserInfo().let {
                callback(it)
            }
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job + UI) {
            val user = getUserInfo()
        }
    }
}

class GetMyInfo(val callback:(User) -> Unit): API(){

    val api = retrofit.create(UserAPI::class.java)
    val util = FirebaseUtil()

    private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
        api.getMyInfo(token).await()
    }

    private suspend fun getMyInfo() {

        val token = util.getIdToken()

        try {
            if(token != null) {
                getAsyncMyInfo(token).let{
                    callback(it)
                }
            }

        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            util.startWithGettingToken(user) {
                launch(this.job + UI) {
                    getMyInfo()
                }
            }
        }
    }
}

class PutMyInfo(private  val name: String, val callback:(User) -> Unit): API(){
    val api = retrofit.create(UserAPI::class.java)
    val util = FirebaseUtil()

    private suspend fun putAsyncMyInfo(token:String): User = withContext(CommonPool){
        api.changeUserInfo(token, name).await()
    }

    private suspend fun putMyInfo(){
        try {
            val token = util.getIdToken()

            if(token != null) {
                putAsyncMyInfo(token).let{
                    callback(it)
                }
            }
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            util.startWithGettingToken(user) {
                launch(this.job + UI) {
                    putMyInfo()
                }
            }
        }
    }
}
