package fr.tikione.c2e.core.service.index

import fr.tikione.c2e.core.model.index.GameEntries
import java.io.File
import java.io.IOException

interface IndexReaderService {

    /**
     * Read existing index file, then return magazines number and games information.
     */
    @Throws(IOException::class)
    fun read(file: File): GameEntries
}
