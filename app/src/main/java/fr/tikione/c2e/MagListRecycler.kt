package fr.tikione.c2e

import android.graphics.*
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Response
import fr.tikione.c2e.Utils.LoadingUtils
import fr.tikione.c2e.Utils.Recyclers.IRecycler
import fr.tikione.c2e.Utils.Network.NetworkUtils
import fr.tikione.c2e.Utils.TmpUtils
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.*


data class MagasineInfo(var downloaded: Boolean = false,
                        var number: Int = 0,
                        var date: DateCustom,
                        var isMostRecent: Boolean = false,
                        var bitmap: Bitmap? = null)


open class MagListRecycler : IRecycler<MagasineInfo>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_magasine
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName: String = ""

    override var binder = MagListBinder()
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



        try {
            binder.imageUrlMap = TmpUtils.readObjectFile(binder.imageUrlMapFilename, context!!)
        } catch (e: FileNotFoundException) {
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createFirstMagasines()
    }

    private fun createFirstMagasines() {
        val list = mutableListOf<MagasineInfo>()
        val cal = Calendar.getInstance();
        val week = cal.get(Calendar.WEEK_OF_MONTH)
        val date = DateCustom(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, week < 3)
        val todayDate = date.copy()

        val num376 = DateCustom(2018, 3, true) //376: March 1 2018
        var addNum = 0
        while (date != num376) {
            list.add(MagasineInfo(false, 0, date.copy()))
            addNum++
            date.decrease()
        }

        var currentNum = 376 + addNum;
        todayDate.increase()
        var nextNum = MagasineInfo(isNumDownloaded(currentNum + 1), currentNum + 1, todayDate, true);

        if (list.size > 8)
            list.subList(8, list.size).clear()

        for (elem in list) {
            elem.number = currentNum--
            elem.downloaded = isNumDownloaded(elem.number)
        }

        lastNum = list.last().copy()
        lastNum.date = lastNum.date.copy()
        adapter?.add(list)
        addLatestMagasine(nextNum)
        LoadingUtils.EndLoadingView(rootView)

    }

    private fun addMagasines() {
        val list = mutableListOf<MagasineInfo>()
        for (i in 0..6) {
            if (lastNum.number == 348) //first number published on website
                break
            lastNum.date = lastNum.date.copy()
            lastNum.number--;
            lastNum.date.decrease()
            lastNum.downloaded = isNumDownloaded(lastNum.number)
            list.add(lastNum.copy())
        }
        if (list.size != 0) {
            adapter?.add(list)
        }
    }

    private fun addLatestMagasine(mag: MagasineInfo) {
        if (mag.downloaded || binder.imageUrlMap.containsKey(mag.number))
            adapter?.addElemAtStart(mag)
        else {
            NetworkUtils.HTMLrequest(context!!, com.android.volley.Request.Method.GET,
                    "https://www.canardpc.com/numero/" + mag.number,
                    Response.Listener { _ ->
                        this.adapter!!.addElemAtStart(mag)
                        binder.imageUrlMap[mag.number] = ""
                    }, null)
        }
    }

    private fun isNumDownloaded(num: Int): Boolean {
        val dlFolder = TmpUtils.getFilesPath(context!!)
        val numDirFolder = File(dlFolder, num.toString())
        if (numDirFolder.exists() && numDirFolder.isDirectory)
            return true
        return false
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

    override fun onDestroy() {
        super.onDestroy()
        TmpUtils.writeObjectFile(binder.imageUrlMap, binder.imageUrlMapFilename, context!!)
    }

    override fun fillDataObject(json: JSONObject): MagasineInfo {
        return MagasineInfo(false, 0, DateCustom(0, 0, false))
        /*stub*/
    }


}

class LinearItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        outRect.top = spaceHeight
    }
}
