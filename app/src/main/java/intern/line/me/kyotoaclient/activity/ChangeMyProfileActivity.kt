package intern.line.me.kyotoaclient.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.util.FileUtils
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.*
import kotlinx.android.synthetic.main.activity_change_my_profile.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.UnsupportedEncodingException

class ChangeMyProfileActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777
    lateinit var file: File

    private val job = Job()
    private var myId = 0L
    private val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        my_profile_progress_bar.visibility = View.VISIBLE

        //非同期でユーザー情報を取ってくる
        launch(job + UI) {
            GetMyInfo().getMyInfo().let {
                setUserInfo(it)
                setImgByC(it.id)
                myId = it.id
            }
        }

        if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        //ボタンを押したときの処理
        apply_button.setOnClickListener {

            val inputText = changed_name.text.toString()

            launch(job + UI) {
                if (regex.matches(inputText) ) {
                    PutMyInfo(inputText).putMyInfo().let { setUserInfo(it) }
                    changed_name.text.clear()
                } else {
                    changed_name.error = "不正な名前です"
                }
            }
        }

        //画像変更ボタン
        image_change.setOnClickListener{


            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }

        //画像削除ボタン
        image_delete.setOnClickListener {

            launch(job + UI) {
                DeleteIcon().deleteIcon()
                setImgByC(myId)
                UserListAdapter.list.clear()
            }
        }
    }

    //Activityを閉じたときに非同期処理をキャンセルさせる
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //主に画像選択後の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data != null) {
                val uri = Uri.parse(data.dataString)

                val f = FileUtils(this).getFile(uri, "user_icon_id_" + myId.toString())
                if (f == null) {
                    Toast.makeText(this, "適切でないファイル形式です", Toast.LENGTH_SHORT).show()
                } else {
                    file = f
                    launch(job + UI) {
                        PostIcon(file).postIcon()
                        setImgByC(myId)
                        file.delete()
                    }
                    UserListAdapter.list.clear()
                }
            }
        } catch (t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    //ユーザー情報をセットする
    private fun setUserInfo(user: User){
        my_profile_progress_bar.visibility = View.INVISIBLE
        changed_name.hint = user.name
    }

    //画像をセットする
    private fun setImgByC(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        launch(job + UI) {
            GetIcon(id).getIcon().let {
                val image = BitmapFactory.decodeStream(it)
                imageView.setImageBitmap(image)
            }
        }
    }
}
