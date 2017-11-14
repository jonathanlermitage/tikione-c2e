package fr.tikione.c2e.cli.service

import java.io.IOException

interface CliLauncherService {

    @Throws(IOException::class, InterruptedException::class)
    fun start(args: Array<String>)
}
