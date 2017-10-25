## TikiOne C2E Change Log

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

* tous les messages sont archivés dans des logs. Voir le sous-dossier ``logs``.
* le numéro de version de l'application est affiché au démarrage.

### 1.2.1 (2017/07/21)

* correction de la récupération des numéros (suite aux récentes modifications du site).

### 1.2.0 (2017/07/06)

* le fichier de sortie porte maintenant le nom ``CPCxxx-opts.ext`` où ``xxx`` est le numéro, ``ext`` l'extension voulue et ``-opts`` reprends le nom de certains paramètres (``-nopic``, ``-compresspic``), par exemple ``CPC348-nopic.html``.
* optimisations et nettoyage du code.

### 1.1.0 (2017/06/30)

* intégré : liens externes figurants à la fin des articles.
* intégré : les textes en gras ou italique dans les articles.
* les liens vers les articles online sont maintenant positionnés à la fois dans le sommaire et au niveau de chaque article.
* ajout d'informations sur le projet juste après le sommaire (liens GitHub, PayPal, licence, etc, et une image CPC rigolote).
* la taille du fichier d'export HTML est affichée en fin de process.
* de courtes pauses son marquées durant le téléchargement pour ne pas surcharger le serveur CanardPC : 30s entre chaque numéro, 500ms entre chaque article et 250ms entre chaque image. Cela permet aussi de réduire drastiquement les erreurs de téléchargement (articles vides).
* il est désormais possible de télécharger un, plusieurs ou tous les numéros :
  * ``-cpc348`` télécharge uniquement le numéro 348.
  * ``-cpc348 -cpc349 -cpc350...`` le paramètre ``-cpc`` peut être répété, ici pour télécharger les numéros 348, 349 et 350.
  * ``-cpcall`` télécharge tous les numéros à votre disposition.
* correction d'un crash (sans conséquences) à la fermeture du programme lorsque ``-list`` est utilisé sans demander d'export.
* ajout du paramètre ``-compresspic`` pour compresser les images lorsque c'est possible, afin de gagner quelques Mo sur le fichier final. Les images ne sont donc plus compressées par défaut car certaines images PNG posent problème (le canal Alpha semble mal géré et les images compressées ne sont pas satisfaisantes).

### 1.0.0 (2017/06/15)

* le programme est packagé pour Windows avec un JRE [Zulu d'Azul](http://www.azul.com/downloads/zulu/zulu-windows/) (se compresse beaucoup mieux que la [HotSpot d'Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)). Lancer ``c2e.cmd`` suivi des paramètres décrits dans la version 0.0.4, par exemple ``c2e.bat username password -cpc348``. Attention, ``-gui`` n'est pas encore branché, ni ``-pdf`` et ``-epub``. Les utilisateurs de MacOS, Linux ou BSD devraient savoir se débrouiller pour installer un JRE (un script sera créé plus tard).

### 0.0.4 (2017/06/14)

* un affichage optimisé pour la lecture en journée, et un autre pour la nuit. Un bouton permet de passer de l'un à l'autre facilement.
* une CLI basique est disponible. Les paramètres sont : ``username password [-gui] [-debug] [-list] [-cpc348] [-pdf] [-epub] [-html] [-nopic]`` où ``-gui`` démarre une interface graphique au lieu de la ligne de commande, ``-cpc`` spécifie le numéro à télécharger, ``-pdf`` ``-epub`` ``-html`` le format de sortie (seul ``-html`` est branché aujourd'hui), ``-nopic`` pour ne pas téléchanger les images (un numéro contient 60~100Mo d'images, et ~500Ko de texte), et ``-list`` pour savoir quels numéros sont accessibles au téléchargement. Le fichier est généré (ou écrasé) dans le répertoire courant et porte le nom ``CPCxxx.ext`` où ``xxx`` est le numéro et ``ext`` l'extension voulue, par exemple ``CPC348.html``. Enfin, ``-debug`` affiche le détail du téléchargement dans un format proche de JSON.
  
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
