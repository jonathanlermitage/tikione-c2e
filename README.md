*Ce projet est maintenant archivé !*  
*Si vous êtes utilisateur de ce logiciel, et souhaitez continuer à recevoir des mises à jour, n'hésitez pas à m'envoyer un petit mail : je continue le développement dans un cadre personnel, et le partager avec quelques amis ne devrait pas poser problème, pour peu que vous gardiez ce logiciel (et les magazines ainsi téléchargés) pour vous.*  
*A bientôt sur d'autres projets !*

Télécharge vos magazines [CanardPC](https://www.canardpc.com/) (abo numérique) dans un format optimisé pour une lecture hors-ligne sur PC, tablette et smartphone.  
Fonctionne sous Windows, MacOS, Linux, BSD.

## Téléchargement et utilisation en ligne de commande

Téléchargez [la dernière release](https://github.com/jonathanlermitage/tikione-c2e/releases) et décompressez-là dans un répertoire accessible en écriture. 

Trois versions packagées existent : 
* avec un JRE Windows 64bits et ImageMagick (`c2e-x.y.z-withWin64JRE-withImageMagick.zip`), **recommandé**.
* avec un JRE Windows 64bits (`c2e-x.y.z-withWin64JRE.zip`).
* et sans JRE (`c2e-x.y.z.zip`).

Le JRE est le Java Runtime Environment, un logiciel permettant d'exécuter TikiOne C2E. Dites-vous que le JRE est comparable au framework .NET (dot net), Python, AIR...  
ImageMagick est un logiciel de manipulation d'images. TikiOne C2E l'utilise pour redimensionner des images. Vous en aurez donc besoin uniquement si vous souhaitez redimentionnser les captures d'écran du magazine CanardPC.

### Windows

Placez-vous dans le répertoire de l'application et lancez une console (<kbd>Maj</kbd> + <kbd>clic droit</kbd>, <kbd>Ouvrir un invité de commande ici</kbd>). Tapez `c2e.cmd username password paramètres...` (les seuls paramètres obligatoires sont username et password, les autres sont optionnnels).

| Paramètre | Description |
| :-----: | :-----------
| `username` et `password` | votre identifiant et mot de passe à l'abonnement CanardPC numérique, ces paramètres sont obligatoires. |
| `-cpcXXX` | télécharger le numéro XXX, par exemple `-cpc348`. Répétez ce paramètre pour télécharger plusieurs numéros, par exemple `-cpc348 -cpc349 -cpc350 -cpc351`. |
| `-cpcall` | télécharger l'intégralité des numéros à votre disposition. |
| `-cpcmissing` | télécharger uniquement les numéros manquants. |
| `-nopic` | ne pas téléchanger les images (un numéro contient 60~200Mo d'images, et ~500Ko de texte). |
| `-list` | savoir quels numéros sont accessibles au téléchargement. |
| `-debug` | afficher le détail du téléchargement dans un format proche de JSON. |
| `-resizeXX` | redimensionner les images selon le ratio `XX` (ex: `-resize50` pour un ratio de 50%). Basé sur [ImageMagick](http://www.imagemagick.org), lequel doit être disponible dans le PATH ou packagé avec l'appli. Testé sous Windows uniquement, mais doit fonctionner partout où ImageMagick est disponible. |
| `-index` | génèrer un sommaire CSV (`CPC-index.csv`) et HTML (`CPC-index.html`) de tous les numéros disponibles au téléchargement, avec en détails la note, présence de DRM, poids au téléchargement, plateformes, etc. Attention, prévoir plusieurs dizaines de minutes pour ce traitement. Si le fichier `CPC-index.csv` existe déjà, il sera complété avec les numéros manquants. `CPC-index.html` est (re)créé à partir du `CPC-index.csv` existant et propose un moteur de recherche par titre de jeu et numéro de magazine. |
| `-proxy:address:port` | utiliser le proxy HTTP(S) définit par l'adresse `address` (nom de domaine ou adresse IP) et le port `port`. Cette option est généralement utile si vous vous connectez depuis le réseau d'un entreprise qui impose un proxy pour accéder au web. |
| `-sysproxy` | utiliser le proxy système. |
| `-dark` | activer par défaut le mode nuit. |
| `-fontsize:XXuu` | spécifier une taille de police de base, où `XX` est un nombre et `uu` une unité, par exemple `-fontsize:20px` ou `-fontsize:2em`. |
| `-up` | télécharger toute nouvelle version de l'application (version ZIP minimale, sans JRE ni ImageMagick) dans le répertoire courant. Son installation reste à la charge de l'utilisateur (dézipper l'archive téléchargée). |
| `-directory:mon_dossier` | créer les fichiers dans le dossier spécifié. |
| `-home` | génèrer une page d'accueil HTML `CPC-home.html` listant tous les magazines téléchargés (présents dans le répertoire courant), avec un lien pour ouvrir chaque numéro. L'idée est de mettre cette page en favoris dans votre navigateur, ainsi vous n'aurez pas à chercher le bon fichier HTML à chaque fois que vous voudrez consulter un numéro. |
| `-dysfont` | charger une police pensée pour améliorer le confort de lecture des personnes dyslexiques. |
| `-nocolumn` | afficher le texte des articles sur une seule colonne (si paramètre absent, affichage sur plusieurs colonnes en fonction de la taille de l'écran pour améliorer le confort de lecture). |
        
Exemples :

* télécharger le numéro 348, tapez `c2e.cmd username password -cpc348`.  
* télécharger le numéro 348 sans les images, tapez `c2e.cmd username password -cpc348 -nopic`.  
* télécharger le numéro 348 et réduire les images à 40% de leur taille originelle, tapez `c2e.cmd username password -cpc348 -resize40`.  
* télécharger plusieurs numéros à la fois, par exemple 348, 350 et 355, tapez `c2e.cmd username password -cpc348 -cpc350 -cpc355`.  
* télécharger l'intégralité des numéros disponibles, tapez `c2e.cmd username password -cpcall`.
* télécharger les numéros manquants, tapez `c2e.cmd username password -cpcmissing`.  
* générer le sommaire de l'intégralité des numéros disponibles, tapez `c2e.cmd username password -index`.
* télécharger le numéro 348 au travers du proxy HTTP(S) companygateway sur le port 3128, tapez `c2e.cmd username password -cpc348 -proxy:companygateway:3218`.  
* télécharger le numéro 348 au travers du proxy système, tapez `c2e.cmd username password -cpc348 -sysproxy`.  
    
Le fichier est généré (ou écrasé) dans le répertoire courant (là où est le programme) et porte le nom `CPCxxx-opts.html` où `xxx` est le numéro et `-opts` rappelle certains paramètres (`-nopic`, `-resize`), par exemple `CPC348-nopic.html`.

La police de caractères par défaut est `RobotoSlab-Light` (celle utilisée sur le site CanardPC). Pour utiliser une autre police, déposez un fichier TTF (par exemple `Arial.ttf`) dans le répertoire de application (à côté de `c2e.cmd` et `c2e.sh`) : elle sera automatiquement utilisée.

### MacOS, Linux, BSD

Téléchargez et décompressez la version packagée `c2e-x.y.z.zip`. Comme Windows, mais remplacez `c2e.cmd` par `./c2e.sh`.  
Java 8 doit être installé et accessible depuis le PATH (avec un Ubuntu récent, tapez `sudo apt-get install default-jre`). Aussi, `c2e.sh` doit être rendu exécutable : tapez `chmod +x c2e.sh`.  
Ce script est testé sous Ubuntu 16.04 LTS et devrait fonctionner sur la majorité des distributions Linux.

## Avancement

Voir le [changelog](https://github.com/jonathanlermitage/tikione-c2e/blob/master/CHANGELOG.md) pour l'avancée des travaux.

## Contributeurs

* [guame](https://github.com/guame)
* [willoucom](https://github.com/willoucom)

Merci !

## Licence

Licence MIT. En d'autres termes, ce logiciel est libre de droits et gratuit, vous pouvez en faire ce que vous voulez.

## Outils

Je développe TikiOne C2E grâce à ces logiciels :

|Kotlin|JetBrains IntelliJ IDEA|
|:--|:--|
|[![IntelliJ](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_kotlin.png)](https://kotlinlang.org/)|[![IntelliJ](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_intellij.png)](https://www.jetbrains.com/idea/)|


|Gradle|
|:--|
|[![Gradle](https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/misc/logo_gradle.png)](https://gradle.org)|
