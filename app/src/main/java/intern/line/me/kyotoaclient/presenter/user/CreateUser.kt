package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

//一つのメソッドに対して一つのクラスを作成。引数はクラスのコンストラクタを利用
class CreateUser: API(){
    private val api = retrofit.create(UserAPI::class.java)
    private val repo = UserRepository()

    private suspend fun createASyncUser(name: String) : Response<User> = withContext(CommonPool) {
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

        api.createUser(name, token).awaitResponse()
    }


    suspend fun createUser(name: String): Response<User>{
        try {
            val res =   createASyncUser(name)

            if(res.isSuccessful) {
                repo.create(res.body()!!)
            }

            return res
        } catch (t: HttpException) {
            throw Exception("can't access server.")
        }
    }
}