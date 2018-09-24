package intern.line.me.kyotoaclient.lib.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.android.gms.common.util.IOUtils
import java.io.File
import java.io.FileOutputStream

class FileUtils(private val context: Context) {
    var file: File? = null

    fun getFile(uri: Uri, fileName: String): File? {
        val path: String? = getPath(uri)
        if (path != null) {
            val localFile = File(path)
            val filename = localFile.name
            file = File(context.cacheDir, filename)
            val fos = FileOutputStream(file)
            fos.write(localFile.readBytes())
            fos.close()
            return file
        }
        val mime = context.contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        val filename = fileName + "." + ext

        file = File(context.cacheDir, filename)
        val fos = FileOutputStream(file)
        fos.write(IOUtils.toByteArray(context.contentResolver.openInputStream(uri)))
        fos.close()
        return file
    }

    fun getPath(uri: Uri): String? {

        if (DocumentsContract.isDocumentUri(this.context, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {// ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    "/stroage/" + type + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {// DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(this.context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == uri.authority) {// MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                contentUri = MediaStore.Files.getContentUri("external")
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(this.context, contentUri, selection, selectionArgs)
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {//MediaStore
            return getDataColumn(this.context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {// File
            return uri.path
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
}