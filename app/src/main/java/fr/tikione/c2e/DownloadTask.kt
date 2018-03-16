package fr.tikione.c2e

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.github.salomonbrys.kodein.instance
import compat.AssetService
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.model.web.Auth
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import java.io.File
import java.io.IOException
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle




class DownloadTask() : IntentService("DownloadTask") {

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

    private lateinit var username: String
    private lateinit var password: String
    private lateinit var magNumber: String
    private var incPictures: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent!!.extras

        username = extras.getString("username")
        password = extras.getString("password")
        magNumber = extras.getString("magNumber")
        incPictures = extras.getBoolean("incPictures")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            notificationManager = NotificationManagerCompat.from(baseContext)
            notifBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)

            notifBuilder.setContentTitle(getString(R.string.notif_title) + magNumber)
                    .setContentText(getString(R.string.notif_downloading))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                setOreoNotif()

            downloadMag()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }


    private fun downloadMag() {
        val assetService: AssetService = kodein.instance()
        assetService.setAssetManager(assets)
        val cpcAuthService: CPCAuthService = kodein.instance()
        val cpcReaderService: CPCReaderService = kodein.instance()
        val writerService: HtmlWriterService = kodein.instance()

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
        }
        catch (e : Exception)
        {
            errorString = getString(R.string.invalid_permissions);
            updateDlStatus(-1.0f)
            return;
        }

        var auth: Auth
        try {
            auth = cpcAuthService.authenticate(username, password)
        } catch (e: IOException) {
            errorString = e.message.toString()
            updateDlStatus(-1.0f)
            return
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                val tmpStatus = cpcReaderService.downloadStatus
                if (tmpStatus != downloadStatus)
                    updateDlStatus(tmpStatus)
                handler.postDelayed(this, 3000)
            }
        }, 3000)

        val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
        handler.removeCallbacksAndMessages(null)
        updateDlStatus(99.0f)
        writerService.write(magazine, output, incPictures, null, false, "")
        updateDlStatus(100.0f)
        return
    }

    private fun updateDlStatus(ndlStat: Float) {
        downloadStatus = ndlStat

        if (downloadStatus < 0)
        {
            notifBuilder.setContentText(errorString)
            notifBuilder.setProgress(0, 0, false);
            notificationManager.notify(notificationId, notifBuilder.build())
            return
        }

        sendBroadcast(Intent(PROGRESSION_UPDATE).putExtra("progress", downloadStatus))
        notifBuilder.setProgress(100, downloadStatus.toInt(), false)
        if (downloadStatus == 99.0f)
            notifBuilder.setContentText(getString(R.string.notif_writing))
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

        val intent = Intent(DOWNLOAD_ENDED)
        if (!errorString.isNullOrBlank())
            intent.putExtra("error", errorString)
        sendBroadcast(intent)
    }
}
