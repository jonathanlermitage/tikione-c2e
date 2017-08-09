package fr.tikione.c2e

import com.google.common.base.Strings
import fr.tikione.c2e.service.html.HtmlWriterService
import fr.tikione.c2e.service.html.HtmlWriterServiceImpl
import fr.tikione.c2e.service.web.CPCAuthService
import fr.tikione.c2e.service.web.CPCAuthServiceImpl
import fr.tikione.c2e.service.web.scrap.CPCReaderService
import fr.tikione.c2e.service.web.scrap.CPCReaderServiceImpl
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object Main {

    var log: Logger = LoggerFactory.getLogger(this.javaClass)

    var DEBUG = false
    var VERSION = "1.2.3"
    private val VERSION_URL = "https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/uc/latest_version.txt"

    // params: username password [-gui] [-debug] [-list] [-cpc360 -cpc361...|-cpcall] [-html] [-nopic] [-compresspic]
    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {
        log.info("TikiOne C2E version {}, Java {}", VERSION, System.getProperty("java.version"))
        try {
            val latestVersion = Jsoup.connect(VERSION_URL).get().text()
            if (VERSION != latestVersion) {
                log.warn("<< une nouvelle version de TikiOne C2E est disponible (" + latestVersion + "), " +
                        "rendez-vous sur https://github.com/jonathanlermitage/tikione-c2e/releases >>")
            }
        } catch (e: Exception) {
            log.warn("impossible de vérifier la présence d'une nouvelle version de TikiOne C2E", e)
        }
        val argsToShow = args.clone()
        if (argsToShow.isNotEmpty()) {
            argsToShow[1] = Strings.repeat("*", argsToShow[1].length)
        }
        log.info("les paramètres de lancement sont : {}", Arrays.toString(argsToShow))
        val argsList = Arrays.asList(*args)
        DEBUG = argsList.contains("-debug")
        startCLI(*args)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun startCLI(vararg args: String) {
        assert(args.size > 2)
        val switchList = Arrays.asList(*args).subList(2, args.size)
        val list = switchList.contains("-list")
        val includePictures = !switchList.contains("-nopic")
        val compressPictures = switchList.contains("-compresspic")
        val doHtml = switchList.contains("-html")
        val allMags = switchList.contains("-cpcall")

        val cpcAuthService: CPCAuthService = CPCAuthServiceImpl()
        val cpcReaderService: CPCReaderService = CPCReaderServiceImpl()

        val auth = cpcAuthService.authenticate(args[0], args[1])
        val headers = cpcReaderService.listDownloadableMagazines(auth)
        val magNumbers = ArrayList<Int>()
        if (allMags) {
            magNumbers.addAll(headers)
        } else {
            args.filter { it.startsWith("-cpc") }.forEach {
                try {
                    magNumbers.add(Integer.parseInt(it.substring("-cpc".length)))
                } catch (nfe: NumberFormatException) {
                    log.debug("un numéro du magazine CPC est mal tapé, il sera ignoré")
                }
            }
        }
        if (list) {
            log.info("les numéros disponibles sont : {}", headers)
        }

        if (doHtml) {
            if (magNumbers.size > 1) {
                log.info("téléchargement des numéros : {}", magNumbers)
            }
            for (i in magNumbers.indices) {
                val magNumber = magNumbers[i]
                val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
                val file = File("CPC" + magNumber
                        + (if (includePictures) "" else "-nopic")
                        + (if (compressPictures) "-compresspic" else "")
                        + ".html")
                val writerService: HtmlWriterService = HtmlWriterServiceImpl()
                writerService.write(magazine, file, includePictures, compressPictures)
                if (i != magNumbers.size - 1) {
                    log.info("pause de 30s avant de télécharger le prochain numéro")
                    for (j in 0..29) {
                        TimeUnit.SECONDS.sleep(1)
                    }
                    log.info(" ok\n")
                }
            }
        }

        log.info("terminé !")
    }
}
