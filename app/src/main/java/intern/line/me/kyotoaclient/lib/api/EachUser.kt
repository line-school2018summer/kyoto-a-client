package intern.line.me.kyotoaclient.lib.api

import com.firebase.ui.auth.data.model.User
import intern.line.me.kyotoaclient.NonUidUser
import intern.line.me.kyotoaclient.adapter.EachUserListAdapter
import intern.line.me.kyotoaclient.lib.api.interfaces.UsersApi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException

class GetUserList(): API(){

    val api = retrofit.create(UsersApi::class.java)

    private suspend fun getAsyncUsersList(): Array<NonUidUser> = withContext(CommonPool){
        api.getUsers().await()
    }

    private suspend fun getUsersList() : Array<NonUidUser>{
        try {
            val usersList = getAsyncUsersList()
            return usersList
        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job) { getUsersList() }
    }
}

class GetUserInfo(private val id: Long): API(){

    val api = retrofit.create(UsersApi::class.java)

    private suspend fun getAsyncUserInfo(): NonUidUser = withContext(CommonPool){
        api.getUserInfoById(id).await()
    }

    private suspend fun getUserInfo(): NonUidUser{
        try{
            val userInfo = getAsyncUserInfo()
            return userInfo
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }

    override fun start() {
        launch(this.job){ getUserInfo() }
    }
}

class GetMyInfo(private val Token: String): API(){

    val api = retrofit.create(UsersApi::class.java)

    private suspend fun getAsyncMyInfo(): NonUidUser = withContext(CommonPool){
        api.getMyInfo(Token).await()
    }

    private suspend fun getMyInfo(): NonUidUser {
        try {
            val userInfo = getAsyncMyInfo()
            return userInfo
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start(){
        launch(this.job){ getMyInfo() }
    }
}

class PutMyInfo(private val Token: String, private  val name: String): API(){
    val api = retrofit.create(UsersApi::class.java)

    private suspend fun putAsyncMyInfo(): NonUidUser = withContext(CommonPool){
        api.changeUserInfo(Token, name).await()
    }

    private suspend fun putMyInfo(): NonUidUser{
        try {
            val userInfo = putAsyncMyInfo()
            return userInfo
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    override fun start(){
        launch(this.job){ putMyInfo() }
    }
}
