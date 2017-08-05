# TikiOne C2E

Télécharge vos magazines [CanardPC](https://www.canardpc.com/) (abo numérique) pour une lecture hors-ligne sur PC, tablette et smartphone.  
Fonctionne sous Windows, MacOS, Linux, BSD.

## Téléchargement et utilisation en ligne de commande

Téléchargez [la dernière release](https://github.com/jonathanlermitage/tikione-c2e/releases) et décompressez-là dans un répertoire accessible en écriture. La version ``c2e-x.y.z.zip`` (où x.y.z correspond à un numéro de version, par exemple 1.0.0) est multiplateforme, tandis que la ``c2e-x.y.z-withWin64JRE.zip`` contient un JRE (Java 8) propre à Windows. C2E est un programme nécessitant Java 8 ou supérieur.

### Windows

* placez-vous dans le répertoire de l'application et lancez une console (Maj + clic droit, "Ouvrir un invité de commande ici"). Tapez ``c2e.cmd username password [-cpcXXX -cpcYYY -cpcZZZ...|-cpcall] [-html] [-nopic] [-compresspic] [-list] [-debug]`` (les paramètres entre ``[]`` sont optionnels et peuvent être placés dans n'importe quel ordre)
  * ``username`` et ``password`` sont votre identifiant et mot de passe à l'abonnement CanardPC numérique, ces paramètres sont obligatoires
  * ``-cpcXXX`` télécharger le numéro XXX, par exemple ``-cpc348``.  *(depuis la v1.1.0)* Télécharger plusieurs numéros, par exemple ``-cpc348 -cpc349 -cpc350 -cpc351``. Vous pouvez aussi utiliser ``-cpcall`` pour télécharger l'intégralité des numéros à votre disposition
  * ``-html``format de sortie HTML riche
  * ``-nopic`` ne pas téléchanger les images (un numéro contient 60~200Mo d'images, et ~500Ko de texte)
  * ``-compresspic`` *(depuis la v1.1.0)* compresser les images lorsque c'est possible, afin de gagner quelques Mo sur le fichier final
  * ``-list`` savoir quels numéros sont accessibles au téléchargement. 
  * ``-debug`` affiche le détail du téléchargement dans un format proche de JSON
  
Le fichier est généré (ou écrasé) dans le répertoire courant et porte le nom ``CPCxxx-opts.ext`` où ``xxx`` est le numéro, ``ext`` l'extension voulue et ``-opts`` rappelle certains paramètres (``-nopic``, ``-compresspic``), par exemple ``CPC348-nopic.html``.

Deux versions packagées existent : avec un JRE Windows 64bits (``c2e-x.y.z-withWin64JRE.zip``), et sans JRE (``c2e-x.y.z.zip``).

### MacOS, Linux, BSD

Téléchargez et décompressez la version packagée ``c2e-x.y.z.zip``. Comme Windows, mais remplacez ``c2e.bat`` par ``java -jar -Xms32m -Xmx512m -Dfile.encoding=UTF-8 "c2e.jar"``. Java 8 doit être installé et accessible depuis le PATH (avec un Ubuntu récent, tapez ``sudo apt-get install default-jre``).

## Compilation

Il sagit d'un projet Java 8 construit avec Maven 3.3.9. Installez un JDK8 et Maven 3.3.9+, puis lancez ``mvn clean package`` pour construire un applicatif dans le répertoire ``dist``.  
Si vous disposez d'une ancienne version de Maven, vous pouvez utiliser celle embarquée dans le projet : pour cela, ne lancez pas ``mvn`` mais ``mvnw``, par exemple ``mvnw clean package``.

## Avancement

Voir le [changelog](https://github.com/jonathanlermitage/tikione-c2e/blob/master/CHANGELOG.md) pour l'avancée des travaux.

## Motivation

Lors du Kickstarter ayant financé la version numérique de CanardPC, une compatibilité Pocket avait été annoncée - Pocket permettant de télécharger une page web pour la consulter hors ligne. Des raisons techniques empêchent aujourd'hui CanardPC de respecter cette promesse.  
TikiOne C2E a pour objectif de contenter les canards laisés, en leur permettant de télécharger leurs magazines dans divers formats pour une lecture hors-ligne.  

## A savoir

* L'export d'un numéro peut mal fonctionner et certains articles être vides : recommencez simplement l'export, cela devrait fonctionner.
* Le programme se connecte avec votre compte CanardPC. Le site détecte cette connexion et vous force à vous authentifier à nouveau lorsque vous revennez via votre navigateur web. C'est le comportement normal du site (sans doute pour éviter le partage de compte), ne soyez donc pas surpris.
* les exports PDF et EPUB sont annulés car leur niveau d'intégration est loin d'égaler celui d'un HTML dit "responsive" (adapté au PC, tablettes et smartphones). Ce sont aussi des formats favorisant le piratage du magazine, phénomène que je souhaite minimiser autant que possible.

## Licence

Licence MIT. En d'autres termes, ce logiciel est libre de droits et gratuit, vous pouvez en faire ce que vous voulez.

## Outils

Je développe TikiOne C2E grâce à ces logiciels :

|Kotlin|
|:--|
|[![IntelliJ](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_kotlin.png)](https://kotlinlang.org/)|

|JetBrains IntelliJ IDEA|
|:--|
|[![IntelliJ](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_intellij.png)](https://www.jetbrains.com/idea/)|

|Apache Maven|
|:--|
|[![Maven](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_maven.png)](https://maven.apache.org)|

|Oracle JDK|
|:--|
|[![JDK](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_java.png)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)|

