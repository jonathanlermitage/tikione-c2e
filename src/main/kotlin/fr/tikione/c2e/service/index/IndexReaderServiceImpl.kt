package fr.tikione.c2e.service.index

import fr.tikione.c2e.model.index.GameEntries
import fr.tikione.c2e.model.index.GameEntry
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class IndexReaderServiceImpl : IndexReaderService {

    private val csvDelimiterRegex = ",".toRegex()

    @Throws(IOException::class)
    override fun read(file: File): GameEntries {
        val gameEntries = GameEntries()
        if (!file.exists()) {
            return gameEntries
        }
        val lines = FileUtils.readLines(file, StandardCharsets.ISO_8859_1.name())
        if (lines.size < 2) {
            return gameEntries
        }
        lines.removeAt(0) // header is useless here
        lines.filter { line -> line.count { c -> c == ',' } >= 11 }.forEach { line ->
            val tokens = line.split(csvDelimiterRegex)
            val ge = GameEntry()
            ge.title = tokens[0]
            ge.magNumber = tokens[1]
            ge.authorAndDate = tokens[2]
            ge.gameConfig = tokens[3]
            ge.gameDDL = tokens[4]
            ge.gameDRM = tokens[5]
            ge.gameDev = tokens[6]
            ge.gameEditor = tokens[7]
            ge.gameLang = tokens[8]
            ge.gamePlatform = tokens[9]
            ge.gameScore = tokens[10]
            ge.gameTester = tokens[11]
            gameEntries.games.add(ge)
            if (!gameEntries.magNumbers.contains(ge.magNumber)) {
                gameEntries.magNumbers.add(ge.magNumber)
            }
        }
        return gameEntries
    }
}
