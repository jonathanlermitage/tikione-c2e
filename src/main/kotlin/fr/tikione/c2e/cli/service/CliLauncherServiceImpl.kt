package fr.tikione.c2e.cli.service

import com.github.salomonbrys.kodein.instance
import compat.Tools
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.index.IndexWriterService
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class CliLauncherServiceImpl : CliLauncherService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(IOException::class, InterruptedException::class)
    override fun start(args: Array<String>) {
        assert(args.size > 2)
        val argsToShow = args.clone()
        if (argsToShow.isNotEmpty()) {
            argsToShow[0] = "(identifiant)"
            argsToShow[1] = "(mot de passe)"
        }
        log.info("les parametres de lancement sont : {}", Arrays.toString(argsToShow))
        val argsList = Arrays.asList(*args)
        Tools.debug = argsList.contains("-debug")

        val switchList = Arrays.asList(*args).subList(2, args.size)

        // Proxy declaration
        if (args.contains("-sysproxy")) {
            Tools.setSysProxy()
        } else {
            args.filter { arg -> arg.startsWith("-proxy:") }
                    .map { arg -> arg.substring("-proxy:".length).split(":") }
                    .filter { proxy -> 2 == proxy.size }
                    .forEach { proxy -> Tools.setProxy(proxy[0], proxy[1]) }
        }

        try {
            val latestVersion = Jsoup.connect(Tools.VERSION_URL).get().text().trim()
            if (Tools.VERSION != latestVersion) {
                log.warn("<< une nouvelle version de TikiOne C2E est disponible (" + latestVersion + "), " +
                        "rendez-vous sur https://github.com/jonathanlermitage/tikione-c2e/releases >>")
            }
        } catch (e: Exception) {
            log.warn("impossible de verifier la presence d'une nouvelle version de TikiOne C2E", e)
        }

        val doList = switchList.contains("-list")
        val doIncludePictures = !switchList.contains("-nopic")
        val doIndex = switchList.contains("-index")
        var doHtml = false
        val doAllMags = switchList.contains("-cpcall")
        val doResize = args.firstOrNull { it.startsWith("-resize") }?.substring("-resize".length)
        val cpcAuthService: CPCAuthService = kodein.instance()
        val cpcReaderService: CPCReaderService = kodein.instance()

        val auth = cpcAuthService.authenticate(args[0], args[1])
        val headers = cpcReaderService.listDownloadableMagazines(auth)
        val magNumbers = ArrayList<String>()
        if (doAllMags) {
            magNumbers.addAll(headers)
            doHtml = true
        } else {
            args.filter { it.startsWith("-cpc") }.forEach {
                magNumbers.add(it.substring("-cpc".length))
                doHtml = true
            }
        }
        if (doList) {
            log.info("les numeros disponibles sont : {}", headers)
        }

        if (doHtml) {
            if (magNumbers.size > 1) {
                log.info("telechargement des numeros : {}", magNumbers)
            }
            for (i in magNumbers.indices) {
                val magNumber = magNumbers[i]
                val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
                val file = File("CPC" + magNumber
                        + (if (doIncludePictures) "" else "-nopic")
                        + (if (doResize == null) "" else "-resize$doResize")
                        + ".html")
                val writerService: HtmlWriterService = kodein.instance()
                writerService.write(magazine, file, doIncludePictures, doResize)
                if (i != magNumbers.size - 1) {
                    log.info("pause de ${Tools.PAUSE_BETWEEN_MAG_DL}s avant de telecharger le prochain numero")
                    TimeUnit.SECONDS.sleep(Tools.PAUSE_BETWEEN_MAG_DL)
                    log.info("ok\n")
                }
            }
        }

        if (doIndex) {
            log.info("creation de l'index de tous les numeros disponibles")
            val file = File("CPC-index.csv")
            val writerService: IndexWriterService = kodein.instance()
            writerService.write(auth, headers, file)
        }

        log.info("termine !")
    }
}
