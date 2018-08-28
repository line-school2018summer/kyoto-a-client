package intern.line.me.kyotoaclient.lib.api

import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.ChangeMyProfileActivity
import intern.line.me.kyotoaclient.GetUserProfileActivity
import intern.line.me.kyotoaclient.MessageActivity
import intern.line.me.kyotoaclient.UserListActivity
import intern.line.me.kyotoaclient.lib.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException

class GetUserList(val activity: UserListActivity): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool){
        api.getUsers().await()
    }

    private suspend fun getUsersList(){
        try {
            val usersList = getAsyncUsersList()
            activity.setUsers(usersList)

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job + UI) { getUsersList() }
    }
}

class GetUserInfo(val activity: GetUserProfileActivity, val id: Long): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUserInfo(): User = withContext(CommonPool){
        api.getUserInfoById(id).await()
    }

    private suspend fun getUserInfo(){
        try{
            val user = getAsyncUserInfo()
            activity.setUserInfo(user)
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job + UI){ getUserInfo() }
    }
}

class GetMyInfo(val activity: ChangeMyProfileActivity): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
        api.getMyInfo(token).await()
    }

    private suspend fun getMyInfo() {

        val token = FirebaseUtil().getIdToken()

        try {
            if(token != null) {
                val userInfo = getAsyncMyInfo(token)
                activity.setUserInfo(userInfo)
            }

        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start(){
        launch(this.job + UI){ getMyInfo() }
    }
}

class GetMyInfoMessage(val activity: MessageActivity): API(){

    val api = retrofit.create(UserAPI::class.java)
    val util = FirebaseUtil()

    private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
        api.getMyInfo(token).await()
    }

    private suspend fun getMyInfo() {

        val token = util.getIdToken()

        try {
            if(token != null) {
                val userInfo = getAsyncMyInfo(token)
                activity.setUserInfo(userInfo)
            }

        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start(){
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            util.startWithGettingToken(user) {
                launch(this.job + UI) { getMyInfo() }
            }
        }
    }
}

class PutMyInfo( private  val name: String): API(){
    val api = retrofit.create(UserAPI::class.java)

    private suspend fun putAsyncMyInfo(token:String): User = withContext(CommonPool){
        api.changeUserInfo(token, name).await()
    }

    private suspend fun putMyInfo(){
        try {
            val token = FirebaseUtil().getIdToken()

            if(token != null) {
                val userInfo = putAsyncMyInfo(token)
            }
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start(){
        launch(this.job + UI){ putMyInfo() }
    }
}
