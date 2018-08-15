package fr.tikione.c2e.cli.service

import com.github.salomonbrys.kodein.instance
import fr.tikione.c2e.core.Tools
import fr.tikione.c2e.core.cfg.Cfg
import fr.tikione.c2e.core.coreKodein
import fr.tikione.c2e.core.service.home.LocalReaderService
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.index.IndexWriterService
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
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

        val switchList = Arrays.asList(*args).drop(2)

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
            if (versionStrToInt(latestVersion) > versionStrToInt(Tools.VERSION)) {
                if (doUpgrade) {
                    val upgradeFile = File("c2e-$latestVersion.zip")
                    if (upgradeFile.exists()) {
                        log.info("<< une nouvelle version de TikiOne C2E ($latestVersion) a deja ete telechargee (${upgradeFile.canonicalPath}), libre a vous de l'utiliser >>")
                    } else {
                        log.info("<< une nouvelle version de TikiOne C2E va etre telechargee ($latestVersion) >>")
                        val upgradeFileUrl = URL("https://github.com/jonathanlermitage/tikione-c2e/releases/download/v$latestVersion/c2e-$latestVersion.zip")
                        upgradeFile.writeBytes(upgradeFileUrl.readBytes())
                        log.info("<< TikiOne C2E $latestVersion a ete telechargee (${upgradeFile.canonicalPath}), libre a vous de l'utiliser >>")
                    }
                } else {
                    log.warn("<< une nouvelle version de TikiOne C2E est disponible ($latestVersion), " +
                            "rendez-vous sur https://github.com/jonathanlermitage/tikione-c2e/releases ou relancez C2E avec la parametre -up >>")
                }
            }
        } catch (e: Exception) {
            log.warn("impossible de verifier la presence d'une nouvelle version de TikiOne C2E", e)
        }

        val cfg: Cfg = coreKodein.instance()
        cfg.doList = switchList.contains("-list")
        cfg.doIncludePictures = !switchList.contains("-nopic")
        cfg.doIndex = switchList.contains("-index")
        cfg.doDarkMode = switchList.contains("-dark")
        cfg.doHtml = false
        cfg.doAllMags = switchList.contains("-cpcall")
        cfg.doAllMissing = switchList.contains("-cpcmissing")
        cfg.resize = args.firstOrNull { it.startsWith("-resize") }?.substring("-resize".length)
        cfg.doHome = switchList.contains("-home")
        cfg.doDysfont = switchList.contains("-dysfont")
        cfg.doColumn = !switchList.contains("-nocolumn")

        args.filter { arg -> arg.startsWith("-directory:") }
                .map { arg -> arg.substring("-directory:".length) }
                .forEach { dd -> cfg.directory = dd }
        try {
            log.info("le dossier de destination est : {}", if ("." == cfg.directory) "le repertoire de l'application" else cfg.directory)
            File(cfg.directory).mkdirs()
        } catch (e: Exception) {
            log.warn("impossible de creer le dossier de destination", e)
        }

        var fontsize = "1em"
        args.filter { arg -> arg.startsWith("-fontsize:") }
                .map { arg -> arg.substring("-fontsize:".length) }
                .forEach { fs -> fontsize = fs }
        cfg.customCss = "body { font-size: $fontsize; }"

        val cpcAuthService: CPCAuthService = coreKodein.instance()
        val cpcReaderService: CPCReaderService = coreKodein.instance()

        val auth = cpcAuthService.authenticate(args[0], args[1])
        val headers = cpcReaderService.listDownloadableMagazines(auth)
        val magNumbers = ArrayList<String>()

        when {
            cfg.doAllMags -> {
                magNumbers.addAll(headers)
                cfg.doHtml = true
            }
            cfg.doAllMissing -> {
                magNumbers.addAll(headers)
                val existing = listDownloadedMags()
                if (existing.isNotEmpty()) {
                    log.info("les numeros deja presents ne seront pas telecharges : {}", existing)
                }
                magNumbers.removeAll(existing)
                cfg.doHtml = true
            }
            else -> args.filter { it.startsWith("-cpc") }.forEach {
                magNumbers.add(it.substring("-cpc".length))
                cfg.doHtml = true
            }
        }

        if (cfg.doList) {
            log.info("les numeros disponibles sont : {}", headers)
        }

        if (cfg.doHtml) {
            if (magNumbers.size > 1) {
                log.info("telechargement des numeros : {}", magNumbers)
            } else if (magNumbers.isEmpty()) {
                log.info("il ne reste aucun numero a telecharger")
            }
            for (i in magNumbers.indices) {
                val magNumber = magNumbers[i]
                val magazine = cpcReaderService.downloadMagazine(auth, magNumber)
                val file = File(makeMagFilename(cfg.directory, magNumber))
                val writerService: HtmlWriterService = coreKodein.instance()
                writerService.write(magazine, file)
                if (i != magNumbers.size - 1) {
                    log.info("pause de ${Tools.PAUSE_BETWEEN_MAG_DL}s avant de telecharger le prochain numero")
                    TimeUnit.SECONDS.sleep(Tools.PAUSE_BETWEEN_MAG_DL)
                    log.info("ok\n")
                }
            }
        }

        if (cfg.doIndex) {
            log.info("creation des index CSV et HTML de tous les numeros disponibles")
            val csvFile = File("${cfg.directory}/CPC-index.csv")
            val htmlFile = File("${cfg.directory}/CPC-index.html")
            val writerService: IndexWriterService = coreKodein.instance()
            writerService.writeCSV(auth, headers, csvFile)
            writerService.writeHTMLFromCSV(htmlFile, csvFile)
        }

        if (cfg.doHome) {
            log.info("creation de la page d'accueil CPC-home.html")
            val file = File("${cfg.directory}/CPC-home.html")
            val localReaderService: LocalReaderService = coreKodein.instance()
            val writerService: HtmlWriterService = coreKodein.instance()
            writerService.write(localReaderService.listDownloadedMagazines(File(cfg.directory)), file)
        }

        log.info("termine !")
    }

    private fun listDownloadedMags(directory: String = "."): List<String> =
            File(directory).listFiles { _, name -> name.startsWith("CPC") && name.contains('-') && name.toUpperCase().endsWith(".HTML") }
                    .map { file -> file.name.substring("CPC".length, file.name.indexOf('-')) }
                    .sortedDescending()

    private fun makeMagFilename(directory: String = ".", magNumber: String): String {
        val cfg: Cfg = coreKodein.instance()
        return """$directory/CPC$magNumber${if (cfg.doIncludePictures) "" else "-nopic"}${if (cfg.resize == null) "" else "-resize${cfg.resize}"}.html"""
    }

    private fun versionStrToInt(version: String): Int {
        return if ("\\d+\\.\\d+\\.\\d+".toRegex().matches(version)) {
            val versionTokens = version.split('.')
            Integer.parseInt(versionTokens[0]) * 10000 + Integer.parseInt(versionTokens[1]) * 100 + Integer.parseInt(versionTokens[2])
        } else {
            -1
        }
    }
}
