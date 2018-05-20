package fr.tikione.c2e.core

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import compat.AssetService
import compat.AssetServiceImpl
import fr.tikione.c2e.core.cfg.Cfg
import fr.tikione.c2e.core.service.home.LocalReaderService
import fr.tikione.c2e.core.service.home.LocalReaderServiceImpl
import fr.tikione.c2e.core.service.html.HtmlWriterService
import fr.tikione.c2e.core.service.html.HtmlWriterServiceImpl
import fr.tikione.c2e.core.service.index.IndexReaderService
import fr.tikione.c2e.core.service.index.IndexReaderServiceImpl
import fr.tikione.c2e.core.service.index.IndexWriterService
import fr.tikione.c2e.core.service.index.IndexWriterServiceImpl
import fr.tikione.c2e.core.service.web.CPCAuthService
import fr.tikione.c2e.core.service.web.CPCAuthServiceImpl
import fr.tikione.c2e.core.service.web.scrap.CPCReaderService
import fr.tikione.c2e.core.service.web.scrap.CPCReaderServiceImpl
import fr.tikione.c2e.core.service.web.scrap.CPCScraperService
import fr.tikione.c2e.core.service.web.scrap.CPCScraperServiceImpl

val coreKodein = Kodein {
    bind<AssetService>() with singleton { AssetServiceImpl(null) }
    bind<CPCAuthService>() with singleton { CPCAuthServiceImpl() }
    bind<CPCReaderService>() with singleton { CPCReaderServiceImpl() }
    bind<CPCScraperService>() with singleton { CPCScraperServiceImpl() }
    bind<HtmlWriterService>() with singleton { HtmlWriterServiceImpl() }
    bind<IndexWriterService>() with singleton { IndexWriterServiceImpl() }
    bind<IndexReaderService>() with singleton { IndexReaderServiceImpl() }
    bind<LocalReaderService>() with singleton { LocalReaderServiceImpl() }
    bind<Cfg>() with singleton { Cfg() }
}
