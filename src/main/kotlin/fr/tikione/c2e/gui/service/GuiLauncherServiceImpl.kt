package fr.tikione.c2e.gui.service

import fr.tikione.c2e.gui.CPCScrapperGui
import javafx.application.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GuiLauncherServiceImpl : GuiLauncherService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun start(args: Array<String>) {
        Application.launch(CPCScrapperGui::class.java, *args)
    }
}
