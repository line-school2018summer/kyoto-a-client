package intern.line.me.kyotoaclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.presenter.CreateUserPresenter
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.util.*


class AuthActivity : AppCompatActivity() {

    companion object {
        private const  val RC_SIGN_IN = 123
        private val auth = FirebaseAuth.getInstance()!!

        fun intent(context: Context): Intent =
                Intent(context, AuthActivity::class.java)
    }


    private val job = Job()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //必ずログインするように変更(デバッグのしやすさから)
        auth.signOut()

        if(auth.currentUser != null){
            onCompleteSignIn()
        }else {
            startFirebaseLoginActivity()
        }


        retry_sign_in_button.setOnClickListener{
            faild_to_sign_in_textview.visibility = View.GONE
            retry_sign_in_button.visibility = View.GONE
            auth_progress_bar.visibility = View.VISIBLE

            startFirebaseLoginActivity()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("AuthActivity","start onActivityResult")
        if(requestCode == RC_SIGN_IN){

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK) {

                val user = auth.currentUser!!

                launch(job + UI) {
                    val res = CreateUserPresenter().createUser(user.displayName!!)
                    if (res.isSuccessful) {
                        onCompleteSignIn()
                    } else {
                        showFaildToSignIn()
                    }
                }
            }

            else {
                //ログアウトした後にログインが必要な画面に戻ると発生する
                if(response == null){
                    intent(this).let{startActivity(it)}
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    //FirebaseUIのログインページに飛ぶ
    fun startFirebaseLoginActivity() {

        val providers = Arrays.asList(AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN)
    }

    fun onCompleteSignIn(){
        auth_progress_bar.visibility = View.GONE
        RoomListActivity.intent(this).let { startActivity(it) }
        finish()

    }


    fun showFaildToSignIn(){
        faild_to_sign_in_textview.visibility = View.VISIBLE
        retry_sign_in_button.visibility = View.VISIBLE
        auth_progress_bar.visibility = View.GONE

    }
}
