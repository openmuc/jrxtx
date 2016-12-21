package gnu.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LibraryLoader to load a nested binary from a jar.
 */
class LibraryLoader {
    public static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(.+)(\\.[^\\.]+)$");

    public static boolean loadLibsFromJar(String... toResPaths) {
        for (String toResPath : toResPaths) {
            try {
                if (!loadLib(toResPath)) {
                    return false;
                }
            } catch (URISyntaxException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    private static boolean loadLib(String libsDIr) throws URISyntaxException, IOException {

        URL dirUrl = LibraryLoader.class.getResource(libsDIr);

        if (dirUrl == null) {
            return false;
        }

        String protocol = dirUrl.getProtocol();
        if (protocol.equals("jar")) {
            return loadFromJar(dirUrl, libsDIr);
        }
        else if (protocol.equals("file")) {
            return loadFromFile(dirUrl);
        }
        else {
            return false;
        }
    }

    private static boolean loadFromFile(URL dirUrl) throws URISyntaxException {
        File libsDir = new File(dirUrl.toURI());

        if (!libsDir.isDirectory()) {
            return false;
        }

        for (File lib : libsDir.listFiles()) {
            System.load(lib.getAbsolutePath());
        }

        return true;
    }

    private static boolean loadFromJar(URL dirUrl, String libsDir) throws IOException {
        String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!"));

        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));

        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry element = entries.nextElement();

            if (element.isDirectory() || !element.getName().startsWith(libsDir.substring(1))) {
                continue;
            }

            InputStream fileInputStream = jar.getInputStream(element);
            try {
                if (!saveResStreamToFileAndLoad(element.getName(), fileInputStream)) {
                    return false;
                }

            } finally {
                saveClose(fileInputStream);
            }
        }
        return true;
    }

    private static boolean saveResStreamToFileAndLoad(String fileName, InputStream is) throws IOException {
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            return false;
        }
        File tempFileLib = File.createTempFile(matcher.group(1), matcher.group(2));
        tempFileLib.deleteOnExit();

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(tempFileLib);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            try {
                System.load(tempFileLib.getAbsolutePath());
            } catch (UnsatisfiedLinkError e) {
                return false;
            }
        } finally {
            saveClose(fos);
        }
        return true;
    }

    private static void saveClose(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    private LibraryLoader() {
    }

}
