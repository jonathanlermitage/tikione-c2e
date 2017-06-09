## TikiOne C2E Change Log

### 0.0.4 (WIP)

L'export HTML est finalisé et a été testé sur les numéros 348 à 361. Reste à rendre le programme facile à utiliser. 

* un affichage optimisé pour la lecture en journée, et un autre pour la nuit. Un bouton permet de passer de l'un à l'autre facilement.
* une CLI basique est disponible. Les paramètres sont : ``username password [-debug] [-list] [-cpc=348] [-pdf] [-epub] [-html] [-nopic]`` où ``-cpc`` spécifie le numéro à télécharger, ``-pdf`` ``-epub`` ``-html`` le format de sortie (seul ``-html`` est branché aujourd'hui), ``-nopic`` pour ne pas téléchanger les images (un numéro contient 60~100Mo d'images, et ~500Ko de texte), et ``-list`` pour savoir quels numéros sont accessibles au téléchargement. Le fichier est généré (ou écrasé) dans le répertoire courant et porte le nom ``CPCxxx.ext`` où ``xxx`` est le numéro et ``ext`` l'extension voulue, par exemple ``CPC348.html``.
* en cours : 
  * packager le programme avec un JRE [Zulu d'Azul](http://www.azul.com/downloads/zulu/zulu-windows/) (se compresse beaucoup mieux que la [HotSpot d'Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)).
  
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
