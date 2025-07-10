/*
 * Copyright 2025 Stoyan Petkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.venylang.veny.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Loads the Veny standard library source files for the compiler.
 * <p>
 * The loader attempts to locate and load `.veny` source files from two locations:
 * <ul>
 *     <li><strong>Classpath resources</strong> – usually from a packaged JAR</li>
 *     <li><strong>Development path override</strong> – if running in development mode</li>
 * </ul>
 * This allows seamless support for both development and deployment scenarios.
 */
public class StdlibLoader {

    private final String basePackage;
    private final Optional<Path> devOverridePath;
    private final String resourcePath;

    /**
     * Constructs a new {@code StdlibLoader}.
     *
     * @param basePackage     the base package name where stdlib source files are located (e.g., {@code veny.lang})
     * @param devOverridePath an optional path to use as a fallback in development mode
     */
    public StdlibLoader(String basePackage, Optional<Path> devOverridePath) {
        this.basePackage = basePackage;
        this.devOverridePath = devOverridePath;
        this.resourcePath = basePackage.replace('.', '/');
    }

    /**
     * Loads all `.veny` source files from the classpath or, if not found, from the development override path.
     *
     * @return a list of validated {@link SourceFile} instances
     * @throws RuntimeException if loading or decoding any file fails
     */
    public List<SourceFile> load() {
        List<SourceFile> result = new ArrayList<>();

        try {
            if (devOverridePath.isPresent()) {
                loadFromDevPath(result);
            } else {
                loadFromClasspath(result);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load stdlib resources", e);
        }

        return result;
    }

    /**
     * Attempts to load stdlib source files from the classpath (e.g., JAR resources or filesystem).
     *
     * @param result the list to append loaded {@link SourceFile} instances to
     * @return {@code true} if any classpath resource was found and processed
     * @throws IOException if resource loading fails
     */
    private boolean loadFromClasspath(List<SourceFile> result) throws IOException {
        boolean foundAny = false;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(resourcePath);

        while (resources.hasMoreElements()) {
            foundAny = true;
            URL url = resources.nextElement();

            switch (url.getProtocol()) {
                case "file" -> {
                    String path = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
                    // problem with leading / on Windows
                    if (System.getProperty("os.name").toLowerCase().contains("win") && path.matches("^/[A-Za-z]:/.*")) {
                        path = path.substring(1);
                    }
                    Path dir = Paths.get(path);
                    loadFromFileSystem(dir, result);
                }
                case "jar" -> loadFromJar(url, result);
            }
        }

        return foundAny;
    }

    /**
     * Loads stdlib source files from a development override directory (if specified).
     *
     * @param result the list to append loaded {@link SourceFile} instances to
     * @throws IOException if file traversal or reading fails
     */
    private void loadFromDevPath(List<SourceFile> result) throws IOException {
        Path path = devOverridePath.get().resolve(resourcePath);
        if (Files.exists(path)) {
            loadFromFileSystem(path, result);
        }
    }

    /**
     * Recursively loads `.veny` files from a base directory on the filesystem.
     *
     * @param baseDir the directory to search within
     * @param result  the list to append loaded {@link SourceFile} instances to
     * @throws IOException if file traversal or reading fails
     */
    private void loadFromFileSystem(Path baseDir, List<SourceFile> result) throws IOException {
        Files.walk(baseDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".veny"))
                .forEach(p -> {
                    try {
                        result.add(SourceFile.of(p));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file: " + p, e);
                    }
                });
    }

    /**
     * Loads `.veny` source files from within a JAR file.
     *
     * @param jarResourceUrl the URL to the JAR resource
     * @param result         the list to append loaded {@link SourceFile} instances to
     * @throws IOException if reading the JAR or entries fails
     */
    private void loadFromJar(URL jarResourceUrl, List<SourceFile> result) throws IOException {
        String jarPath = jarResourceUrl.getPath();
        jarPath = jarPath.substring(5, jarPath.indexOf("!")); // strip "file:" prefix and "!/veny/lang"
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() &&
                        entry.getName().startsWith(resourcePath) &&
                        entry.getName().endsWith(".veny")) {

                    try (InputStream is = jarFile.getInputStream(entry)) {
                        result.add(SourceFile.of(entry.getName(), is));
                    }
                }
            }
        }
    }
}
