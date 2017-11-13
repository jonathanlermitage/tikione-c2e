package fr.tikione.c2e.service.web.scrap

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals


/**
 * @author GUAM
 * 
 * 
 * 
 * 10.11.2017 - 13:50
 */
internal class CPCScraperServiceImplTest {
    val scrapper = CPCScraperServiceImpl()
    
    @Test
    fun testArticleDateParsing() {
        val extractedDate = scrapper.extractDate("Par Maria Kalash | le 29 août 2017")
        val date = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(extractedDate)
        assertEquals("29 août 2017", date)
    }

    @Test
    fun testArticleAuthorParsing() {
        val extractedAuthor = scrapper.extractAuthor("Par Maria Kalash | le 29 août 2017")
        assertEquals("Maria Kalash", extractedAuthor)
    }
}