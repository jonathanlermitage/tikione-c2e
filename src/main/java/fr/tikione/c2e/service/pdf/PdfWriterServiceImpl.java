package fr.tikione.c2e.service.pdf;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Magazine;

import java.io.File;
import java.io.IOException;

public class PdfWriterServiceImpl implements PdfWriterService {
    
    @Inject
    public PdfWriterServiceImpl() {
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(Magazine magazine, File file, boolean incluePictures) throws IOException {
        file.delete();
        if (file.exists()) {
            throw new IOException("cannot write over file " + file.getAbsolutePath());
        }
        
        
    }
}
