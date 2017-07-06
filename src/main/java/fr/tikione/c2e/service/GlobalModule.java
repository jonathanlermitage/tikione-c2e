package fr.tikione.c2e.service;

import com.google.inject.AbstractModule;
import fr.tikione.c2e.service.html.HtmlWriterService;
import fr.tikione.c2e.service.html.HtmlWriterServiceImpl;
import fr.tikione.c2e.service.web.CPCAuthService;
import fr.tikione.c2e.service.web.CPCAuthServiceImpl;
import fr.tikione.c2e.service.web.scrap.CPCReaderService;
import fr.tikione.c2e.service.web.scrap.CPCReaderServiceImpl;
import fr.tikione.c2e.service.web.scrap.CPCScraperService;
import fr.tikione.c2e.service.web.scrap.CPCScraperServiceImpl;

public class GlobalModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(CPCReaderService.class).to(CPCReaderServiceImpl.class);
        bind(CPCAuthService.class).to(CPCAuthServiceImpl.class);
        bind(CPCScraperService.class).to(CPCScraperServiceImpl.class);
        bind(HtmlWriterService.class).to(HtmlWriterServiceImpl.class);
    }
}
