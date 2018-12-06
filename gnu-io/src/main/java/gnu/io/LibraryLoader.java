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
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LibraryLoader to load a nested binary from a jar.
 */
class LibraryLoader {
    public static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(.+)(\\.[^\\.]+)$");

    private static Set<String> successfullyLoaded = new HashSet<String>();

    public synchronized static void loadRxtxNative() {
        try {
            loadLibsFromJar("/libs");
        } catch (LibLoadException e) {
            try {
                System.loadLibrary("rxtxSerial");
            } catch (UnsatisfiedLinkError e1) {
                System.err.println("Could not load lib from jar and from system.");
                e.printStackTrace();
                throw e1;
            }
        }
    }

    private static boolean loadLibsFromJar(String... toResPaths) throws LibLoadException {
        for (String toResPath : toResPaths) {

            if (successfullyLoaded.contains(toResPath)) {
                continue; // skip if loaded already
            }
            try {
                loadLib(toResPath);
            } catch (URISyntaxException e) {
                throw new LibLoadException("Failed to load Library.", e);
            } catch (IOException e) {
                throw new LibLoadException("Failed to load Library.", e);
            }
            successfullyLoaded.add(toResPath);
        }

        return true;
    }

    private static void loadLib(String libsDIr) throws URISyntaxException, IOException, LibLoadException {

        URL dirUrl = LibraryLoader.class.getResource(libsDIr);

        if (dirUrl == null) {
            throw new LibLoadException("directory does not exist " + libsDIr);
        }

        String protocol = dirUrl.getProtocol();
        if (protocol.equals("jar")) {
            loadFromJar(dirUrl, libsDIr);
        }
        else if (protocol.equals("file")) {
            loadFromFile(dirUrl);
        }
        else {
            throw new LibLoadException("unknown protocol: " + protocol);
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

    private static void loadFromJar(URL dirUrl, String libsDir) throws IOException, LibLoadException {
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
                saveResStreamToFileAndLoad(element.getName(), fileInputStream);
            } finally {
                saveClose(fileInputStream);
            }
        }
    }

    private static void saveResStreamToFileAndLoad(String fileName, InputStream is)
            throws IOException, LibLoadException {
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            throw new LibLoadException("Filename '" + fileName + "' does not match pattern.");
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

            System.load(tempFileLib.getAbsolutePath());
        } finally {
            saveClose(fos);
        }
    }

    private static void saveClose(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    private LibraryLoader() {
    }

}
