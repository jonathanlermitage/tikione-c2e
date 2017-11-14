package fr.tikione.c2e.cli

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import fr.tikione.c2e.cli.service.CliLauncherService
import fr.tikione.c2e.cli.service.CliLauncherServiceImpl

val kodein = Kodein {
    bind<CliLauncherService>() with singleton { CliLauncherServiceImpl() }
}
