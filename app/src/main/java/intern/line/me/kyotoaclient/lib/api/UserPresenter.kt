package intern.line.me.kyotoaclient.lib.api

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.AuthActivity
import intern.line.me.kyotoaclient.lib.model.User
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

//一つのメソッドに対して一つのクラスを作成。引数はクラスのコンストラクタを利用
class CreateUserPresenter(val name: String,private val activity: AuthActivity): API(){
    private val api = retrofit.create(UserAPI::class.java)

    //FirebaseUtilのインスタンスを作成しないとトークンが共有できない
    private val firebase_cli = FirebaseUtil()

    private suspend fun createASyncUser(name: String,token : String) : Response<User> = withContext(CommonPool) {
            api.createUser(name, token).awaitResponse()
    }

    private suspend fun createUser() {

        //トークンは以下のメソッドで取得できるが、FirebaseUtil.startWithGettingToken{}でラップしていないとnullが返ってくる
        val token = firebase_cli.getIdToken()

        try {
            if (token != null) {
                val res = createASyncUser(name, token)

                if(res.isSuccessful) {
                    //res.body()でUserオブジェクトを取得できる(今回は使わない)
                    activity.onCompleteSignIn()

                }else{
                    //res.code()でレスポンスコードを取得できる
                    Log.d("createUser",res.code().toString())

                    activity.showFaildToSignIn()
                }

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
