package fr.tikione.c2e

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.github.salomonbrys.kodein.instance
import compat.AssetService
import compat.EndServiceException
import fr.tikione.c2e.AccountManager.AuthUtils
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.model.web.Auth
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.web.AbstractReader
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import org.jsoup.Jsoup
import java.io.File

class DownloadTask : IntentService("DownloadTask") {

    companion object {
        const val PROGRESSION_UPDATE = "download_progressing"
        const val DOWNLOAD_ENDED = "download_ended"
    }

    private val CHANNEL_ID = "CPC_Download"
    private val notificationId = 2080

    private var errorString: String = ""
    var downloadStatus: Float = 0.0f
    var handler: Handler = Handler()

    private lateinit var notificationManager : NotificationManagerCompat
    private lateinit var notifBuilder: NotificationCompat.Builder

    private lateinit var magNumber: String
    private var incPictures: Boolean = false

    val cpcReaderService: CPCReaderService = kodein.instance()
    val writerService: HtmlWriterService = kodein.instance()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent!!.extras

        magNumber = extras.getString("magNumber")
        incPictures = extras.getBoolean("incPictures")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        val output: File? = getFile() ?: return
        try {
            notificationManager = NotificationManagerCompat.from(baseContext)
            notifBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)

            notifBuilder.setContentTitle(getString(R.string.notif_title) + magNumber)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setColor(resources.getColor(R.color.CPCMainColor))
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                setOreoNotif()
            startForeground(notificationId, notifBuilder.build());

            downloadMag(output!!)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (e: EndServiceException) {
            output!!.delete()
            notificationManager.cancel(notificationId)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun downloadMag(output: File) {
        val assetService: AssetService = kodein.instance()
        assetService.setAssetManager(assets)
        var dlArticlesEnded = false


        val auth: Auth
        val cookieList = AuthUtils.getToken(this).split('=')
        var cookie = hashMapOf(cookieList[0] to cookieList[1])
        auth = Auth(cookie, AuthUtils.getUsername(this), "")

        val testConnectedURL = "https://www.canardpc.com/user"
        val connectFailedURL = "https://www.canardpc.com/user/login"

        val testLoginconnexion = Jsoup.connect(testConnectedURL)
                .followRedirects(false)
                .timeout(10000)
                .cookies(auth.cookies)
                .userAgent(AbstractReader.UA)
                .execute()

        if (testLoginconnexion.header("location") == connectFailedURL) {
            AuthUtils.logout(this)
            startActivity(Intent(this, Login::class.java))
            return
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (downloadStatus == 100.0f)
                    return
                var tmpStatus: Float;
                if (incPictures && dlArticlesEnded) {
                    tmpStatus = writerService.downloadStatus
                } else {
                    tmpStatus = cpcReaderService.downloadStatus
                    if (incPictures)
                        tmpStatus /= 10
                    else if (tmpStatus > 1)
                        tmpStatus--
                }
                if (tmpStatus != downloadStatus)
                    updateDlStatus(tmpStatus)
                handler.postDelayed(this, 3000)
            }
        }, 3000)

        val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
        dlArticlesEnded = true;
        writerService.write(magazine, output!!, incPictures, null, false, "")
        handler.removeCallbacksAndMessages(null)
        updateDlStatus(100.0f)
    }

    private fun getFile() : File?
    {
        var output = File("")
        try {
            val dlFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                    + File.separator + getString(R.string.folder_name))
            if (!dlFolder.exists())
                dlFolder.mkdirs()
            val filename = "cpc" + magNumber + (if (incPictures) "" else "-nopic") + ".html"
            output = File(dlFolder, filename)
            if (!output.exists()) {
                output.parentFile.mkdirs()
                output.createNewFile()
            }
        } catch (e: Exception) {
            errorString = getString(R.string.invalid_permissions);
            updateDlStatus(-1.0f)
            return null;
        }
        return output
    }


    private fun updateDlStatus(ndlStat: Float) {
        downloadStatus = ndlStat

        if (downloadStatus < 0) {
            notifBuilder.setContentText(errorString)
            notifBuilder.setProgress(0, 0, false);
            notificationManager.notify(notificationId, notifBuilder.build())
            return
        }

        sendBroadcast(Intent(PROGRESSION_UPDATE).putExtra("progress", downloadStatus))
        notifBuilder.setProgress(100, downloadStatus.toInt(), false)
        if (downloadStatus == 100.0f) {
            notifBuilder.setContentText(getString(R.string.notif_ended))
            notifBuilder.setProgress(0, 0, false);
            /*
            val intent = Intent(this, ACTIVITY_NAME::class.java)
            intent.data = NUMBER_MAG OR ARTICLE?
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            notifBuilder.setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            */
        }
        notificationManager.notify(notificationId, notifBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOreoNotif() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.notif_title)
        val description = getString(R.string.notif_downloading)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        // Register the channel with the system
        val manager = (baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        stopForeground(false)

        if (downloadStatus != 100.0f && errorString.isEmpty())
        {
            cpcReaderService.cancelDl = true
            writerService.cancelDl = true
        }

        val intent = Intent(DOWNLOAD_ENDED)
        if (!errorString.isBlank())
            intent.putExtra("error", errorString)
        sendBroadcast(intent)
    }
}
