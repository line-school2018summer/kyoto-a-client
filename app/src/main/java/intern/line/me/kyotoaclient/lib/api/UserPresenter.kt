package intern.line.me.kyotoaclient.lib.api

import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.AuthActivity
import intern.line.me.kyotoaclient.lib.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException

//一つのメソッドに対して一つのクラスを作成。引数はクラスのコンストラクタを利用
class CreateUserPresenter(val name: String): API(){
    private val api = retrofit.create(UserAPI::class.java)

    //FirebaseUtilのインスタンスを作成しないとトークンが共有できない
    private val firebase_cli = FirebaseUtil()

    private suspend fun createASyncUser(name: String,token : String) : User = withContext(CommonPool) {
            api.createUser(name, token).await()
    }

    private suspend fun createUser() {

        //トークンは以下のメソッドで取得できるが、FirebaseUtil.startWithGettingToken{}でラップしていないとnullが返ってくる
        val token = firebase_cli.getIdToken()

        try {
            if (token != null) {
                createASyncUser(name, token)
            } else {
                throw Exception("can't get token.")
            }

        } catch (t: HttpException) {
            throw Exception("can't access server.")
        }
    }

    override fun start() {
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser
        if(user != null) {
            firebase_cli.startWithGettingToken(user) {
                launch(this.job + UI) { createUser() }
            }
        }

    }
}