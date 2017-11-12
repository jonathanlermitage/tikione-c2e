package fr.tikione.c2e.gui.service

import java.io.IOException

interface GuiLauncherService {

    @Throws(IOException::class, InterruptedException::class)
    fun start(args: Array<String>)
}
