package fr.tikione.c2e;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.tikione.c2e.model.web.Auth;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.service.GlobalModule;
import fr.tikione.c2e.service.html.HtmlWriterService;
import fr.tikione.c2e.service.web.CPCAuthService;
import fr.tikione.c2e.service.web.scrap.CPCReaderService;
import fr.tikione.gui.MainApp;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {
    
    public static boolean DEBUG = false;
    public static String VERSION = "1.2.1";
    
    // params: username password [-gui] [-debug] [-list] [-cpc360 -cpc361...|-cpcall] [-html] [-nopic] [-compresspic]
    public static void main(String... args) throws Exception {
        System.out.println("les paramètres de lancement sont : " + Arrays.toString(args));
        assert args != null;
        List<String> argsList = Arrays.asList(args);
        DEBUG = argsList.contains("-debug");
        if (argsList.contains("-gui")) {
            startGUI(args);
        } else {
            startCLI(args);
        }
    }
    
    private static void startCLI(String... args)
            throws IOException, InterruptedException {
        assert args.length > 2;
        List<String> switchList = Arrays.asList(args).subList(2, args.length);
        boolean list = switchList.contains("-list");
        boolean includePictures = !switchList.contains("-nopic");
        boolean compressPictures = switchList.contains("-compresspic");
        boolean doHtml = switchList.contains("-html");
        boolean allMags = switchList.contains("-cpcall");
        
        Injector cpcInjector = Guice.createInjector(new GlobalModule());
        CPCAuthService cpcAuthService = cpcInjector.getInstance(CPCAuthService.class);
        CPCReaderService cpcReaderService = cpcInjector.getInstance(CPCReaderService.class);
        
        Auth auth = cpcAuthService.authenticate(args[0], args[1]);
        List<Integer> headers = cpcReaderService.listDownloadableMagazines(auth);
        List<Integer> magNumbers = new ArrayList<>();
        if (allMags) {
            magNumbers.addAll(headers);
        } else {
            for (String arg : args) {
                if (arg.startsWith("-cpc")) {
                    try {
                        magNumbers.add(Integer.parseInt(arg.substring("-cpc".length())));
                    } catch (NumberFormatException nfe) {
                        System.out.println("un numéro du magazine CPC est mal tapé, il sera ignoré");
                    }
                }
            }
        }
        if (list) {
            System.out.println("les numéros disponibles sont : " + headers);
        }
        
        if (doHtml) {
            if (magNumbers.size() > 1) {
                System.out.println("téléchargement des numéros : " + magNumbers);
            }
            for (int i = 0; i < magNumbers.size(); i++) {
                int magNumber = magNumbers.get(i);
                Magazine magazine = cpcReaderService.downloadMagazine(auth, magNumber);
                File file = new File("CPC" + magNumber
                        + (includePictures ? "" : "-nopic")
                        + (compressPictures ? "-compresspic" : "")
                        + ".html");
                HtmlWriterService writerService = cpcInjector.getInstance(HtmlWriterService.class);
                writerService.write(magazine, file, includePictures, compressPictures);
                if (i != magNumbers.size() - 1) {
                    System.out.print("pause de 30s avant de télécharger le prochain numéro");
                    for (int j = 0; j < 30; j++) {
                        System.out.print(".");
                        TimeUnit.SECONDS.sleep(1);
                    }
                    System.out.println(" ok\n");
                }
            }
        }
        
        System.out.println("terminé !");
    }
    
    private static void startGUI(String... args)
            throws Exception {
        Application.launch(MainApp.class, args);
    }
}
