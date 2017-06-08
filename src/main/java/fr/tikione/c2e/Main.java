package fr.tikione.c2e;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.tikione.c2e.model.web.Auth;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.service.GlobalModule;
import fr.tikione.c2e.service.epub.EpubWriterService;
import fr.tikione.c2e.service.html.HtmlWriterService;
import fr.tikione.c2e.service.pdf.PdfWriterService;
import fr.tikione.c2e.service.web.CPCAuthService;
import fr.tikione.c2e.service.web.scrap.CPCReaderService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {
    
    public static boolean DEBUG = false;
    public static String VERSION = "0.0.4";
    
    // params: username password [-debug] [-list] [-cpc=360] [-pdf] [-epub] [-html] [-nopic]
    public static void main(String... args) throws Exception {
        assert args != null;
        assert args.length > 2;
        List<String> argsList = Arrays.asList(args).subList(2, args.length);
        DEBUG = argsList.contains("-debug");
        boolean list = argsList.contains("-list");
        boolean includePictures = !argsList.contains("-nopic");
        boolean doPdf = argsList.contains("-pdf");
        boolean doEpub = argsList.contains("-epub");
        boolean doHtml = argsList.contains("-html");
        int magNumber = -1;
        for (String arg : args) {
            if (arg.startsWith("-cpc=")) {
                try {
                    magNumber = Integer.parseInt(arg.substring("-cpc=".length()));
                } catch (NumberFormatException nfe) {
                    System.out.println("le numéro du magazine CPC est mal tapé");
                    return;
                }
            }
        }
        
        Injector cpcInjector = Guice.createInjector(new GlobalModule());
        CPCAuthService cpcAuthService = cpcInjector.getInstance(CPCAuthService.class);
        CPCReaderService cpcReaderService = cpcInjector.getInstance(CPCReaderService.class);
        
        Auth auth = cpcAuthService.authenticate(args[0], args[1]);
        List<Integer> headers = cpcReaderService.listDownloadableMagazines(auth);
    
        if (list) {
            System.out.println("les numéros disponibles sont : " + headers);
        }
    
        Magazine magazine = cpcReaderService.downloadMagazine(auth, magNumber);
        if (doPdf) {
            File file = new File("CPC" + magNumber + ".pdf");
            PdfWriterService writerService = cpcInjector.getInstance(PdfWriterService.class);
            writerService.write(magazine, file, includePictures);
        }
        if (doEpub) {
            File file = new File("CPC" + magNumber + ".epub");
            EpubWriterService writerService = cpcInjector.getInstance(EpubWriterService.class);
            writerService.write(magazine, file, includePictures);
        }
        if (doHtml) {
            File file = new File("CPC" + magNumber + ".html");
            HtmlWriterService writerService = cpcInjector.getInstance(HtmlWriterService.class);
            writerService.write(magazine, file, includePictures);
        }
        System.out.println("terminé !");
    }
}
