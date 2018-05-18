package fr.tikione.c2e.Utils.Recyclers

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import fr.tikione.c2e.Utils.LoadingUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



/**
 * Created by tuxlu on 12/11/17.
 */


abstract class IRecycler<T: Any> : Fragment() {

    protected var saveJArray: JSONArray = JSONArray()
    abstract val layoutListId: Int
    abstract val layoutObjectId: Int
    abstract val recycleId: Int
    abstract val requestObjectName : String

    abstract val itemDecoration: RecyclerView.ItemDecoration?
    abstract val binder:  ViewHolderBinder<T>?

    protected var adapter: Adapter<T>? = null

    abstract fun fillDataObject(json: JSONObject): T
    abstract fun setLayoutManager(): RecyclerView.LayoutManager

    lateinit var rootView : View
    protected var noResView : View? = null
    protected var isNoResViewVisible : Boolean = true

    public fun setLoadingStatus(status: Boolean)
    {
        if (!::rootView.isInitialized)
            return
        if (status)
            LoadingUtils.StartLoadingView(rootView, context)
        else
            LoadingUtils.EndLoadingView(rootView)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(layoutListId, container, false)
        //LoadingUtils.EndLoadingView(rootView)
        val recycler = rootView.findViewById<RecyclerView>(recycleId)
        recycler.setHasFixedSize(true)

        val layoutManager = setLayoutManager()
        if (itemDecoration != null)
            recycler.addItemDecoration(itemDecoration)
        recycler.layoutManager = layoutManager

        return rootView
    }

    fun setNoResViewVisibility(nvis : Boolean)
    {
        isNoResViewVisible = nvis
    }

    protected fun getAddData(data: JSONArray, replace: Boolean) : MutableList<T>
    {
        if (replace)
            saveJArray = data
        else
        {
            for (i in 0..(data.length() - 1))
                saveJArray.put(data.get(i))
        }

        val list : MutableList<T> = parseJSON(data)

        if (noResView != null) {
            if (list.size == 0 && isNoResViewVisible)
                noResView!!.visibility = View.VISIBLE
            else
                noResView!!.visibility = View.INVISIBLE
        }
        return list
    }

    open fun add(data: JSONArray, replace: Boolean=false)
    {
        val list = getAddData(data, replace)
        if (replace || list.size == 0)
            adapter?.clear()
        if (list.size != 0)
            adapter?.add(parseJSON(data))
        adapter?.notifyDataSetChanged()
        if (::rootView.isInitialized)
            LoadingUtils.EndLoadingView(rootView)
    }

    open fun clear()
    {
        adapter?.clear()
        adapter?.notifyDataSetChanged()
    }

    protected fun parseJSON(jArray: JSONArray): MutableList<T> {
        val data = ArrayList<T>()
        if (jArray.length() == 0)
            return data
        for (i in 0 until jArray.length()) {
            val obj : T
            try {
                val json = jArray.getJSONObject(i)
                obj = fillDataObject(json)
                data.add(obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return data
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recycler = rootView.findViewById<RecyclerView>(recycleId)
        adapter = Adapter(activity as Context, ArrayList(), layoutObjectId, binder!!)
        recycler.adapter = adapter

        if (savedInstanceState != null) {
            val json =  savedInstanceState.getString("json")
            add(JSONArray(json), true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("json", saveJArray.toString())
        super.onSaveInstanceState(outState)

    }

}
