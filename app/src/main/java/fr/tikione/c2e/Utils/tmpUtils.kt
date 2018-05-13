package fr.tikione.c2e.Utils

import android.content.Context
import android.os.Environment
import fr.tikione.c2e.DateCustom
import fr.tikione.c2e.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TmpUtils {


    companion object {

        fun getFilesPath(context: Context) : File
        {
            //return context.filesDir
            return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                    + File.separator + context.getString(R.string.folder_name))
        }

        fun dateToString(date: DateCustom): String {
            val day = if (date.firstWeek) "1er" else "15"
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, date.year)
            cal.set(Calendar.MONTH, date.month - 1)
            cal.set(Calendar.DAY_OF_MONTH, 1)

            val format = SimpleDateFormat(" MMM yyyy", Locale.FRENCH)
            val text = format.format(cal.time).replaceFirst(".", "")
            return (day + text)
        }

    }
}
