package fr.tikione.c2e.service.web.scrap;

import fr.tikione.c2e.model.web.Auth;
import fr.tikione.c2e.model.web.Magazine;

import java.util.List;

/**
 * Tools to download CanardPC magazines.
 */
public interface CPCReaderService {
    
    /**
     * List downloadable magazines: archives and current.
     * @param auth authentication data.
     * @return web number and title.
     */
    List<Integer> listDownloadableMagazines(Auth auth);
    
    /**
     * Download a web.
     * @param auth authentication data.
     * @param number web number.
     * @return web.
     */
    Magazine downloadMagazine(Auth auth, int number);
}
