package fr.tikione.c2e.core.service.index

import fr.tikione.c2e.core.model.web.Auth
import java.io.File
import java.io.IOException

interface IndexWriterService {

    /**
     * Write a CSV index file of given magazines.
     */
    @Throws(IOException::class)
    fun writeCSV(auth: Auth, magNumbers: ArrayList<String>, file: File)

    /**
     * Write an HTML index file of given magazines, from existing CSV index.
     */
    @Throws(IOException::class)
    fun writeHTMLFromCSV(htmlFile: File, existingCsvFile: File)
}
