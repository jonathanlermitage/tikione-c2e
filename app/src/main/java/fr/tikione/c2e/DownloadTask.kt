package fr.tikione.c2e

import android.content.res.AssetManager
import android.os.AsyncTask
import android.os.Environment
import fr.tikione.c2e.service.html.HtmlWriterServiceImpl
import fr.tikione.c2e.service.web.CPCAuthServiceImpl
import fr.tikione.c2e.service.web.scrap.CPCReaderServiceImpl
import java.io.File

class DownloadTask(private var asset: AssetManager, private var username: String, private var password: String,
                   private var magNumber: Int, private var incPictures: Boolean) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        return downloadMag(asset, username, password, magNumber, incPictures)
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }

    private fun downloadMag(asset: AssetManager, username: String, password: String, magNumber: Int, incPictures: Boolean): String {
        val cpcAuthService = CPCAuthServiceImpl()
        val cpcReaderService = CPCReaderServiceImpl()
        val writerService = HtmlWriterServiceImpl(asset)
        val dlFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val output = File(dlFolder, "cpc" + magNumber + (if (incPictures) "" else "-nopic") + ".html")
        val auth = cpcAuthService.authenticate(username, password)
        val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
        writerService.write(magazine, output, incPictures)
        return output.absolutePath
    }
}
