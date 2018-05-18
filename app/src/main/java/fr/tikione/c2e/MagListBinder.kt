package fr.tikione.c2e

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import fr.tikione.c2e.Utils.Network.NetworkUtils
import fr.tikione.c2e.Utils.TmpUtils
import jp.wasabeef.blurry.Blurry
import java.io.File
import java.util.HashMap
import android.graphics.Bitmap
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.FileOutputStream
import java.io.OutputStream
import android.graphics.BitmapFactory
import android.support.v4.content.FileProvider


open class MagListBinder : ViewHolderBinder<MagasineInfo> {

    val couvName = "couv.jpg"

    var imageUrlMap = HashMap<Int, String>()
    val imageUrlMapFilename = "imageUrlMap.hmap"
    private var useBlurry: Boolean? = null

    override fun bind(holder: Adapter.ViewHolder<MagasineInfo>, item: MagasineInfo) {

        if (useBlurry == null)
            useBlurry = holder.v.context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE).getBoolean("useBlurry", true)
        holder.setIsRecyclable(false)
        val context = holder.v.context

        holder.v.findViewById<TextView>(R.id.magNumberText).text = item.number.toString()
        holder.v.findViewById<TextView>(R.id.magDateText).text = item.date.dateToString()

        if (item.isMostRecent)
            return
        if (item.downloaded) {
            val dlFolder = TmpUtils.getFilesPath(holder.v.context)
            val numDirFolder = File(dlFolder, item.number.toString())
            val couv = File(numDirFolder, couvName)
            if (couv.exists() && couv.isFile && couv.canRead()) {
                val bit = BitmapFactory.decodeFile(couv.absolutePath)
                setImages(bit, false, true, holder)
            }
        } else {
            var imageUrl: String;
            if (imageUrlMap.containsKey(item.number) && imageUrlMap[item.number]!!.isNotEmpty()) {
                imageUrl = imageUrlMap[item.number]!!
                setImageFromUrl(imageUrl, holder, item)
            } else {
                NetworkUtils.HTMLrequest(context!!, com.android.volley.Request.Method.GET,
                        "https://www.canardpc.com/numero/" + item.number,
                        Response.Listener { res ->
                            //imageUrl = res.getElementsByClass("slideshow")[0].attr("href") //big couv
                            imageUrl = res.getElementById("numero-couverture").attr("src")
                            imageUrl = "https://www.canardpc.com/$imageUrl";
                            //imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf('?'))
                            imageUrlMap[item.number] = imageUrl
                            setImageFromUrl(imageUrl, holder, item)
                        }, null)
            }
        }

    }

    private fun setImageFromUrl(url: String, holder: Adapter.ViewHolder<MagasineInfo>, item: MagasineInfo) {
        holder.v.findViewById<ImageView>(R.id.coverMagasineImage).visibility = View.INVISIBLE
        NetworkUtils.ImageRequest(holder.v.context, url,
                Response.Listener { res ->
                    item.bitmap = res
                    setImages(res, false, false, holder)
                }, null)
    }

    private fun setImages(drawable: Bitmap, isTemp: Boolean, isDownloaded: Boolean, holder: Adapter.ViewHolder<MagasineInfo>) {
        val imageView = holder.v.findViewById<ImageView>(R.id.coverMagasineImage)
        imageView.setImageBitmap(drawable)
        imageView.visibility = View.VISIBLE

        if (useBlurry!!) {
            try {
                Blurry.with(holder.v.context).radius(15).async()
                        .from(drawable)
                        .into(holder.v.findViewById(R.id.backgroundBlurImage));
            } catch (e: Exception) {
                Log.wtf(javaClass.simpleName, e.message)
            }
        }

        if (!isDownloaded) {
            val matrix = ColorMatrix();
            matrix.setSaturation(0.0f);
            val filter = ColorMatrixColorFilter(matrix);
            holder.v.findViewById<ImageView>(R.id.backgroundBlurImage).colorFilter = filter
        }
    }

    fun storeCoverOnDisk(mag: MagasineInfo, act: MainActivity) {
        val dlFolder = File(TmpUtils.getFilesPath(act.baseContext), mag.number.toString())
        if (!dlFolder.exists())
            dlFolder.mkdirs()
        val coverFile = File(dlFolder, couvName)
        coverFile.createNewFile()
        if (!coverFile.exists() || mag.bitmap == null)
            return

        val os: OutputStream
        try {
            os = FileOutputStream(coverFile)
            mag.bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

    }

    fun buttonDlListener(mag: MagasineInfo, incPicture: Boolean, dialog: Dialog, act: MainActivity) {
        act.downloadMag(mag.number, true);
        dialog.dismiss()
        storeCoverOnDisk(mag, act)
        mag.downloaded = true;
    }

    fun createDlDialog(mag: MagasineInfo, act: MainActivity) {
        val dialog = android.app.Dialog(act)
        dialog.setContentView(R.layout.dialog_download_mag)
        //holder.v.findViewById<ImageView>(R.id.coverMagasineImage)
        dialog.findViewById<Button>(R.id.button_dl_with_pic).setOnClickListener({ buttonDlListener(mag, true, dialog, act) })
        dialog.findViewById<Button>(R.id.button_dl_without_pic).setOnClickListener({ buttonDlListener(mag, false, dialog, act) })
        dialog.show()
    }

    override fun setClickListener(holder: Adapter.ViewHolder<MagasineInfo>, data: MutableList<MagasineInfo>, context: Context) {
        val mag = data[holder.adapterPosition]
        val act = context as MainActivity

        val clickListener = View.OnClickListener { _ ->

            if (act.dlStarted) {
                Toast.makeText(act, act.getString(R.string.dl_already_pending), Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            if (data[holder.adapterPosition].downloaded) {
                if (! TmpUtils.openMag(mag.number, act)) {
                    createDlDialog(mag, act)
                    return@OnClickListener
                }
            } else
                createDlDialog(mag, act)
        }
        holder.v.findViewById<View>(R.id.rootview).setOnClickListener(clickListener)
    }
}