package fr.tikione.c2e

import com.github.salomonbrys.kodein.instance
import compat.Tools
import fr.tikione.c2e.cli.kodein
import fr.tikione.c2e.cli.service.CliLauncherService
import fr.tikione.c2e.gui.kodein
import fr.tikione.c2e.gui.service.GuiLauncherService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

object MainGui {
    
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        log.info("TikiOne C2E version {}, Java {}, {} {} by {}, on {} with {} file encoding",
//                Tools.VERSION,
//                System.getProperty("java.version"),
//                System.getProperty("java.vm.name"),
//                System.getProperty("java.vm.version"),
//                System.getProperty("java.vm.vendor"),
//                System.getProperty("file.encoding"), )
//        System.getProperty("os.name")
         
        startGUI(args)     
    }

    

    @Throws(IOException::class, InterruptedException::class)
    private fun startGUI(args: Array<String>) {
        val gui: GuiLauncherService = fr.tikione.c2e.gui.kodein.instance()
        gui.start(args)
    }
}