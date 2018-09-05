package intern.line.me.kyotoaclient.presenter

import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.model.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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


    fun start(name: String, callback:(Response<User>) -> Unit) {

        //この中では同期的に処理をかける！！！！
        launch(this.job + UI) {
            try {
                createASyncUser(name).let {
                    callback(it)
                }
            } catch (t: HttpException) {
                throw Exception("can't access server.")
            }
        }
    }
}


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

    fun start() {
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

    fun start() {
        launch(this.job + UI) {
            getUserInfo()
        }
    }
}

class GetMyInfo(val callback:(User) -> Unit): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
        api.getMyInfo(token).await()
    }

    private suspend fun getMyInfo() {
        val token = FirebaseUtil().getToken()

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

    fun start() {
        launch(this.job + UI) {
            getMyInfo()
        }
    }
}

class PutMyInfo(private  val name: String, val callback:(User) -> Unit): API(){

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun putAsyncMyInfo(token:String): User = withContext(CommonPool){
        api.changeUserInfo(token, name).await()
    }

    private suspend fun putMyInfo(){
        val token = FirebaseUtil().getToken()

        try {
            if(token != null) {
                putAsyncMyInfo(token).let{
                    callback(it)
                }
            }
        } catch (t: HttpException) {
            throw Exception("Update failed.")
        }
    }

    fun start() {
        launch(this.job + UI) {
            putMyInfo()
        }
    }
}

class SearchUsers(val name: String, val callback: (List<User>) -> Unit): API() {

    val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
        api.searchUsers(name).await()
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

    fun start() {
        launch(this.job + UI) {
            getUsersList()
        }
    }
}