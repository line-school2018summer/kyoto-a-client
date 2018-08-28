package intern.line.me.kyotoaclient.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import intern.line.me.kyotoaclient.R
import kotlinx.android.synthetic.main.activity_logined.*

class LoginedActivity : AppCompatActivity() {
    companion object {

        fun intent(context: Context): Intent =
                Intent(context, LoginedActivity::class.java)
    }

    lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logined)

        mAuth = FirebaseAuth.getInstance()


        val user : FirebaseUser? = mAuth.getCurrentUser()

        if (user != null){
            uid.text = "uid : " + user.uid
            name_text_view.text = "name : " + user.displayName
        }


        logout_button.setOnClickListener{
            mAuth.signOut()
            AuthActivity.intent(this).let{startActivity(it)}
            }
    }
}
