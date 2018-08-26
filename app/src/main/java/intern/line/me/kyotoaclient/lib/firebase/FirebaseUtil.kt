package intern.line.me.kyotoaclient.lib.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUtil{
    companion object {
        val auth = FirebaseAuth.getInstance()!!
    }

    private var token : String? = null

    //トークンを取得する際はこのメソッドを使用する。毎回tokenをnullにしてるので、startWithGettingTokenでラップしていない場合はnullが帰る。
    fun getIdToken(): String?{
        val return_token = token
        token =null
        return return_token
    }

    /*高階関数を使ったメソッド。テクい。なんでこんなことをしたかというと、
    user.getIdToken(true)は内部的には非同期処理が行われてるっぽく、.addOnCompleteListener以下でしかトークンを使った処理がかけない。
    (普通に書くと、非同期処理のせいでトークンを取得する前にトークンを使った処理が実行されてしまう)
    使い方はAuthActivityのonActivityResultを参照
     */
    fun <T> startWithGettingToken(user: FirebaseUser,body : () -> T){
        Log.d("Token", "start getIdToken")

        user.getIdToken(true)
        .addOnCompleteListener {
            Log.d("Token", "complete getIdToken")

            if (it.isSuccessful) {
                token = it.result.token
                Log.d("Token", token)
                body()
            }else{
                throw Exception("can't get token.")
            }
        }
    }
}