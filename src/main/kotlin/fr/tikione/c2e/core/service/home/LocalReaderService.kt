package fr.tikione.c2e.core.service.home

import fr.tikione.c2e.core.model.home.MagazineSummary
import java.io.File
import java.io.IOException

interface LocalReaderService {

    @Throws(IOException::class)
    fun listDownloadedMagazines(folder: File): List<MagazineSummary>
}
