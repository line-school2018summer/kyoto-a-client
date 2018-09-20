package intern.line.me.kyotoaclient.activity

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.IOUtils
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.*
import kotlinx.android.synthetic.main.activity_change_my_profile.*
import kotlinx.android.synthetic.main.activity_get_user_profile.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class ChangeMyProfileActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777
    lateinit var file: File

    private val job = Job()
    private var myId = 0L
    val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")


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
        val context = this

        try {
            if (requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data != null) {
                val uri = Uri.parse(data.dataString)

                val f = getFile(uri)
                if (f == null) {
                    Toast.makeText(this, "適切でないファイル形式です", Toast.LENGTH_SHORT).show()
                } else {
                    file = f
                    launch(job + UI) {
                        PostIcon(file).postIcon()
                        setImgByC(myId)
                    }
                }
            }
        } catch (t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFile(uri: Uri): File? {
        val path: String? = getPath(uri)
        if (path != null) {
            file = File(path)
            return file
        }
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        val filename = "user_icon_id_" + myId.toString() + "." + ext

        file = File(cacheDir, filename)
        val fos = FileOutputStream(file)
        fos.write(IOUtils.toByteArray(contentResolver.openInputStream(uri)))
        fos.close()
        return file
    }

    fun getPath(uri: Uri): String? {
        val context = this

        if (DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.getAuthority()) {// ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    "/stroage/" + type + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == uri.getAuthority()) {// DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == uri.getAuthority()) {// MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                contentUri = MediaStore.Files.getContentUri("external")
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        if ("content".equals(uri.getScheme(), ignoreCase = true)) {//MediaStore
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {// File
            return uri.getPath()
        }
        return null
    }

    fun getDataColumn(context: Context?, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        try {
            cursor = context!!.contentResolver.query(
                    uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val cindex = cursor!!.getColumnIndexOrThrow(projection[0])
                return cursor.getString(cindex)
            }
        } catch (e: Exception) {
            // no op
        } finally {
            cursor?.close()
        }
        return null
    }

    //ユーザー情報をセットする
    fun setUserInfo(user: User){
        my_profile_progress_bar.visibility = View.INVISIBLE
        my_name.text = user.name
    }

    //画像をセットする
    fun setImg(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        Glide.with(this)
                .load("https://kyoto-a-api.pinfort.me/download/icon/${id}")
                .into(imageView)
    }

    //同上
    fun setImgByC(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        launch(job + UI) {
            GetIcon(id).getIcon().let {
                val image = BitmapFactory.decodeStream(it)
                imageView.setImageBitmap(image)
            }
        }
    }
}
