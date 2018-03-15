package fr.tikione.c2e

import android.content.res.AssetManager
import android.os.AsyncTask
import android.os.Environment
import com.github.salomonbrys.kodein.instance
import compat.AssetService
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import java.io.File

class DownloadTask(private var asset: AssetManager, private var username: String, private var password: String,
                   private var magNumber: String, private var incPictures: Boolean) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        return downloadMag(asset, username, password, magNumber, incPictures)
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }

    private fun downloadMag(asset: AssetManager, username: String, password: String, magNumber: String, incPictures: Boolean): String {
        val assetService: AssetService = kodein.instance()
        assetService.setAssetManager(asset)
        val cpcAuthService: CPCAuthService = kodein.instance()
        val cpcReaderService: CPCReaderService = kodein.instance()
        val writerService: HtmlWriterService = kodein.instance()
        val dlFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val output = File(dlFolder, "cpc" + magNumber + (if (incPictures) "" else "-nopic") + ".html")
        val auth = cpcAuthService.authenticate(username, password)
        val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
        writerService.write(magazine, output, incPictures, null, false, "")
        return output.absolutePath
    }
}
