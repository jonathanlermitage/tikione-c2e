@echo off

if [%1] == [help] (
  echo  b:     compilation
  echo  p $V:  construit les paquetages TikiOne C2E pour Windows en version $V
  echo  w $V:  crée ou met à jour le wrapper Gradle en version $V
  echo  cv:    recherche le mises à jour de librairies
)

if [%1] == [b] (
  echo gradlew -i clean jar --no-daemon
  gradlew -i clean jar --no-daemon
)
if [%1] == [w] (
  echo gradle wrapper --gradle-version=%2 --no-daemon
  gradle wrapper --gradle-version=%2 --no-daemon
)
if [%1] == [cv] (
  echo gradlew -Drevision=release -DoutputFormatter=plain -DoutputDir=./build -i dependencyUpdates  --no-daemon
  gradlew dependencyUpdates -Drevision=release -DoutputFormatter=plain -DoutputDir=./build -i --no-daemon
)
if [%1] == [p] (
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
      del "c2e-%2-withWin64JRE-withImageMagick.zip"
      del "c2e-%2-withWin64JRE.zip"
      del "c2e-%2.zip"
  
      echo "Construcion de %APP%"
      mkdir %APP%
      robocopy %cd%\build\libs\ %APP% /E
  
      echo "Copie de la JRE et d'ImageMagick"
      robocopy %TK1_MAGICK% %APP_MAGICK% /E
      robocopy %TK1_JRE% %APP_JRE% /E

      echo "Creation du paquetage avec JRE et ImageMagick"
      7z.exe a -tzip -ssw -mx5 "c2e-%2-withWin64JRE-withImageMagick.zip" "%APP%"

      echo "Creation du paquetage avec JRE, sans ImageMagick"
      rmdir /Q /S %APP_MAGICK%
      7z.exe a -tzip -ssw -mx5 "c2e-%2-withWin64JRE.zip" "%APP%"

      echo "Creation du paquetage sans JRE, sans ImageMagick"
      rmdir /Q /S %APP_JRE%
      7z.exe a -tzip -ssw -mx5 "c2e-%2.zip" "%APP%"

      rmdir /Q /S %APP%
  
      echo "------------ Fichiers crees ------------"
      echo "c2e-%2-withWin64JRE-withImageMagick.zip"
      echo "c2e-%2-withWin64JRE.zip"
      echo "c2e-%2.zip"
  )
)
