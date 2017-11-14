package fr.tikione.c2e.gui

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

/**
 * @author GUAM
 * 14.11.2017 - 13:44
 */
class CPCScrapperGui : Application() {

    private val root = BorderPane()
    
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        
        primaryStage.setOnCloseRequest({
            Platform.exit()
            System.exit(0)
        })

        primaryStage.scene = Scene(root)

        initUIPanels(primaryStage)
        
        primaryStage.sizeToScene()
        
        primaryStage.show()
    }

    private fun initUIPanels(stage: Stage) {
        stage.getIcons().add(Image(loadRessource("gui/troppuissant.png")))
        stage.title = "TikiOne C2E"
        root.resize(800.0, 600.0)
        root.center = Label("hello CPC !")
    }

    private fun loadRessource(path: String): String? {
        return Thread.currentThread().contextClassLoader.getResource(path).toExternalForm()
    }

}