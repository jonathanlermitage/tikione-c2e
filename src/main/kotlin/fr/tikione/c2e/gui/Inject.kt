package fr.tikione.c2e.gui

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import fr.tikione.c2e.gui.service.GuiLauncherService
import fr.tikione.c2e.gui.service.GuiLauncherServiceImpl

val kodein = Kodein {
    bind<GuiLauncherService>() with singleton { GuiLauncherServiceImpl() }
}
