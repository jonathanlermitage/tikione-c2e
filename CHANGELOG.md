## TikiOne Steam Cleaner Change Log

### 0.0.1 (2017/05/25) JAVA8
* l'import du magazine 348 fonctionne, des adaptations mineures sont en cours de dev pour supporter les numéros plus récents. L'approche est la suivante : l'import des articles d'un numéro est tenté avec plusieurs scrapers (aspirateurs de magazine), et on ne garde que les résultats les plus parlants, chaque export ayant un score basé sur la quantité de contenu extrait. Nota : le téléchargement des articles ne se fait pas en parallèle, ceci pour ne pas surcharger les serveurs CanardPC.
* export HTML initié : le contenu est inséré, toute la mise en forme reste à faire. Cette version HTML est monopage et intègre en son code le CSS, JS, et images en base64. Version responsive prévue ainsi qu'un mode nuit (comme sur le site).
