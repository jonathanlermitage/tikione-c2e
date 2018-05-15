package fr.tikione.c2e

import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import fr.tikione.c2e.Utils.Network.NetworkUtils
import fr.tikione.c2e.Utils.TmpUtils
import jp.wasabeef.blurry.Blurry
import java.io.File
import java.util.HashMap

open class MagListBinder : ViewHolderBinder<MagasineInfo> {

    var imageUrlMap = HashMap<Int, String>()
    val imageUrlMapFilename = "imageUrlMap.hmap"


    override fun bind(holder: Adapter.ViewHolder<MagasineInfo>, item: MagasineInfo) {

        holder.setIsRecyclable(false)
        val context = holder.v.context

        holder.v.findViewById<TextView>(R.id.magNumberText).text = item.number.toString()
        holder.v.findViewById<TextView>(R.id.magDateText).text = item.date.dateToString()

        if (item.isMostRecent)
            return
        if (item.downloaded) {
            val dlFolder = TmpUtils.getFilesPath(holder.v.context)
            val numDirFolder = File(dlFolder, item.number.toString())
            val couv = File(numDirFolder, "couv.jpg")
            if (couv.exists() && couv.isFile && couv.canRead()) {
                setImages(BitmapDrawable(context.resources, BitmapFactory.decodeFile(couv.absolutePath)), false, false, holder)
            }
        } else {
            var imageUrl: String;
            if (imageUrlMap.containsKey(item.number)) {
                imageUrl = imageUrlMap[item.number]!!
                setImageFromUrl(imageUrl, holder)
            } else {
                NetworkUtils.HTMLrequest(context!!, com.android.volley.Request.Method.GET,
                        "https://www.canardpc.com/numero/" + item.number,
                        Response.Listener { res ->
                            //imageUrl = res.getElementsByClass("slideshow")[0].attr("href") //big couv
                            imageUrl = res.getElementById("numero-couverture").attr("src")
                            imageUrl = "https://www.canardpc.com/$imageUrl";
                            //imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf('?'))
                            imageUrlMap[item.number] = imageUrl
                            setImageFromUrl(imageUrl, holder)
                        }, null)
            }
        }

    }

    private fun setImageFromUrl(url: String, holder: Adapter.ViewHolder<MagasineInfo>) {
        holder.v.findViewById<ImageView>(R.id.coverMagasineImage).visibility = View.INVISIBLE
        NetworkUtils.ImageRequest(holder.v.context, url,
                Response.Listener { res ->
                    setImages(res, false, false, holder)
                }, null)
    }

    private fun setImages(drawable: Drawable, isTemp: Boolean, isDownloaded: Boolean, holder: Adapter.ViewHolder<MagasineInfo>) {
        val imageView = holder.v.findViewById<ImageView>(R.id.coverMagasineImage)
        imageView.setImageDrawable(drawable)
        imageView.visibility = View.VISIBLE

        try {
            Blurry.with(holder.v.context).radius(15).async()
                    .capture(imageView)
                    .into(holder.v.findViewById(R.id.backgroundBlurImage));
        } catch (e: Exception) {}

        if (!isDownloaded) {
            val matrix = ColorMatrix();
            matrix.setSaturation(0.0f);
            val filter = ColorMatrixColorFilter(matrix);
            holder.v.findViewById<ImageView>(R.id.backgroundBlurImage).colorFilter = filter
        }
    }

    override fun setClickListener(holder: Adapter.ViewHolder<MagasineInfo>, data: MutableList<MagasineInfo>) {
        val context = holder.v.context
        data[holder.adapterPosition].downloaded
        val clickListener = View.OnClickListener { _ ->
        }
        holder.v.findViewById<View>(R.id.rootview).setOnClickListener(clickListener)
    }
}