package fr.tikione.c2e

import com.github.salomonbrys.kodein.instance
import compat.Tools
import fr.tikione.c2e.cli.service.CliLauncherService
import fr.tikione.c2e.gui.service.GuiLauncherService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import fr.tikione.c2e.cli.kodein as cli
import fr.tikione.c2e.gui.kodein as gui

object Main {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        log.info("TikiOne C2E version {}, Java {}, {} {} par {}, sur {} avec le codage {}",
                Tools.VERSION,
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.vm.vendor"),
                System.getProperty("os.name"),
                System.getProperty("file.encoding"))
        startCLI(args) // TODO start GUI by default, add a parameter to use CLI
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun startCLI(args: Array<String>) {
        val cli: CliLauncherService = cli.instance()
        cli.start(args)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun startGUI(args: Array<String>) {
        val gui: GuiLauncherService = gui.instance()
        gui.start(args)
    }
}
