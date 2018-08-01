package fr.tikione.c2e.core.service.home

import fr.tikione.c2e.core.model.home.MagazineSummary
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class LocalReaderServiceImpl : LocalReaderService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun listDownloadedMagazines(folder: File): List<MagazineSummary> {
        log.info("analyse du repertoire $folder")
        return folder
                .listFiles { _, name -> name.endsWith(".html") }
                .filter { file -> !file.name.startsWith("CPC-") }
                .map { file ->
                    val filename = file.name
                    val mag = MagazineSummary()
                    val size = file.length()
                    if (size > 4 * 1024 * 1014) mag.humanSize = ((size / (1024 * 1014)).toString() + " Mo") else mag.humanSize = ((size / 1024).toString() + " Ko")
                    if (filename.contains("-")) {
                        mag.number = filename.substring("CPC".length, filename.indexOf("-"))
                        mag.file = file
                        when {
                            filename.contains("-nopic") -> mag.options = "sans images"
                            filename.contains("-resize") -> {
                                val picRatio = filename.substring(filename.indexOf("-resize") + "-resize".length, filename.indexOf("."))
                                mag.options = "images ratio $picRatio%"
                            }
                            else -> mag.options = "images ratio 100%"
                        }
                    } else {
                        mag.number = filename.substring("CPC".length, filename.indexOf("."))
                        mag.file = file
                        mag.options = "images ratio 100%"
                    }
                    log.info("magazine trouve : $mag")
                    mag
                }
                .toList()
    }
}
