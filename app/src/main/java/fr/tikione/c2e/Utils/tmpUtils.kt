package fr.tikione.c2e.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import fr.tikione.c2e.R
import java.io.*
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast


class GenericFileProvider : FileProvider()

class TmpUtils {


    companion object {
        private const val PERMISSION_REQUEST_STORAGE: Int = 72


        fun getFilesPath(context: Context): File {
            //return context.filesDir
            return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                    + File.separator + context.getString(R.string.folder_name))
        }

        fun <T> readObjectFile(filename: String, context: Context): T {
            val file = File(context.filesDir, filename)
            if (!(file.isFile && file.canRead()))
                throw FileNotFoundException()

            try {
                val stream = FileInputStream(file)
                val ois = ObjectInputStream(stream)
                val res: T = (ois.readObject() as T)
                ois.close()
                return res
            } catch (E: Exception) {
            }
            throw FileNotFoundException()
        }

        fun <T> writeObjectFile(obj: T, filename: String, context: Context) {
            val file = File(context.filesDir, filename)
            val stream = FileOutputStream(file)
            val out = ObjectOutputStream(stream)
            out.writeObject(obj);
            out.flush()
            out.close()
        }


        @RequiresApi(Build.VERSION_CODES.M)
        fun checkPermissions(act: Activity): Boolean {
            val array = arrayListOf<String>()
            if (ContextCompat.checkSelfPermission(act.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                array.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (ContextCompat.checkSelfPermission(act.baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                array.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (!array.isEmpty())
                act.requestPermissions(array.toTypedArray(), PERMISSION_REQUEST_STORAGE)
            return array.isEmpty()
        }

        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        fun showError(msg: String, act: Activity) {
            val builder = AlertDialog.Builder(act)
            builder
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(act.getString(R.string.error))
                    .setMessage(msg)
                    .setPositiveButton(act.getString(R.string.ok)) { dialog, which -> dialog.dismiss() }
            builder.create().show()
        }

        fun openMag(num: Int, act: Activity): Boolean {
            val file = File(TmpUtils.getFilesPath(act.baseContext).absolutePath +
                    File.separator + num.toString() + File.separator + "mag.html")
            if (!file.exists() || !file.canRead() || !file.isFile)
                return false
            val browserIntent = Intent(Intent.ACTION_VIEW);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.data = FileProvider.getUriForFile(act.baseContext, "fr.tikione.fileprovider", file)
            browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            act.startActivity(browserIntent)
            return true
        }


    }
}
