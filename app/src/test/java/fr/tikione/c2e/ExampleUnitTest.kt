package fr.tikione.c2e

import android.util.Log
import fr.tikione.c2e.Utils.TmpUtils.Companion.dateToString
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun date_format() {
        assertEquals("1er déc 2018", DateCustom(2018, 12, true).dateToString())
    }

    @Test
    fun list_magic() {
        val list = intArrayOf(10, 20, 30, 40, 50).toMutableList()
        list.subList(3, list.size).clear()
        assertEquals(intArrayOf(10, 20, 30).toMutableList(), list)

        val l2 = intArrayOf(10, 20, 30, 40, 50).toMutableList()
        System.out.println(l2.last());

        //for (elem in l2)
            //System.out.println(elem.toString());
    }
}
