package fr.tikione.gui

import tornadofx.*

class Inputs {
    var username by property<String>()
    fun usernameProperty() = getProperty(Inputs::username)

    var password by property<String>()
    fun passwordProperty() = getProperty(Inputs::password)

    var magNumber by property<Int>()
    fun magNumberProperty() = getProperty(Inputs::magNumber)

    var includePictures by property<Boolean>()
    fun includePicturesProperty() = getProperty(Inputs::includePictures)
}
