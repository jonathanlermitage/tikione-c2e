package fr.tikione.c2e

/**
 * Created by tuxlu on 12/11/17.
 */

data class DateCustom(var year: Int,
                      var month: Int,
                      var firstWeek: Boolean) {

    fun decrease()
    {
        if (year >= 2018 && month >= 8) //last monthly mag
        {
            month--
            firstWeek = true
        } else {
            firstWeek = !firstWeek
            if (!firstWeek)
                month--
        }
        computeMonth(-1)
    }

    fun increase() {
        if (year >= 2018 && month >= 7) //becomes monthly
        {
            month++
            firstWeek = true
        } else {
            firstWeek = !firstWeek
            if (firstWeek)
                month++
        }
        computeMonth(1)
    }

    private fun computeMonth(increaseVal: Int) {
        when (month) {
            13 -> {
                month = 1;
                year++
            }
            0 -> {
                month = 12
                year--
            }
            8 -> {
                month += increaseVal
            } //no mag in August
        }
    }
}