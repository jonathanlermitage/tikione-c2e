package fr.tikione.c2e

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import fr.tikione.c2e.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import fr.tikione.c2e.Utils.LoadingUtils
import fr.tikione.c2e.Utils.TmpUtils
//import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.coroutineContext


data class MagasineInfo(var downloaded: Boolean = false,
                        var number: Int = 0,
                        var date: DateCustom)


open class RoomSearchBinder : ViewHolderBinder<MagasineInfo> {

    override fun bind(holder: Adapter.ViewHolder<MagasineInfo>, item: MagasineInfo) {

        val context = holder.v.context

        holder.v.findViewById<TextView>(R.id.magNumberText).text = item.number.toString()
        holder.v.findViewById<TextView>(R.id.magDateText).text = TmpUtils.dateToString(item.date)
        if (item.downloaded) {
            val dlFolder = TmpUtils.getFilesPath(holder.v.context)
            val numDirFolder = File(dlFolder, item.number.toString())
            val couv = File(numDirFolder, "couv.jpg")
            if (couv.exists() && couv.isFile && couv.canRead()) {
                setImages(BitmapDrawable(context.resources, BitmapFactory.decodeFile(couv.absolutePath)), false)
            }
            else
                setImages(context.getDrawable(R.drawable.couvencours), )
        } else {
            val imageUrl = "https://www.canardpc.com/numero/" + item.number
            // id = "numero-couverture" src = url
            // if src contains "couvencours" > temp
            //GlideApp.with(holder.v.context).load(item.imageUrl)..placeholder(defaultPictures[random.nextInt(4)]).into(image)
        }
    }

    private fun setImages(drawable: Drawable, isTemp: Boolean, isDownloaded: Boolean, holder: Adapter.ViewHolder<MagasineInfo>)
    {
        holder.v.findViewById<ImageView>(R.id.coverMagasineImage).setImageDrawable(drawable)
        if (isTemp)
            holder.v.findViewById<ImageView>(R.id.backgroundBlurImage).setImageDrawable(holder.v.context.getDrawable(R.drawable.couv380_blurred))
        else
        {
        //blur
        }
        if (!isDownloaded) {
            val  matrix = ColorMatrix();
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

open class MagListRecycler : IRecycler<MagasineInfo>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_magasine
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName: String = ""

    override val binder = RoomSearchBinder()
    override val itemDecoration = LinearItemDecoration(2)

    private lateinit var lastNum: MagasineInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)

        val recycler = rootView!!.findViewById<RecyclerView>(recycleId)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView!!.canScrollVertically(1)) {
                    addMagasines()
                }
            }
        })
        createFirstMagasines()
        return rootView
    }

    private fun createFirstMagasines() {
        val list = mutableListOf<MagasineInfo>()
        val cal = Calendar.getInstance();
        val week = cal.get(Calendar.WEEK_OF_MONTH)
        val date = DateCustom(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), week < 3)

        val num376 = DateCustom(2018, 3, true) //376: March 1 2018
        var addNum = 0;
        while (date != num376) {
            list.add(MagasineInfo(false, 0, date))
            addNum++;
            date.decrease()
        }

        var currentNum = 376 + addNum;
        if (list.size > 6)
            list.subList(6, list.size).clear()

        for (elem in list) {
            elem.number = currentNum--
            elem.downloaded = isNumDownloaded(elem.number)
        }

        lastNum = list.last()
        adapter?.add(list)
        adapter?.notifyDataSetChanged()
    }

    private fun addMagasines() {
        val list = mutableListOf<MagasineInfo>()
        for (i in 0..6) {
            if (lastNum.number == 348) //first number published on website
                break
            lastNum.number--;
            lastNum.date.decrease()
            lastNum.downloaded = isNumDownloaded(lastNum.number)
            list.add(lastNum)

            adapter?.add(list)
            adapter?.notifyDataSetChanged()

        }
    }

    private fun isNumDownloaded(num: Int): Boolean {
        val dlFolder = TmpUtils.getFilesPath(context!!)
        val numDirFolder = File(dlFolder, num.toString())
        if (numDirFolder.exists() && numDirFolder.isDirectory)
            return true
        return false
    }

    override fun fillDataObject(json: JSONObject): MagasineInfo {
        return MagasineInfo(false, 0, DateCustom(0, 0, false))
        /*stub*/
    }


    override fun setLayoutManager(): RecyclerView.LayoutManager {
        //first result will be bigger than the rest
        val manager = GridLayoutManager(activity, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0)
                    return 2
                return 1
            }
        }
        return manager
    }

}

class LinearItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        outRect.top = spaceHeight
    }
}
