/*
 * Copyright 2006 Antonio S. R. Gomes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.profiler4j.console;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Class responsible for loading the console application.
 * 
 * @author Antonio S. R. Gomes
 */
public class Bootstrap {

    /**
     * Name of the class the will have the method main() invoked.
     */
    private static final String MAIN_CLASS = "net.sf.profiler4j.console.Console";

    /**
     * Application entry point
     * @param args
     */
    public static void main(final String[] args) {
        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = Bootstrap.class.getResource("Bootstrap.class");

            if (url.toString().startsWith("jar:file:")) {
                File homeDir = getDir(url);
                File libDir = new File(homeDir, "lib");
                File[] libs = libDir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".jar");
                    };
                });
                URL[] urls = new URL[libs.length];
                int l0 = homeDir.getAbsolutePath().length();
                for (int i = 0; i < libs.length; i++) {
                    print("Added library " + libs[i].getAbsolutePath().substring(l0 + 1));
                    urls[i] = libs[i].toURL();
                }
                loader = new URLClassLoader(urls, Bootstrap.class.getClassLoader());
                Thread.currentThread().setContextClassLoader(loader);
            }

            //
            // Invoke the main method using the classloder
            //            
            Class cc = loader.loadClass(MAIN_CLASS);
            Method m = cc.getDeclaredMethod("main", new Class[]{String[].class});
            m.invoke(null, new Object[]{args});

        } catch (InvocationTargetException e) {
            print("Could not bootstrap application", e.getTargetException());
            System.exit(1);
        } catch (Exception e) {
            print("Could not bootstrap application", e);
            System.exit(1);
        }
    }

    /**
     * Gets the file that represents the directory in the given URL.
     * 
     * @param url URL representing the resource (must be of type jar:file)
     * @return file
     * 
     * @throws UnsupportedEncodingException
     */
    private static File getDir(URL url) throws UnsupportedEncodingException {
        String s = url.toString();
        // print("original url = " + s);
        s = URLDecoder.decode(s, Charset.defaultCharset().name());
        // print("Decoded URL (charset" + Charset.defaultCharset().name()
        // + ") = " + s);

        // Remove the preffix "jar:file:"
        int p = s.indexOf("!");
        s = s.substring(9, p);

        // In the default case the path is something like "/dir/subdir", but
        // this assumption may not be true in windows, so we must check it
        if (s.indexOf(':') == 2) {
            // Probably we are in windows and the string should be something
            // like "/g:/tools/app..." so we must strip the first char out
            s = s.substring(1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append((s.charAt(i) == '/') ? File.separatorChar : s.charAt(i));
        }
        s = sb.toString();
        File homeDir = new File(s).getParentFile();
        print("Install dir is " + homeDir.getAbsolutePath());
        return homeDir;
    }

    private static void print(String s) {
        System.out.println("[P4J-BOOTSTRAP] " + s);
    }

    private static void print(Object s, Throwable t) {
        System.out.println("[P4J-BOOTSTRAP] " + s);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        for (String ste : sw.toString().split("\n+")) {
            System.out.println("[P4J-BOOTSTRAP] " + ste);
        }
    }

}
