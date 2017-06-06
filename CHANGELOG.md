## TikiOne C2E Change Log

### 0.0.3 (WIP, 2017/06/06)

Youpi, le n°348 est complètement intégré !  
Intégration des numéros plus récents, ça devrait être rapide.

* conversion des images PNG en JPEG pour réduire la taille finale de l'export (~40% de gain). *ImageMagick sera surement intégré après la v1.0.0 pour corriger la conversion de certains PNG et mieux gérer les couleurs RGB ou CMYK, le canal Alpha (transparence), etc. Cela concerne heureusement peu d'images.*
* corrigé : détection des contenus de type "encadré" et des contenus "inter-titre" présents plus d'une fois par article.
* intégré : avis et état (des jeux en cours de dev), sous-titre.

### 0.0.2 (2017/06/03)
* le sommaire est correctement affiché (flottant + style + liens vers articles + nombre de colonnes adapté à l'écran), reprennant la mise en forme du site.
* articles en cours d'intégration. Ce qui est fait : auteur, contenus, spécifications, score, images, mieux prendre en compte les scores non numériques, améliorer le rendu du titre des articles.

### 0.0.1 (2017/05/25)
* l'import du magazine 348 fonctionne, des adaptations mineures sont en cours de dev pour supporter les numéros plus récents. L'approche est la suivante : l'import des articles d'un numéro est tenté avec plusieurs scrapers (aspirateurs de magazine), et on ne garde que les résultats les plus parlants, chaque export ayant un score basé sur la quantité de contenu extrait. Nota : le téléchargement des articles ne se fait pas en parallèle, ceci pour ne pas surcharger les serveurs CanardPC.
* export HTML initié : le contenu est inséré, toute la mise en forme reste à faire. Cette version HTML est monopage et intègre en son code le CSS, JS, et images en base64. Version responsive prévue ainsi qu'un mode nuit (comme sur le site).
