package fr.tikione.c2e.cli.service

import com.github.salomonbrys.kodein.instance
import compat.Tools
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.service.home.LocalReaderService
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.index.IndexWriterService
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
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

        val doUpgrade = switchList.contains("-up")
        try {
            val latestVersion = Jsoup.connect(Tools.VERSION_URL).get().text().trim()
            if (Tools.VERSION != latestVersion) {
                if (doUpgrade) {
                    val upgradeFile = File("c2e-$latestVersion.zip")
                    if (upgradeFile.exists()) {
                        log.info("<< une nouvelle version de TikiOne C2E ($latestVersion) a deja ete telechargee (${upgradeFile.absolutePath}), libre a vous de l'utiliser >>")
                    } else {
                        log.info("<< une nouvelle version de TikiOne C2E va etre telechargee ($latestVersion) >>")
                        val upgradeFileUrl = "https://github.com/jonathanlermitage/tikione-c2e/releases/download/v$latestVersion/c2e-$latestVersion.zip"
                        FileUtils.copyURLToFile(URL(upgradeFileUrl), upgradeFile)
                        log.info("<< TikiOne C2E $latestVersion a ete telechargee (${upgradeFile.absolutePath}), libre a vous de l'utiliser >>")
                    }
                } else {
                    log.warn("<< une nouvelle version de TikiOne C2E est disponible ($latestVersion), " +
                            "rendez-vous sur https://github.com/jonathanlermitage/tikione-c2e/releases ou relancez C2E avec la paramètre -up >>")
                }
            }
        } catch (e: Exception) {
            log.warn("impossible de verifier la presence d'une nouvelle version de TikiOne C2E", e)
        }

        val doList = switchList.contains("-list")
        val doIncludePictures = !switchList.contains("-nopic")
        val doIndex = switchList.contains("-index")
        val doDarkMode = switchList.contains("-dark")
        var doHtml = false
        val doAllMags = switchList.contains("-cpcall")
        val doAllMissing = switchList.contains("-cpcmissing")
        val doResize = args.firstOrNull { it.startsWith("-resize") }?.substring("-resize".length)
        val doHome = switchList.contains("-home")
        val doDysfont = switchList.contains("-dysfont")
        val doColumn = !switchList.contains("-nocolumn")

        var directory = "."
        args.filter { arg -> arg.startsWith("-directory:") }
                .map { arg -> arg.substring("-directory:".length) }
                .forEach { dd -> directory = dd }
        try {
            log.info("Le dossier de destination est {}", directory)
            File(directory).mkdirs()
        } catch (e: Exception) {
            log.warn("impossible de creer le dossier de destination", e)
        }

        var fontsize = "1em"
        args.filter { arg -> arg.startsWith("-fontsize:") }
                .map { arg -> arg.substring("-fontsize:".length) }
                .forEach { fs -> fontsize = fs }
        val customCss = "body { font-size: $fontsize; }"

        val cpcAuthService: CPCAuthService = kodein.instance()
        val cpcReaderService: CPCReaderService = kodein.instance()

        val auth = cpcAuthService.authenticate(args[0], args[1])
        val headers = cpcReaderService.listDownloadableMagazines(auth)
        val magNumbers = ArrayList<String>()

        when {
            doAllMags -> {
                magNumbers.addAll(headers)
                doHtml = true
            }
            doAllMissing -> {
                magNumbers.addAll(headers)
                val existing = listDownloadedMags(directory)
                if (existing.isNotEmpty()) {
                    log.info("les numeros deja presents ne seront pas telecharges : {}", existing)
                }
                magNumbers.removeAll(existing)
                doHtml = true
            }
            else -> args.filter { it.startsWith("-cpc") }.forEach {
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
            } else if (magNumbers.isEmpty()) {
                log.info("il ne reste aucun numero a telecharger")
            }
            for (i in magNumbers.indices) {
                val magNumber = magNumbers[i]
                val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
                val file = File(makeMagFilename(directory, magNumber, doIncludePictures, doResize))
                val writerService: HtmlWriterService = kodein.instance()
                writerService.write(magazine, file, doIncludePictures, doResize, doDarkMode, customCss, doDysfont, doColumn)
                if (i != magNumbers.size - 1) {
                    log.info("pause de ${Tools.PAUSE_BETWEEN_MAG_DL}s avant de telecharger le prochain numero")
                    TimeUnit.SECONDS.sleep(Tools.PAUSE_BETWEEN_MAG_DL)
                    log.info("ok\n")
                }
            }
        }

        if (doIndex) {
            log.info("creation de l'index de tous les numeros disponibles")
            val file = File("$directory/CPC-index.csv")
            val writerService: IndexWriterService = kodein.instance()
            writerService.write(auth, headers, file)
        }

        if (doHome) {
            log.info("creation de la page d'accueil CPC-home.html")
            val file = File("$directory/CPC-home.html")
            val localReaderService: LocalReaderService = kodein.instance()
            val writerService: HtmlWriterService = kodein.instance()
            writerService.write(localReaderService.listDownloadedMagazines(File("./")), file, doDysfont)
        }

        log.info("termine !")
    }

    private fun listDownloadedMags(directory: String = "."): List<String> =
            File(directory).listFiles { _, name -> name.startsWith("CPC") && name.contains('-') && name.toUpperCase().endsWith(".HTML") }
                    .map { file -> file.name.substring("CPC".length, file.name.indexOf('-')) }
                    .sortedDescending()

    private fun makeMagFilename(directory: String = ".", magNumber: String, doIncludePictures: Boolean, doResize: String?): String =
            directory + "/CPC" + magNumber + (if (doIncludePictures) "" else "-nopic") + (if (doResize == null) "" else "-resize$doResize") + ".html"

}
