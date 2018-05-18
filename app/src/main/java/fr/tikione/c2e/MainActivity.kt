package fr.tikione.c2e

import android.app.ActivityManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import fr.tikione.c2e.AccountManager.AuthUtils
import fr.tikione.c2e.Utils.TmpUtils
import fr.tikione.c2e.Utils.TmpUtils.Companion.showError
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var receiver: DataUpdateReceiver? = null
    var dlStarted: Boolean = false
    private var numberDl: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo: check if the user has at least 1 magazine saved, in this case login is not forced
        if (!AuthUtils.isAPIConnected(this))
            startActivity(Intent(this, Login::class.java))

        setContentView(R.layout.activity_main)
        title = getString(R.string.title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            TmpUtils.checkPermissions(this)
    }

    fun logout(v: View) {
        AuthUtils.logout(this)
    }

    fun downloadMag(number: Int, incPicture: Boolean) {
        if (!AuthUtils.isAPIConnected(this))
            startActivity(Intent(this, Login::class.java))

        if (dlStarted)
            return Toast.makeText(this@MainActivity, getString(R.string.dl_already_pending), Toast.LENGTH_LONG).show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ! TmpUtils.checkPermissions(this))
            return

        try {
            dlStarted = true
            numberDl = number
            progressLayout.visibility = View.VISIBLE
            progressInfoTextview.text = getString(R.string.downloading_number, number)

            val dlIntent = Intent(this, DownloadTask::class.java)
                    .putExtra("magNumber", number.toString())
                    .putExtra("incPictures", incPicture)
            startService(dlIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            progressLayout.visibility = View.INVISIBLE
            dlStarted = false
            showError("Impossible de se connecter à CanardPC ($e)", this)
        }
    }

    fun stopService(v: View) {
        if (!dlStarted)
            return
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(getString(R.string.stop_dl_question))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    stopService(Intent(this, DownloadTask::class.java))
                    dlStarted = false
                    Toast.makeText(this@MainActivity, getString(R.string.stop_dl_done), Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(getString(R.string.no), null)
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        dlStarted = getSharedPreferences("app", MODE_PRIVATE).getBoolean("dlStarted", false)
        numberDl = getSharedPreferences("app", MODE_PRIVATE).getInt("numberDl", 0)

        if (dlStarted && !isMyServiceRunning(DownloadTask::class.java))
            onDlEnded(Intent())
        if (dlStarted) {
            progressLayout.visibility = View.VISIBLE
            progressInfoTextview.text = getString(R.string.downloading_number, numberDl)
        }
        if (receiver == null)
            createReceiver()
    }

    override fun onStop() {
        super.onStop()
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
            getSharedPreferences("app", MODE_PRIVATE)!!.edit().putBoolean("dlStarted", dlStarted).apply()
            getSharedPreferences("app", MODE_PRIVATE)!!.edit().putInt("numberDl", numberDl).apply()
        }
    }


    //is deprecated since API 26, but don't find any other easy way.
    //maybe by verifying if the file to download was created?
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun createReceiver() {
        receiver = DataUpdateReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadTask.DOWNLOAD_ENDED)
        intentFilter.addAction(DownloadTask.PROGRESSION_UPDATE)
        registerReceiver(receiver, intentFilter)
    }

    private fun onDlEnded(intent: Intent) {
        dlStarted = false
        progressLayout.visibility = View.GONE
        if (intent.hasExtra("error")) {
            val error = intent.getStringExtra("error")
            Toast.makeText(baseContext, error, Toast.LENGTH_LONG).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error == getString(R.string.invalid_permissions))
                checkPermissions()
        } else if (!intent.hasExtra("interrupted")) {
            Toast.makeText(baseContext, R.string.notif_ended, Toast.LENGTH_LONG).show()
        }
    }

    private inner class DataUpdateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadTask.DOWNLOAD_ENDED)
                onDlEnded(intent)
            if (intent.action == DownloadTask.PROGRESSION_UPDATE) {
                if (progressBar.isIndeterminate)
                    progressBar.isIndeterminate = false
                val value = intent.getFloatExtra("progress", 0.0f)
                progressBar.progress = value.toInt();
            }
        }
    }

}
