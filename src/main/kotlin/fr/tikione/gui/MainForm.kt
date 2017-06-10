package fr.tikione.gui

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.CLOUD_DOWNLOAD
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.LIST
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Orientation
import tornadofx.*

class MainForm : View() {
    override val root = Form()
    val inputs = Inputs()

    init {
        title = "TikiOne C2E, vos mags CanardPC offline"

        with(root) {
            fieldset("1/2 Connexion à l'abo numérique CanardPC") {
                field("Identifiant") {
                    textfield().bind(this@MainForm.inputs.usernameProperty())
                }
                field("Mot de passe") {
                    passwordfield().bind(this@MainForm.inputs.passwordProperty())
                }
            }
            fieldset("2/2 Que voulez-vous faire ?") {
                disableProperty().bind(inputs.usernameProperty().isNull.or(inputs.passwordProperty().isNull))
                fieldset {
                    button("Lister les numéros téléchargeables", FontAwesomeIconView(LIST)) {
                        /*setOnAction {
                            Notifications.create()
                                    .title("Erreur")
                                    .text("Ientifiant ou mot de passe invalide")
                                    .owner(this)
                                    .showError()
                        }*/
                    }
                }
                separator(Orientation.HORIZONTAL)
                fieldset {
                    label("Télécharger un numéro au format HTML pour une lecture hors-ligne")
                    field("Numéro") {
                        textfield().bind(this@MainForm.inputs.magNumberProperty())
                        togglebutton("Inclure les images")
                    }
                    button("Télécharger", FontAwesomeIconView(CLOUD_DOWNLOAD)) {

                    }
                }
            }
        }
    }
}
