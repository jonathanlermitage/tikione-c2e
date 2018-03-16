package fr.tikione.c2e

import android.Manifest
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.title)

        buttonDownload.setOnClickListener({ downloadMag() })

        progressBar.visibility = View.GONE
        progressBar.visibility = View.GONE

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

    private fun downloadMag() {
        progressBar.visibility = View.VISIBLE
        try {
            var saveCreds = false
            var username = "";
            var password = "";
            if (AuthUtils.isAPIConnected(baseContext)) {
                username = AuthUtils.getUsername(baseContext)
                password = AuthUtils.getToken(baseContext)
                saveCreds = false
            } else {
                saveCreds = saveCredentialsCheckbox.isChecked
                username = editTextLogin.text.toString()
                password = editTextPassword.text.toString()
            }

            progressBar.visibility = View.VISIBLE

            val intent = Intent(this, DownloadTask::class.java)
                    .putExtra("username", username)
                    .putExtra("password", password)
                    .putExtra("magNumber", editTextMagNumber.text.toString())
                    .putExtra("incPictures", checkboxIncludePictures.isChecked)
            startService(intent)

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

    override fun onResume() {
        super.onResume()
        if (receiver == null)
            createReceiver()
    }

    override fun onPause() {
        super.onPause()
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }
    }

    fun createReceiver() {
        receiver = DataUpdateReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadTask.DOWNLOAD_ENDED)
        intentFilter.addAction(DownloadTask.PROGRESSION_UPDATE)
        registerReceiver(receiver, intentFilter)
    }

    private inner class DataUpdateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadTask.DOWNLOAD_ENDED) {
                progressBar.visibility = View.GONE
                if (intent.hasExtra("error")) {
                    val error = intent.getStringExtra("error")
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error == getString(R.string.invalid_permissions))
                        checkPermissions()
                } else {
                    Toast.makeText(context, R.string.notif_ended, Toast.LENGTH_LONG).show()
                    //val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    //intent.data = Uri.fromFile(File(res))
                    //sendBroadcast(intent)
                }
            }
            if (intent.action == DownloadTask.PROGRESSION_UPDATE) {
                if (progressBar.isIndeterminate)
                    progressBar.isIndeterminate = false
                val value = intent.getFloatExtra("progress", 0.0f)
                progressBar.progress = value.toInt();
            }
        }
    }

}
