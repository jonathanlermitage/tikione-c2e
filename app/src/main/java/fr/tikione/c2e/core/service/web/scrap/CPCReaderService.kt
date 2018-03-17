package fr.tikione.c2e.core.service.web.scrap

import fr.tikione.c2e.core.model.web.Auth
import fr.tikione.c2e.core.model.web.AuthorPicture
import fr.tikione.c2e.core.model.web.Magazine
import org.jsoup.nodes.Document

/**
 * Tools to download CanardPC magazines.
 */
interface CPCReaderService {
    var downloadStatus : Float
    var cancelDl: Boolean

    /**
     * List downloadable magazines: archives and current.
     * @param auth authentication data.
     * @return magazines number and title.
     */
    fun listDownloadableMagazines(auth: Auth): ArrayList<String>

    /**
     * Download a magazine.
     * @param auth authentication data.
     * @param number magazine number.
     * @return magazine.
     */
    fun downloadMagazine(auth: Auth, number: String): Magazine

    fun extractAuthorsPicture(doc: Document): Map<String, AuthorPicture>
}
