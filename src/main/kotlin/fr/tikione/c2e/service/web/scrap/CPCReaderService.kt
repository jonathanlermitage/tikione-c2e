package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Auth
import fr.tikione.c2e.model.web.Magazine

/**
 * Tools to download CanardPC magazines.
 */
interface CPCReaderService {

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
}
