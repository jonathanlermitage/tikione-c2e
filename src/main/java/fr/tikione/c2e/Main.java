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
    
    // params: username password [-debug] [-pdf] [-epub] [-html]
    public static void main(String... args) throws Exception {
        assert args != null;
        assert args.length > 2;
        List<String> argsList = Arrays.asList(args).subList(2, args.length);
        DEBUG = argsList.contains("-debug");
        boolean doPdf = argsList.contains("-pdf");
        boolean doEpub = argsList.contains("-epub");
        boolean doHtml = argsList.contains("-html");
        if (!doPdf && !doEpub && !doHtml) {
            throw new Exception("should use at least '-pdf', '-epub' or '-html' param to convert magazine");
        }
        
        Injector cpcInjector = Guice.createInjector(new GlobalModule());
        CPCAuthService cpcAuthService = cpcInjector.getInstance(CPCAuthService.class);
        CPCReaderService cpcReaderService = cpcInjector.getInstance(CPCReaderService.class);
        
        Auth auth = cpcAuthService.authenticate(args[0], args[1]);
        List<Integer> headers = cpcReaderService.listDownloadableMagazines(auth);
        
        if (Main.DEBUG) {
            System.out.println(headers);
        }
        //int n = headers.get(0);
        //headers.parallelStream().forEach(integer -> cpcReaderService.downloadMagazine(auth, integer));
        
        int n = 348; // will be a parameter
        Magazine magazine = cpcReaderService.downloadMagazine(auth, n);
        
        if (doPdf) {
            File file = new File("CPC" + n + ".pdf");
            PdfWriterService writerService = cpcInjector.getInstance(PdfWriterService.class);
            writerService.write(magazine, file, true);
        }
        if (doEpub) {
            File file = new File("CPC" + n + ".epub");
            EpubWriterService writerService = cpcInjector.getInstance(EpubWriterService.class);
            writerService.write(magazine, file, true);
        }
        if (doHtml) {
            File file = new File("CPC" + n + ".html");
            HtmlWriterService writerService = cpcInjector.getInstance(HtmlWriterService.class);
            writerService.write(magazine, file, true);
        }
    }
}
