package intern.line.me.kyotoaclient

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AuthActivity : AppCompatActivity() {

    companion object {
        private const  val RC_SIGN_IN = 123
        private val auth = FirebaseAuth.getInstance()!!

        fun intent(context: Context): Intent =
                Intent(context,AuthActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if(auth.currentUser != null){
            RoomListActivity.intent(this).let { startActivity(it) }
        }

        val providers = Arrays.asList(AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build())

        //FirebaseUIのログインページに飛ぶ
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN)

    }


    //戻るボタンで戻ってきたときの処理
    override fun onResume(){
        super.onResume()

        //ログインされてたらもとのページに戻る
        if(auth.currentUser != null){
            finish()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                LoginedActivity.intent(this).let { startActivity(it) }
                return
            }
            else {
                //ログアウトした後にログインが必要な画面に戻ると発生する
                if(response == null){
                    AuthActivity.intent(this).let{startActivity(it)}
                    return
                }
            }
        }
    }
}
