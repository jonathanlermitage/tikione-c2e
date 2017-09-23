package fr.tikione.c2e

import android.content.res.AssetManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import fr.tikione.c2e.service.html.HtmlWriterService
import fr.tikione.c2e.service.html.HtmlWriterServiceImpl
import fr.tikione.c2e.service.web.CPCAuthService
import fr.tikione.c2e.service.web.CPCAuthServiceImpl
import fr.tikione.c2e.service.web.scrap.CPCReaderService
import fr.tikione.c2e.service.web.scrap.CPCReaderServiceImpl
import fr.tikione.c2e.service.web.scrap.CPCScraperService
import fr.tikione.c2e.service.web.scrap.CPCScraperServiceImpl

val kodein = Kodein {
    bind<CPCAuthService>() with singleton { CPCAuthServiceImpl() }
    bind<CPCReaderService>() with singleton { CPCReaderServiceImpl() }
    bind<CPCScraperService>() with singleton { CPCScraperServiceImpl() }
    bind<HtmlWriterService>() with singleton { HtmlWriterServiceImpl(AssetManager()) }
}
