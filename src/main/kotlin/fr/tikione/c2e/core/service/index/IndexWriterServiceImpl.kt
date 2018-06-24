package fr.tikione.c2e.core.service.index

import com.github.salomonbrys.kodein.instance
import fr.tikione.c2e.core.cfg.Cfg
import fr.tikione.c2e.core.coreKodein
import fr.tikione.c2e.core.model.index.GameEntries
import fr.tikione.c2e.core.model.index.GameEntry
import fr.tikione.c2e.core.model.web.Article
import fr.tikione.c2e.core.model.web.Auth
import fr.tikione.c2e.core.service.AbstractWriter
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class IndexWriterServiceImpl : AbstractWriter(), IndexWriterService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val csvDelimiter = ","
    private val csvDelimiterRegex = csvDelimiter.toRegex()

    @Throws(IOException::class)
    override fun writeCSV(auth: Auth, magNumbers: ArrayList<String>, file: File) {

        if (magNumbers.contains("hs22")) {
            magNumbers.remove("hs22") // TODO maybe detect and remove all HS mags? hs22 contains no game test.
        }

        val games = ArrayList<GameEntry>(magNumbers.size * 24)
        val indexReader: IndexReaderService = coreKodein.instance()

        //whenever we have an issue reading the index file, we rebuild it. This is quite rude, but it will avoid a crash
        //if the file has been compromised or if the index format has changed
        val existingMags: GameEntries = try {
            indexReader.read(file)
        } catch (e: Exception) {
            deleteFile(file)
            GameEntries()
        }

        if (existingMags.magNumbers.size > 0) {
            log.info("les numeros suivants sont deja presents dans l'index CSV, ils ne seront pas retelecharges: {}", existingMags.magNumbers)
            magNumbers.removeAll(existingMags.magNumbers)
            games.addAll(existingMags.games)
            if (magNumbers.size > 0) {
                log.info("il reste {} numeros a telecharger : {}", magNumbers.size, magNumbers)
            } else {
                log.info("il ne reste plus rien a telecharger, l'index CSV est deja a jour")
                log.info("rappel, le fichier d'index CSV est : {}", file.canonicalPath)
                return
            }
        }
        log.info("creation de l'index CSV des numeros : {}", magNumbers)

        deleteFile(file)
        val reader: CPCReaderService = coreKodein.instance()
        magNumbers.forEach { number ->
            val mag = reader.downloadMagazine(auth, number)
            mag.toc.forEach { toc ->
                toc.items.forEach { item ->
                    item.articles?.forEach { article ->
                        if (isGame(article)) {
                            val ge = GameEntry()
                            ge.title = article.title!!
                            ge.magNumber = number
                            ge.author = article.author ?: ""
                            ge.date = formatDate(article.date) ?: ""
                            ge.gameConfig = article.gameConfig ?: ""
                            ge.gameDDL = article.gameDDL ?: ""
                            ge.gameDRM = article.gameDRM ?: ""
                            ge.gameDev = article.gameDev ?: ""
                            ge.gameEditor = article.gameEditor ?: ""
                            ge.gameLang = article.gameLang ?: ""
                            ge.gamePlatform = article.gamePlatform ?: ""
                            ge.gameScore = article.gameScore ?: ""
                            ge.gameTester = article.gameTester ?: ""
                            games.add(ge)
                        }
                    }
                }
            }
        }

        val orderedGames = games.sortedWith(compareBy({ it.title }, { it.magNumber }))

        val content = StringBuilder()
        content.append("titre,numero mag,auteur,date,config,telechargement,DRM,developpeur,editeur,langue,plateforme,score,testeur\n")
        orderedGames.forEach { game ->
            content.append(formatCSV(game.title)).append(",")
                    .append(formatCSV(game.magNumber)).append(",")
                    .append(formatCSV(game.author)).append(",")
                    .append(formatCSV(game.date)).append(",")
                    .append(formatCSV(game.gameConfig)).append(",")
                    .append(formatCSV(game.gameDDL)).append(",")
                    .append(formatCSV(game.gameDRM)).append(",")
                    .append(formatCSV(game.gameDev)).append(",")
                    .append(formatCSV(game.gameEditor)).append(",")
                    .append(formatCSV(game.gameLang)).append(",")
                    .append(formatCSV(game.gamePlatform)).append(",")
                    .append(formatCSV(game.gameScore)).append(",")
                    .append(formatCSV(game.gameTester)).append("\n")
        }
        file.writeText(content.toString(), StandardCharsets.ISO_8859_1)
        log.info("fichier d'index cree : {}", file.canonicalPath)
    }

    private fun deleteFile(file: File) {
        try {
            if (file.exists() && !file.delete())
                throw IOException("impossible de supprimer le fichier : " + file.canonicalPath)
        } catch (e: Exception) {
            throw IOException("impossible de supprimer le fichier : " + file.canonicalPath)
        }
    }

    private fun formatDate(date: Date?): String? {
        if (date == null)
            return ""

        return SimpleDateFormat("yyyy/MM/dd").format(date)
    }

    private fun isGame(a: Article): Boolean =
            a.gamePlatform != null && a.gamePlatform!!.isNotEmpty() && a.title != null && a.title!!.isNotEmpty()

    private fun formatCSV(s: String): String {
        return s.replace(csvDelimiterRegex, " ")
                .replace("Config recommandée :", "")
                .replace("Développeur :", "")
                .replace("DRM :", "")
                .replace("Editeur :", "")
                .replace("Langues :", "")
                .replace("Plateforme :", "")
                .replace("Téléchargement :", "")
                .replace("Testé sur :", "")
                .trim()
    }

    override fun writeHTMLFromCSV(htmlFile: File, existingCsvFile: File) {
        if (!existingCsvFile.exists()) {
            return
        }
        deleteFile(htmlFile)
        var lines = existingCsvFile.readLines(StandardCharsets.ISO_8859_1)
        if (lines.size < 2) {
            return
        }
        val tableContent = StringBuilder()

        // table title
        tableContent.append("\n<tr class=\"title\">\n <td>" + lines.get(0).replace("<>".toRegex(), "").replace(csvDelimiter, "</td>\n <td>") + "</td>\n</tr>\n")

        // table games
        lines = lines.drop(1)
        lines.forEach { csvGameLine ->
            tableContent.append("\n<tr class=\"game\">\n <td>" + csvGameLine.replace("<>".toRegex(), "").replace(csvDelimiter, "</td>\n <td>") + "</td>\n</tr>\n")
        }

        val cfg: Cfg = coreKodein.instance()
        val faviconBase64 = resourceAsBase64("tmpl/html-export/img/french_duck.png")
        val fontRobotoBase64 = findFontAsBase64(cfg.doDysfont)
        val content = resourceAsStr("tmpl/index/index.html")
                .replace("$\$favicon_base64$$", faviconBase64)
                .replace("$\$robotoFont_base64$$", fontRobotoBase64)
                .replace("/*$\$content$$*/", tableContent.toString())

        htmlFile.writeText(content, StandardCharsets.ISO_8859_1)
        log.info("fichier d'index HTML cree : {}", htmlFile.canonicalPath)
    }
}
