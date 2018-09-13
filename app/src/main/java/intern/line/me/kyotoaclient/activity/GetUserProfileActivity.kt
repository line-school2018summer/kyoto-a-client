package intern.line.me.kyotoaclient.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.GetUserInfo
import kotlinx.android.synthetic.main.activity_get_user_profile.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class GetUserProfileActivity : AppCompatActivity() {

    private val job = Job()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_profile)

        val selectedId = intent.getLongExtra("longTapUserId", 1)

        user_profile_progress_bar.visibility = View.VISIBLE

        launch(job + UI) {
            GetUserInfo(selectedId).getUserInfo().let{ setUserInfo(it) }
        }
    }


    fun setUserInfo(set_user: User) {
        user_name.text = set_user.name
        user_profile_progress_bar.visibility = View.INVISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
