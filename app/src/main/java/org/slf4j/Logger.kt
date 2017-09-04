package org.slf4j

import android.util.Log
import java.util.*

class Logger {

    fun debug(vararg o: Any) {
        Log.d("", Arrays.toString(o))
    }

    fun info(vararg o: Any) {
        Log.i("", Arrays.toString(o))
    }

    fun warn(vararg o: Any) {
        Log.w("", Arrays.toString(o))
    }

    fun error(vararg o: Any) {
        Log.e("", Arrays.toString(o))
    }
}
