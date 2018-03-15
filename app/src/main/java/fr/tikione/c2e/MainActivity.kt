package fr.tikione.c2e

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var downloadBtn: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "CanardPC hors-ligne"

        downloadBtn = findViewById<View>(R.id.buttonDownload) as Button
        downloadBtn.setOnClickListener({ downloadMag() })

        progressBar = findViewById<View>(R.id.progressbar) as ProgressBar
        progressBar.visibility = View.GONE
    }

    private fun downloadMag() {
        progressBar.visibility = View.VISIBLE
        try {
            val dlTask = DownloadTask(this.assets, editTextLogin.text.toString(),
                    editTextPassword.text.toString(),
                    editTextMagNumber.text.toString(), checkboxIncludePictures.isChecked)
            dlTask.execute()
            val res = dlTask.get()

            //1
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File(res))
            sendBroadcast(intent)

            //2
            //MediaScannerConnection.scanFile(this, arrayOf(res), null) { _, _ -> }

            showSuccess("Le téléchargement est un succès ($res) / exists:" + File(res).exists())
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Impossible de se connecter à CanardPC ($e)")
        }
        progressBar.visibility = View.GONE

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            @Suppress("DEPRECATION") // non-deprecated asks API level 26
            vibrator.vibrate(400)
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
}
