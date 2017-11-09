# TikiOne C2E

Télécharge vos magazines [CanardPC](https://www.canardpc.com/) (abo numérique) pour une lecture hors-ligne sur PC, tablette et smartphone.  
Fonctionne sous Windows, MacOS, Linux, BSD.

Deux branches sont développées :

* [master](https://github.com/jonathanlermitage/tikione-c2e) : le programme pour PC, stable.
* [android](https://github.com/jonathanlermitage/tikione-c2e/tree/android) : l'application pour Android 5 et supérieur, en cours de dev.

## Téléchargement et utilisation en ligne de commande

Téléchargez [la dernière release](https://github.com/jonathanlermitage/tikione-c2e/releases) et décompressez-là dans un répertoire accessible en écriture. 

Trois versions packagées existent : 
* avec un JRE Windows 64bits et ImageMagick (`c2e-x.y.z-withWin64JRE-withImageMagick.zip`), **recommandé**.
* avec un JRE Windows 64bits (`c2e-x.y.z-withWin64JRE.zip`).
* et sans JRE (`c2e-x.y.z.zip`).

### Windows

* placez-vous dans le répertoire de l'application et lancez une console (Maj + clic droit, "Ouvrir un invité de commande ici"). Tapez `c2e.cmd username password -cpcXXX -nopic -list -debug -resizeXX` (les seuls paramètres obligatoires sont username et password, les autres sont optionnnels).
  * `username` et `password` sont votre identifiant et mot de passe à l'abonnement CanardPC numérique, ces paramètres sont obligatoires.
  * `-cpcXXX` télécharger le numéro XXX, par exemple `-cpc348`.  *(depuis la version 1.1.0)* Télécharger plusieurs numéros, par exemple `-cpc348 -cpc349 -cpc350 -cpc351`. Vous pouvez aussi utiliser `-cpcall` pour télécharger l'intégralité des numéros à votre disposition.
  * `-nopic` ne pas téléchanger les images (un numéro contient 60~200Mo d'images, et ~500Ko de texte).
  * `-list` savoir quels numéros sont accessibles au téléchargement.
  * `-debug` affiche le détail du téléchargement dans un format proche de JSON.
  * `-resizeXX` redimensionne les images selon le ratio `XX` (ex: `-resize50` pour un ratio de 50%). Basé sur [ImageMagick](http://www.imagemagick.org), lequel doit être disponible dans le PATH ou packagé avec l'appli. Testé sous Windows uniquement, mais doit fonctionner partout où ImageMagick est disponible.
  * `-index` génère un sommaire CSV (`CPC-index.csv`) de tous les numéros disponibles au téléchargement, avec en détails la note, présence de DRM, poids au téléchargement, plateformes, etc. Attention, prévoir plusieurs dizaines de minutes pour ce traitement. Si le fichier `CPC-index.csv` existe déjà, il sera complété avec les numéros manquants.
  * `-proxy=http|https:address:port` utilise le proxy HTTP ou HTTPS définit par l'adresse `address` (nom de domaine ou adresse IP) et le port `port`. Cette option est généralement utile si vous vous connectez depuis le réseau d'un entreprise qui impose un proxy pour accéder au web.
  * `-sysproxy` utilise le proxy système.
  
*Attention, le paramètre `-html` est supprimé depuis la version 1.3.3. Pour les versions précédentes, n'oubliez pas de préciser `-html` pour générer le fichier.* 
    
Exemples :
      
* Pour télécharger le numéro 348 sans les images, tapez `c2e.cmd username password -cpc348 -nopic`.  
* Pour télécharger le numéro 348 avec les images, tapez `c2e.cmd username password -cpc348`.  
* Pour télécharger le numéro 348 avec les images et réduire celles-ci de 50%, tapez `c2e.cmd username password -cpc348 -resize50`.  
* Pour télécharger plusieurs numéros à la fois, par exemple 348, 350 et 355, tapez `c2e.cmd username password -cpc348 -cpc350 -cpc355`.  
* Pour télécharger l'intégralité des numéros disponibles, tapez `c2e.cmd username password -cpcall`.
* Pour générer le sommaire de l'intégralité des numéros disponibles, tapez `c2e.cmd username password -index`.
* Pour télécharger le numéro 348 au travers du proxy HTTP companygateway:3128, tapez `c2e.cmd username password -cpc348 -proxy=http:companygateway:3218`.  
* Pour télécharger le numéro 348 au travers du proxy HTTPS companygateway:3128, tapez `c2e.cmd username password -cpc348 -proxy=https:companygateway:3218`.  
* Pour télécharger le numéro 348 au travers du proxy système, tapez `c2e.cmd username password -cpc348 -sysproxy`.  
    
Le fichier est généré (ou écrasé) dans le répertoire courant (là où est le programme) et porte le nom `CPCxxx-opts.html` où `xxx` est le numéro et `-opts` rappelle certains paramètres (`-nopic`, `-resize`), par exemple `CPC348-nopic.html`.

### MacOS, Linux, BSD

Téléchargez et décompressez la version packagée `c2e-x.y.z.zip`. Comme Windows, mais remplacez `c2e.cmd` par `./c2e.sh`.  
Java 8 doit être installé et accessible depuis le PATH (avec un Ubuntu récent, tapez `sudo apt-get install default-jre`). Aussi, `c2e.sh` doit être rendu exécutable : tapez `chmod +x c2e.sh`.  
Ce script est testé sous Ubuntu 16.04 LTS et devrait fonctionner sur la majorité des distributions Linux.

## Compilation

Il sagit d'un projet Kotlin (Java 8 jusqu'à la v1.2.2, Kotlin ensuite) construit avec Gradle. Installez un JDK8 et Gradle 3+, puis lancez `gradle jar` pour construire un applicatif dans le répertoire `build/libs` (ou `gradlew jar` pour utiliser le wrapper Gradle 4, conseillé).

## Avancement

Voir le [changelog](https://github.com/jonathanlermitage/tikione-c2e/blob/master/CHANGELOG.md) pour l'avancée des travaux.

## Contributeurs

* [guame](https://github.com/guame)

Merci !

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

|Gradle|
|:--|
|[![Gradle](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_gradle.png)](https://gradle.org)|

|Oracle JDK|
|:--|
|[![JDK](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_java.png)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)|

|meow ?|
|:--|
|![cats](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/cats.gif)|

