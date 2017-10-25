package fr.tikione.c2e.service.index

import fr.tikione.c2e.model.web.Auth
import java.io.File
import java.io.IOException

interface IndexWriterService {

    @Throws(IOException::class)
    fun write(auth: Auth, magNumbers: List<String>, file: File)
}
