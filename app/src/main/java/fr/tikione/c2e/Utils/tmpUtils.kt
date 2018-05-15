package fr.tikione.c2e.Utils

import android.content.Context
import android.os.Environment
import fr.tikione.c2e.DateCustom
import fr.tikione.c2e.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class TmpUtils {


    companion object {

        fun getFilesPath(context: Context) : File {
            //return context.filesDir
            return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                    + File.separator + context.getString(R.string.folder_name))
        }

        fun <T> readObjectFile(filename: String, context: Context): T {
            val file = File(context.filesDir, filename)
            if (!(file.isFile && file.canRead()))
                throw FileNotFoundException()

            val stream = FileInputStream(file)
            val ois = ObjectInputStream(stream)
            val res: T =  (ois.readObject() as T)
            ois.close()
            return res
        }

        fun <T> writeObjectFile(obj: T, filename: String, context: Context)
        {
            val file = File(context.filesDir, filename)
            val stream = FileOutputStream(file)
            val out = ObjectOutputStream(stream)
            out.writeObject(obj);
            out.flush()
            out.close()
        }

    }
}
