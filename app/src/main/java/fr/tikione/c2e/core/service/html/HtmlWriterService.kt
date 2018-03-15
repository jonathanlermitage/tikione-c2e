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
     * @param incluePictures should include magazine pictures in HTML file?
     * @param resize resize screenshots (percents). Use null to disable resize.
     * @param dark activate dark-mode by default.
     * @param customCss custom CSS.
     */
    @Throws(IOException::class)
    fun write(magazine: Magazine, file: File, incluePictures: Boolean, resize: String?, dark: Boolean, customCss: String?)

    /**
     * Write a home page that enumerates downloaded magazines.
     * @param magazines magazines.
     * @param file HTML file.
     */
    @Throws(IOException::class)
    fun write(magazines: List<MagazineSummary>, file: File)
}
