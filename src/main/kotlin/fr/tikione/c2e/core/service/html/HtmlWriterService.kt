package fr.tikione.c2e.core.service.html

import fr.tikione.c2e.core.model.home.MagazineSummary
import fr.tikione.c2e.core.model.web.Magazine

import java.io.File
import java.io.IOException

interface HtmlWriterService {

    /**
     * Write a magazine to a new single HTML file.
     * @param magazine magazine.
     * @param file HTML file.
     */
    @Throws(IOException::class)
    fun write(magazine: Magazine, file: File)

    /**
     * Write a home page that enumerates downloaded magazines.
     * @param magazines magazines.
     * @param file HTML file.
     */
    @Throws(IOException::class)
    fun write(magazines: List<MagazineSummary>, file: File)
}
