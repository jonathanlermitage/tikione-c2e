## TikiOne C2E Change Log

L'export HTML est finalisé et a été testé sur les numéros 348 à 362. Reste à rendre le programme facile à utiliser et peaufiner l'export HTML. 

* en cours : 
  * une GUI basée sur Kotlin, JavaFX et TornadoFX.
  * assister le lancement du programme sous MacOS, Linux ou BSD.
  * améliorations mineures sur l'export HTML.

### 1.1.0 (2017/06/24 - WIP)

* intégré : liens externes figurants à la fin des articles.
* les liens vers les articles online sont maintenant positionnés à la fois dans le sommaire et au niveau de chaque article.
* la taille du fichier d'export HTML est affichée en fin de process.
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
