## TikiOne C2E Change Log

### 1.5.0 (dev en cours)

* **(en cours)** paramètres d'application gérés via [Picocli](http://picocli.info/#_kotlin).
* **(en cours)** ajout d'un module de recherche et de tri dans le sommaire HTML (pour chercher des noms de jeux, trier par numéro, etc).

### 1.4.0 (dev en cours)

*On revient à une numérotation des versions plus [classique](https://semver.org/lang/fr/) : majeure, mineure, patch.*

* la création du sommaire CSV (via `-index`) crée maintenant une version HTML `CPC-index.html` en plus de la version CSV `CPC-index.csv`.
* amélioration de l'affichage des chemins de fichiers lorsque le chemin contient `/./` ou `/../`.
* interne : refactoring, centralisation des paramètres d'application dans un singleton.

### 1.3.16 (2018/05/19)

* affiche le texte des articles sur plusieurs colonnes pour améliorer le confort de lecture. Peut être désactivé avec le paramètre `-nocolumn`.
* ajout du paramètre `-dysfont` pour charger une police pensée pour améliorer le confort de lecture des personnes dyslexiques.
* lien de donation PayPal mis à jour: le don se fait maintenant en Euro plutôt qu'en Dollar, et ne passe plus par une redirection Sourceforge.net.

### 1.3.15 (2018/04/20)

* colorisation de la page d'accueil HTML `CPC-home.html`: chaque numéro a une couleur différente.

### 1.3.14 (2018/02/24)

* ajout du paramètre `-home` pour générer une page d'accueil HTML `CPC-home.html` listant tous les magazines téléchargés (présents dans le répertoire courant), avec un lien pour ouvrir chaque numéro. L'idée est de mettre cette page en favoris dans votre navigateur, ainsi vous n'aurez pas à chercher le bon fichier HTML à chaque fois que vous voudrez consulter un numéro.

### 1.3.13 (2018/02/11)

* le script de lancement Windows n'est plus limité à 9 paramètres.
* ajout du paramètre`-directory:mon_dossier` pour créer les fichiers dans le dossier spécifié.

### 1.3.12 (2018/02/02)

* [#21](https://github.com/jonathanlermitage/tikione-c2e/issues/21) ajout du paramètre `-fontsize:XXuu` pour spécifier une taille de police de base, où `XX` est un nombre et `uu` une unité, par exemple `-fontsize:20px` ou `-fontsize:2em`.
* [#27](https://github.com/jonathanlermitage/tikione-c2e/issues/27) amélioration de la récupération des numéros, et correction pour le numéro 374.

### 1.3.11 (2017/12/12)

* [#12](https://github.com/jonathanlermitage/tikione-c2e/issues/12) ajout d'un guide utilisateur `LISEZMOI.html`
* [#14](https://github.com/jonathanlermitage/tikione-c2e/issues/14) ajout du paramètre `-up`: si une nouvelle version de l'application est disponible, télécharge la version ZIP minimale (sans JRE ni ImageMagick) dans le répertoire courant. Son installation reste à la charge de l'utilisateur (dézipper l'archive téléchargée).
* [#16](https://github.com/jonathanlermitage/tikione-c2e/issues/16) la première lettre de chaque article est décorée à la manière du magazine en ligne.
* interne : refactoring en vue de simplifier le développement de l'application Android.
* interne : les licences tierces sont packagées avec l'application dans le dossier `3rdparty-licenses`. 
* interne : `build.gradle`, téléchargement automatique de la javadoc et des sources des dépendances sous IntelliJ.
* interne : ajout d'un script pour packager chaque release Windows (`make-windows.cmd`). Les versions de l'application proposées au téléchargement sont créées avec ce script.

### 1.3.10 (2017/11/29)

* [#13](https://github.com/jonathanlermitage/tikione-c2e/issues/13) un échec d'authentification arrête maintenant le programme au lieu de télécharger des magazines vides.
* [#24](https://github.com/jonathanlermitage/tikione-c2e/issues/24) intégration de la jaquette dans l'édito.
* ajout d'un guide de contribution dans le `readme.md`.
* correction du paramètre `-dark` : le mode nuit n'était pas activé sur l'intégralité du magazine.

### 1.3.9 (2017/11/20)

* [#8](https://github.com/jonathanlermitage/tikione-c2e/issues/8) ajout du paramètre `-cpcmissing` en alternative à `-cpcall` : télécharge uniquement les numéros manquants.
* [#9](https://github.com/jonathanlermitage/tikione-c2e/issues/9) si un fichier TTF (`.ttf`) est présent dans le répertoire de l'application, il sera utilisé à la place de la police embarquée `RobotoSlab-Light`. Si plusieurs fichiers TTF sont présents, le premier est utilisé (classement par ordre alphabétique).
* [#10](https://github.com/jonathanlermitage/tikione-c2e/issues/10) ajout du paramètre `-dark` : active par défaut le mode nuit.

### 1.3.8 (2017/11/17)

* amélioration du sommaire CSV : distinction de l'auteur et de la date de chaque test.
* correction d'un code HTML invalide dans le titre des articles.
* ajout des paramètres `-proxy:address:port` et `-sysproxy` pour définir un proxy. Exemple pour utiliser le proxy HTTP(S) "companygateway" sur le port 3128 : `-proxy:companygateway:3128`. Pour le proxy système : `-sysproxy`.
* interne : la version de l'application est désormais définie dans la configuration Gradle et non plus en dur dans le code Kotlin.
* interne : refactoring en vue de simplifier le développement d'une interface graphique et l'application Android.
* interne : stabilisation du build Gradle.

### 1.3.7 (2017/11/03)

* intégration de l'édito lors du téléchargement d'un numéro.
* la création du sommaire CSV est désormais incrémentale : seuls les nouveaux numéros sont ajoutés au sommaire existant.
* ajout du script de démarrage Linux. Testé sous Ubuntu 16.04 LTS.

### 1.3.6 (2017/10/25)

* ajout du paramètre `-index` : génère un sommaire CSV (codage ISO 8859-1, séparateur : virgule) de tous les numéros disponibles au téléchargement. Ce sommaire permet de lister l'intégralité des jeux testés, previews, etc, et quelques données associées comme la note, date de publication, détails technique.
* la console affiche des messages moins verbeux : ne garde que l'horodatage, niveau de log et message. Les informations détaillées (package, classe, ligne) restent visibles dans les logs fichier.
* interne : refactoring et nettoyage de code.

### 1.3.5 (2017/10/20)

* correction bug [GitHub #2](https://github.com/jonathanlermitage/tikione-c2e/issues/2) : le paramètre `-cpcall` ne téléchargeait plus aucun magazine.
* correction du listing des numéros disponibles : le numéro du dernier magazine était mal calculé.
* suppression des caractères accentués dans la console et les logs.
* interne : introduction de l'injecteur de dépendance [Kodein](https://github.com/SalomonBrys/Kodein).
* interne : projet migré de [Maven](https://maven.apache.org) vers [Gradle](https://gradle.org).
* la version d'[ImageMagick](http://www.imagemagick.org) est allégée pour ne garder que les composants essentiels à la conversion d'images (~18MB contre ~200MB auparavant).

### 1.3.4 (2017/09/28)

* correction de la vérification de l'authentification (suite aux récentes modifications du site).

### 1.3.3 (2017/09/21)

* suppression du paramètre `-html`, devient implicite lorsque `-cpcXXX` est utilisé.
* le paramètre `-cpcXXX` est maintenant plus souple et permet la sélection du magazine hors-série 22 (`hs22`, tapez donc `-cpchs22`).
* le paramètre `-list` est lui aussi plus souple et intègre désormais les magazines hors-série dans la liste des numéros disponibles. 
* clarifications sur l'usage de la ligne de commande (voir `README.md`).
* clarification des messages d'avertissement.

### 1.3.2 (2017/09/06)

* réduction de l'usage des fichiers temporaires.
* réduction de la verbosité des logs.
* interne : amélioration du process de build (nom de l'archive et copie des ressources utiles).
* interne : correction du niveau de bytecode ciblé (préférer la stdlib jre6 au lieu de jre8). 
* interne : amélioration de la compatibilité Android.

### 1.3.1 (2017/09/02)

* introduction du paramètre `-resizeXX` pour redimensionner les images selon le ratio `XX`, par exemple `-resize50` pour 50%. Basé sur [ImageMagick](http://www.imagemagick.org), lequel doit être disponible dans le PATH ou packagé avec l'appli.

### 1.3.0 (2017/08/19)

* le programme vérifie la présence de mise à jour de l'application au démarrage.
* suppression du paramètre `-compresspic`, la compression des images étant trop peu efficace et peu fiable.
* interne : le code est migré de Java 8 vers Kotlin, pour faciliter sa possible future intégration dans une appli Android.
* interne : les dépendances (Maven) vers les librairies tierces instables sont supprimées.
* interne : le package de release embarque des infos de base du projet Git (git-infos).

### 1.2.2 (2017/08/05)

* tous les messages sont archivés dans des logs. Voir le sous-dossier `logs`.
* le numéro de version de l'application est affiché au démarrage.

### 1.2.1 (2017/07/21)

* correction de la récupération des numéros (suite aux récentes modifications du site).

### 1.2.0 (2017/07/06)

* le fichier de sortie porte maintenant le nom `CPCxxx-opts.ext` où `xxx` est le numéro, `ext` l'extension voulue et `-opts` reprends le nom de certains paramètres (`-nopic`, `-compresspic`), par exemple `CPC348-nopic.html`.
* optimisations et nettoyage du code.

### 1.1.0 (2017/06/30)

* intégré : liens externes figurants à la fin des articles.
* intégré : les textes en gras ou italique dans les articles.
* les liens vers les articles online sont maintenant positionnés à la fois dans le sommaire et au niveau de chaque article.
* ajout d'informations sur le projet juste après le sommaire (liens GitHub, PayPal, licence, etc, et une image CPC rigolote).
* la taille du fichier d'export HTML est affichée en fin de process.
* de courtes pauses son marquées durant le téléchargement pour ne pas surcharger le serveur CanardPC : 30s entre chaque numéro, 500ms entre chaque article et 250ms entre chaque image. Cela permet aussi de réduire drastiquement les erreurs de téléchargement (articles vides).
* il est désormais possible de télécharger un, plusieurs ou tous les numéros :
  * `-cpc348` télécharge uniquement le numéro 348.
  * `-cpc348 -cpc349 -cpc350...` le paramètre `-cpc` peut être répété, ici pour télécharger les numéros 348, 349 et 350.
  * `-cpcall` télécharge tous les numéros à votre disposition.
* correction d'un crash (sans conséquences) à la fermeture du programme lorsque `-list` est utilisé sans demander d'export.
* ajout du paramètre `-compresspic` pour compresser les images lorsque c'est possible, afin de gagner quelques Mo sur le fichier final. Les images ne sont donc plus compressées par défaut car certaines images PNG posent problème (le canal Alpha semble mal géré et les images compressées ne sont pas satisfaisantes).

### 1.0.0 (2017/06/15)

* le programme est packagé pour Windows avec un JRE [Zulu d'Azul](http://www.azul.com/downloads/zulu/zulu-windows/) (se compresse beaucoup mieux que la [HotSpot d'Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)). Lancer `c2e.cmd` suivi des paramètres décrits dans la version 0.0.4, par exemple `c2e.bat username password -cpc348`. Attention, `-gui` n'est pas encore branché, ni `-pdf` et `-epub`. Les utilisateurs de MacOS, Linux ou BSD devraient savoir se débrouiller pour installer un JRE (un script sera créé plus tard).

### 0.0.4 (2017/06/14)

* un affichage optimisé pour la lecture en journée, et un autre pour la nuit. Un bouton permet de passer de l'un à l'autre facilement.
* une CLI basique est disponible. Les paramètres sont : `username password [-gui] [-debug] [-list] [-cpc348] [-pdf] [-epub] [-html] [-nopic]` où `-gui` démarre une interface graphique au lieu de la ligne de commande, `-cpc` spécifie le numéro à télécharger, `-pdf` `-epub` `-html` le format de sortie (seul `-html` est branché aujourd'hui), `-nopic` pour ne pas téléchanger les images (un numéro contient 60~100Mo d'images, et ~500Ko de texte), et `-list` pour savoir quels numéros sont accessibles au téléchargement. Le fichier est généré (ou écrasé) dans le répertoire courant et porte le nom `CPCxxx.ext` où `xxx` est le numéro et `ext` l'extension voulue, par exemple `CPC348.html`. Enfin, `-debug` affiche le détail du téléchargement dans un format proche de JSON.
  
### 0.0.3 (2017/06/06)

* conversion des images PNG en JPEG pour réduire la taille finale de l'export (~40% de gain). *ImageMagick sera surement intégré après la v1.0.0 pour corriger la conversion de certains PNG et mieux gérer les couleurs RGB ou CMYK, le canal Alpha (transparence), etc. Cela concerne heureusement peu d'images.*
* corrigé : détection des contenus de type "encadré" et des contenus "inter-titre" présents plus d'une fois par article, boutons de partage Twitter/Facebook/Email retirés.
* intégré : avis et état (des jeux en cours de dev), sous-titre, catégorie et titre des news.

### 0.0.2 (2017/06/03)
* le sommaire est correctement affiché (flottant + style + liens vers articles + nombre de colonnes adapté à l'écran), reprennant la mise en forme du site.
* articles en cours d'intégration. Ce qui est fait : auteur, contenus, spécifications, score, images, mieux prendre en compte les scores non numériques, améliorer le rendu du titre des articles.

### 0.0.1 (2017/05/25)
* l'import du magazine 348 fonctionne, des adaptations mineures sont en cours de dev pour supporter les numéros plus récents. L'approche est la suivante : l'import des articles d'un numéro est tenté avec plusieurs scrapers (aspirateurs de magazine), et on ne garde que les résultats les plus parlants, chaque export ayant un score basé sur la quantité de contenu extrait. Nota : le téléchargement des articles ne se fait pas en parallèle, ceci pour ne pas surcharger les serveurs CanardPC.
* export HTML initié : le contenu est inséré, toute la mise en forme reste à faire. Cette version HTML est monopage et intègre en son code le CSS, JS, et images en base64. Version responsive prévue ainsi qu'un mode nuit (comme sur le site).
