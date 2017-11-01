package fr.tikione.c2e.service.index

import fr.tikione.c2e.model.web.Auth
import java.io.File
import java.io.IOException

interface IndexWriterService {

    /**
     * Write an index file of given magazines.
     */
    @Throws(IOException::class)
    fun write(auth: Auth, magNumbers: ArrayList<String>, file: File)
}
