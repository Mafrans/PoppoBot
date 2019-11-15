//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.mafrans.poppo.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import me.mafrans.mahttpd.MaHTTPD;
import org.apache.commons.io.IOUtils;

public class FileUtils {
    public FileUtils() {
    }

    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines = Files.readAllLines(file.toPath());

        for(int i = 0; i < lines.size(); ++i) {
            if (i != 0) {
                sb.append("\n");
            }

            sb.append(lines.get(i));
        }

        return sb.toString();
    }

    public static String readFileSupressed(File file) {
        try {
            StringBuilder sb = new StringBuilder();
            List<String> lines = Files.readAllLines(file.toPath());

            for(int i = 0; i < lines.size(); ++i) {
                if (i != 0) {
                    sb.append("\n");
                }

                sb.append(lines.get(i));
            }

            return sb.toString();
        } catch (Exception var4) {
            return "";
        }
    }

    public static void writeFile(File file, String string) throws IOException {
        Files.write(file.toPath(), Arrays.asList(string.split("\\r?\\n")), Charset.forName("UTF-8"));
    }

    public static void writeFileSupressed(File file, String string) {
        try {
            writeFile(file, string);
        } catch (IOException var3) {
            ;
        }
    }

    public static void initializeFile(File file, String string, boolean supressed) throws IOException {
        if(file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        if (supressed) {
            if (file.exists()) {
                if (readFileSupressed(file).isEmpty()) {
                    writeFileSupressed(file, string);
                }
            } else {
                file.createNewFile();
                writeFileSupressed(file, string);
            }
        } else if (file.exists()) {
            if (readFile(file).isEmpty()) {
                writeFile(file, string);
            }
        } else {
            file.createNewFile();
            writeFile(file, string);
        }

    }

    public static void addToClasspath(File f) throws Exception {
        URL u = f.toURL();
        URLClassLoader urlClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u);
    }

    public static String readStream(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        return writer.toString();
    }

    public static void createResource(String resource, File outFile, Class origin) throws IOException {
        InputStream stream = origin.getClassLoader().getResourceAsStream(resource);
        if (stream != null && !outFile.exists()) {
            initializeFile(outFile, readStream(stream), false);
        }
    }

    public static void createResource(Class origin, String resource) throws IOException {
        createResource(resource, new File(resource), origin);
    }
}
