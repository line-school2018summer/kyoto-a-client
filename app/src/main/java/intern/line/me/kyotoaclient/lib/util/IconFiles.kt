package intern.line.me.kyotoaclient.lib.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import intern.line.me.kyotoaclient.presenter.room.GetRoomIcon
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileOutputStream

class IconFiles {
    fun updateRoomIcon(context: Context, roomId: Long) {
        launch(CommonPool) {
            val fileName = "room/icon_id_" + roomId.toString()
            val file = File(context.filesDir, fileName)
            GetRoomIcon().getRoomIcon(roomId).let {
                val image = BitmapFactory.decodeStream(it)
                val fos = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            }
        }
    }

    fun getMimeTypeOfFile(pathName: String): String {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, opt)
        return opt.outMimeType
    }
}