package fr.tikione.c2e

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.android.volley.Response
import fr.tikione.c2e.Utils.Network.NetworkUtils
import fr.tikione.c2e.Utils.TmpUtils
import org.jsoup.nodes.Document
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.FileNotFoundException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("fr.tikione.c2e", appContext.packageName)
    }

    @Test
    fun openWriteFile() {
        val appContext = InstrumentationRegistry.getTargetContext()

        val filename = "testOpenWriteFile.txt"

        try {
            TmpUtils.readObjectFile<HashMap<Int, String>>(filename, appContext)
        } catch (e: Exception) {
            assert(e is FileNotFoundException)
        }

        val map = HashMap<Int, String>()
        map[380] = "test"
        map[170] = "170"
        TmpUtils.writeObjectFile(map, filename, appContext)

        val map2 = TmpUtils.readObjectFile<HashMap<Int, String>>(filename, appContext)
        assertEquals("test", map2[380])
        assertEquals("170", map2[170])
    }

    @Test
    fun checkNonExistingCPCPage()
    {
        val appContext = InstrumentationRegistry.getTargetContext()

        //did you know? Canard PC numéro 999 existe! au moins sur le site...
        NetworkUtils.HTMLrequest(appContext, com.android.volley.Request.Method.GET, "https://www.canardpc.com/numero/1234",
                Response.Listener<Document> { response ->
                    assertEquals("this code should'nt be reached!","")
                },
                Response.ErrorListener { error ->
                    if (error.networkResponse != null)
                    assertEquals(404, error.networkResponse.statusCode)
                })
        Thread.sleep(8000)
    }
}
