@echo off
rem --- Construit les paquetages TikiOne C2E pour Windows ---

rem Variables d'environnement à créer :
rem TK1_7Z : chemin de 7-zip (par exemple C:\Program Files\7-Zip\, doit contenir 7z.exe)
rem TK1_JRE : chemin d'un JRE8 (par exemple C:\Program Files\Java8\, doit contenir bin\java.exe)
rem TK1_MAGICK : chemin d'un dossier ImageMagick (par exemple C:\Program Files\ImageMagick-7.0.6-10-portable-Q16-x64\, doit contenir magick.exe)

if not exist %cd%\build\libs\c2e.jar (
    echo "Compilez l'application en premier (gradlew.bat clean jar), puis relancez le batch"
) else if not exist %TK1_7Z%7z.exe (
    echo "Renseignez correctement la variable d'environnement TK1_7Z"
) else if not exist %TK1_JRE%\bin\java.exe (
    echo "Renseignez correctement la variable d'environnement TK1_JRE"
) else if not exist %TK1_MAGICK%\magick.exe (
    echo "Renseignez correctement la variable d'environnement TK1_MAGICK"
) else (
    set "APP=%cd%\c2e"
    set "PATH=%TK1_7Z%;%PATH%"
    set "APP_MAGICK=%APP%\imagemagick\"
    set "APP_JRE=%APP%\jre\"

    echo "Nettoyage"
    rmdir /Q /S %APP%
    del "c2e-%1-withWin64JRE-withImageMagick.zip"
    del "c2e-%1-withWin64JRE.zip"
    del "c2e-%1.zip"

    echo "Construcion de %APP%"
    mkdir %APP%
    robocopy %cd%\build\libs\ %APP% /E

    echo "Copie de la JRE et d'ImageMagick"
    robocopy %TK1_MAGICK% %APP_MAGICK% /E
    robocopy %TK1_JRE% %APP_JRE% /E

    echo "Creation du paquetage avec JRE et ImageMagick"
    7z.exe a -tzip -ssw -mx5 "c2e-%1-withWin64JRE-withImageMagick.zip" "%APP%"

    echo "Creation du paquetage avec JRE, sans ImageMagick"
    rmdir /Q /S %APP_MAGICK%
    7z.exe a -tzip -ssw -mx5 "c2e-%1-withWin64JRE.zip" "%APP%"

    echo "Creation du paquetage sans JRE, sans ImageMagick"
    rmdir /Q /S %APP_JRE%
    7z.exe a -tzip -ssw -mx5 "c2e-%1.zip" "%APP%"

    rmdir /Q /S %APP%

    echo "------------ Fichiers crees ------------"
    echo "c2e-%1-withWin64JRE-withImageMagick.zip"
    echo "c2e-%1-withWin64JRE.zip"
    echo "c2e-%1.zip"
    echo "----------------------------------------"
)
