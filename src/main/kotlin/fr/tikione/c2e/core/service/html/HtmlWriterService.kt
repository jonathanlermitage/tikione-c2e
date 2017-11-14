package fr.tikione.c2e.core.service.html

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
     */
    @Throws(IOException::class)
    fun write(magazine: Magazine, file: File, incluePictures: Boolean, resize: String?)
}
