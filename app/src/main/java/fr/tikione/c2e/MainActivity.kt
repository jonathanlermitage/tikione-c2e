package fr.tikione.c2e

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import fr.tikione.c2e.AccountManager.AuthUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var receiver: DataUpdateReceiver? = null
    private val PERMISSION_REQUEST_STORAGE: Int = 72
    private var dlStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo: check if the user has at least 1 magazine saved, in this case login is not forced
        if (!AuthUtils.isAPIConnected(this))
            startActivity(Intent(this, Login::class.java))

        setContentView(R.layout.activity_main)
        title = getString(R.string.title)
        progressBar.visibility = View.GONE
        buttonCancelDownload.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissions()
        setLoginUI()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        val array = arrayListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            array.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            array.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!array.isEmpty())
            requestPermissions(array.toTypedArray(), PERMISSION_REQUEST_STORAGE)
        //TODO: verify if permissions are checked
    }

    private fun setLoginUI() {
        if (AuthUtils.isAPIConnected(baseContext)) {
            editTextLogin.visibility = View.GONE
            editTextPassword.visibility = View.GONE
        } else
            buttonLogout.visibility = View.GONE
    }

    fun logout(v: View) {
        AuthUtils.logout(this)
    }

    fun downloadMag(v: View) {
        if (!AuthUtils.isAPIConnected(this))
            startActivity(Intent(this, Login::class.java))

        progressBar.visibility = View.VISIBLE
        try {
            dlStarted = true
            buttonDownload.visibility = View.GONE
            buttonCancelDownload.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

            val dlIntent = Intent(this, DownloadTask::class.java)
                    .putExtra("magNumber", editTextMagNumber.text.toString())
                    .putExtra("incPictures", checkboxIncludePictures.isChecked)
            startService(dlIntent)

            //2
            //MediaScannerConnection.scanFile(this, arrayOf(res), null) { _, _ -> }

            //if (saveCreds) {
            //AuthUtils.addAccount(baseContext, username, password)
            //setLoginUI()
            //}
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Impossible de se connecter à CanardPC ($e)")
        }
    }

    fun stopService(v : View) {
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

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun showSuccess(msg: String) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Terminé")
                .setMessage(msg)
                .setPositiveButton("Terminé") { dialog, which -> Toast.makeText(this@MainActivity, "OK", Toast.LENGTH_LONG).show() }
        builder.create().show()
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun showError(msg: String) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Erreur")
                .setMessage(msg)
                .setPositiveButton("Erreur") { dialog, which -> Toast.makeText(this@MainActivity, "OK", Toast.LENGTH_LONG).show() }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        if (dlStarted && !isMyServiceRunning(DownloadTask::class.java))
            onDlEnded(Intent())
        if (receiver == null)
            createReceiver()
    }

    override fun onStop() {
        super.onStop()
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
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
        buttonDownload.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonCancelDownload.visibility = View.GONE
        if (intent.hasExtra("error")) {
            val error = intent.getStringExtra("error")
            Toast.makeText(baseContext, error, Toast.LENGTH_LONG).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error == getString(R.string.invalid_permissions))
                checkPermissions()
        } else {
            Toast.makeText(baseContext, R.string.notif_ended, Toast.LENGTH_LONG).show()
            //val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            //intent.data = Uri.fromFile(File(res))
            //sendBroadcast(intent)
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
