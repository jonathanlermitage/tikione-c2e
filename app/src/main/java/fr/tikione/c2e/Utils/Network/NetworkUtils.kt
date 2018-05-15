package fr.tikione.c2e.Utils.Network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import fr.tikione.c2e.BuildConfig
import fr.tikione.c2e.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import com.android.volley.NetworkResponse



/**
 * Created by tuxlu on 19/12/17.
 */

object NetworkUtils {

    fun isConnected(context: Context): Boolean {
        val mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = mgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    fun showConnectionErrorToast(context: Context) {
        Toast.makeText(context, context.getString(R.string.no_network), Toast.LENGTH_LONG).show()
    }

    fun checkNetworkError(context: Context, error: VolleyError, url: String, errorListener: Response.ErrorListener?) {
        if (error is NetworkError || error is TimeoutError) {
            showConnectionErrorToast(context)
            return;
        }
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
            if (error.networkResponse != null) {
                val resp = error.networkResponse
                val data = String(resp.data)
                Log.wtf("NETWORK", "code = " + resp.statusCode +
                        "\n url= " + url +
                        "\n data=" + data.substring(0, Math.min(data.length, 40)) + "...")
            }
        }

        errorListener?.onErrorResponse(error)

    }

    fun HTMLrequest(context: Context, method: Int, url: String,
                    listener: Response.Listener<Document>,
                    errorListener: Response.ErrorListener?) {
        if (!isConnected(context)) { return showConnectionErrorToast(context) }
        val req = HTMLRequest(method, url, listener, errorListener, context)
        addRequestToQueue(context, req)
    }

    fun ImageRequest(context: Context, url: String,
                     listener: Response.Listener<Drawable>,
                     errorListener: Response.ErrorListener?) {
        if (!isConnected(context)) { return showConnectionErrorToast(context) }
        val req = PictureRequest(url, listener, errorListener, context)
        addRequestToQueue(context, req)
    }


    private fun <T> addRequestToQueue(context: Context, request: Request<T>) {
        //avoid timeout
        request.retryPolicy = DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        VHttp.getInstance(context.applicationContext).addToRequestQueue(request)
    }


    class PictureRequest(url: String, listener: Response.Listener<Drawable>, errorListener: Response.ErrorListener?, context: Context) :
            ImageRequest(url,
                    Response.Listener { bitmap ->
                        listener.onResponse(BitmapDrawable(context.resources, bitmap))
                    }, 0, 0, null, Bitmap.Config.RGB_565,
                    Response.ErrorListener { error ->
                        checkNetworkError(context, error, url, errorListener)
                    })


    class HTMLRequest(method: Int, url: String, listener: Response.Listener<Document>?,
                      errorListener: Response.ErrorListener?,
                      context: Context) : StringRequest(method, url,
            Response.Listener { response ->
                listener?.onResponse(Jsoup.parse(response.toString()))
            },
            Response.ErrorListener { error ->
                checkNetworkError(context, error, url, errorListener)
            }) {

        var statusCode :Int = 0;

        override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
            statusCode = response.statusCode
            if (statusCode == 404)
                return Response.error(VolleyError(NetworkResponse(404, null, false, 0, null)))
            return super.parseNetworkResponse(response)
        }

    }
}
