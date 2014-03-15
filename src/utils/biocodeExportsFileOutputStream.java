package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * biocodeExportsFileOutputStream provides a convenient format for writing Strings directly to the
 * fileOutputStream
 */
public class biocodeExportsFileOutputStream extends FileOutputStream {
    public biocodeExportsFileOutputStream(File file) throws FileNotFoundException {
        super(file);
    }

    public void write(String input) throws IOException {
        super.write(input.getBytes(Charset.forName("Latin1")));
    }
}
