package intern.line.me.kyotoaclient.presenter

import intern.line.me.kyotoaclient.model.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse

//一つのメソッドに対して一つのクラスを作成。引数はクラスのコンストラクタを利用
class CreateUserPresenter: API(){
    private val api = retrofit.create(UserAPI::class.java)


    private suspend fun createASyncUser(name: String) : Response<User> = withContext(CommonPool) {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        api.createUser(name, token).awaitResponse()
    }


    suspend fun createUser(name: String): Response<User>{
        try {
            return createASyncUser(name)
        } catch (t: HttpException) {
            throw Exception("can't access server.")
        }
    }
}


class GetUserList: API() {

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
        api.getUsers().await()
    }

    suspend fun getUsersList(): List<User> {
        try {
            return getAsyncUsersList()

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }
}

class GetUserInfo(val id: Long): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUserInfo(): User = withContext(CommonPool){
        api.getUserInfoById(id).await()
    }

    suspend fun getUserInfo(): User{
        try{
            return getAsyncUserInfo()
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }
}

class GetMyInfo: API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
        api.getMyInfo(token).await()
    }

    suspend fun getMyInfo(): User{
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            return getAsyncMyInfo(token)

        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }
}

class PutMyInfo(private  val name: String): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun putAsyncMyInfo(token:String): User = withContext(CommonPool){
        api.changeUserInfo(token, name).await()
    }

    suspend fun putMyInfo(): User{
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        try {
            return putAsyncMyInfo(token)
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }
}

class SearchUsers(val name: String): API() {

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
        api.searchUsers(name).await()
    }

    suspend fun getUsersList(): List<User> {
        try {
            return getAsyncUsersList()

        } catch (t: HttpException) {
            throw Exception("update failed.")
        }
    }
}