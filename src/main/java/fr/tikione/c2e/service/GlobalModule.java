package fr.tikione.c2e.service;

import com.google.inject.AbstractModule;
import fr.tikione.c2e.service.epub.EpubWriterService;
import fr.tikione.c2e.service.epub.EpubWriterServiceImpl;
import fr.tikione.c2e.service.html.HtmlWriterService;
import fr.tikione.c2e.service.html.HtmlWriterServiceImpl;
import fr.tikione.c2e.service.pdf.PdfWriterService;
import fr.tikione.c2e.service.pdf.PdfWriterServiceImpl;
import fr.tikione.c2e.service.web.CPCAuthService;
import fr.tikione.c2e.service.web.CPCAuthServiceImpl;
import fr.tikione.c2e.service.web.scrap.CPC348ScraperService;
import fr.tikione.c2e.service.web.scrap.CPC348ScraperServiceImpl;
import fr.tikione.c2e.service.web.scrap.CPCReaderService;
import fr.tikione.c2e.service.web.scrap.CPCReaderServiceImpl;

public class GlobalModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(CPCReaderService.class).to(CPCReaderServiceImpl.class);
        bind(CPCAuthService.class).to(CPCAuthServiceImpl.class);
        bind(CPC348ScraperService.class).to(CPC348ScraperServiceImpl.class);
        bind(EpubWriterService.class).to(EpubWriterServiceImpl.class);
        bind(PdfWriterService.class).to(PdfWriterServiceImpl.class);
        bind(HtmlWriterService.class).to(HtmlWriterServiceImpl.class);
    }
}
