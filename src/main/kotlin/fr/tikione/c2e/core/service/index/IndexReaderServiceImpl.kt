package fr.tikione.c2e.core.service.index

import fr.tikione.c2e.core.model.index.GameEntries
import fr.tikione.c2e.core.model.index.GameEntry
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
        var lines = file.readLines(StandardCharsets.ISO_8859_1)
        if (lines.size < 2) {
            return gameEntries
        }
        lines = lines.drop(1) // header is useless here
        lines.filter { line -> line.count { c -> c == ',' } >= 11 }.forEach { line ->
            val tokens = line.split(csvDelimiterRegex).iterator()
            val ge = GameEntry()
            ge.title = tokens.next()
            ge.magNumber = tokens.next()
            ge.author = tokens.next()
            ge.date = tokens.next()
            ge.gameConfig = tokens.next()
            ge.gameDDL = tokens.next()
            ge.gameDRM = tokens.next()
            ge.gameDev = tokens.next()
            ge.gameEditor = tokens.next()
            ge.gameLang = tokens.next()
            ge.gamePlatform = tokens.next()
            ge.gameScore = tokens.next()
            ge.gameTester = tokens.next()
            gameEntries.games.add(ge)
            if (!gameEntries.magNumbers.contains(ge.magNumber)) {
                gameEntries.magNumbers.add(ge.magNumber)
            }
        }
        gameEntries.magNumbers.sortDescending()
        return gameEntries
    }
}
