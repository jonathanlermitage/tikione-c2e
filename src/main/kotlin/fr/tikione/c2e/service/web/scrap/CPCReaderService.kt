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
     * @return web number and title.
     */
    fun listDownloadableMagazines(auth: Auth): List<Int>

    /**
     * Download a web.
     * @param auth authentication data.
     * @param number web number.
     * @return web.
     */
    fun downloadMagazine(auth: Auth, number: Int): Magazine
}
