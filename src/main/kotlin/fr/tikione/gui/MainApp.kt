package fr.tikione.gui

import tornadofx.*

class MainApp : App() {
    override val primaryView = MainForm::class

    init {
        importStylesheet(Styles::class)
    }
}
