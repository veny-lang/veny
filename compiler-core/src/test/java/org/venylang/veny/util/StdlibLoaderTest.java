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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

import static org.junit.jupiter.api.Assertions.*;

class StdlibLoaderTest {

    @TempDir
    Path tempDir;

    private ClassLoader originalClassLoader;

    @BeforeEach
    void saveClassLoader() {
        originalClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @AfterEach
    void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    @Test
    void loadsFromDevOverridePath() throws Exception {
        // Create dev override structure: tempDir/veny/lang/Test.veny
        Path devBase = tempDir.resolve("stdlib-resources");
        Path venyLang = devBase.resolve("veny/lang");
        Files.createDirectories(venyLang);
        Path file = venyLang.resolve("test.veny");
        String content = "package veny.lang\nclass Test(){}\n";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        StdlibLoader loader = new StdlibLoader("veny.lang", Optional.of(devBase));
        List<SourceFile> files = loader.load();

        assertEquals(1, files.size(), "Should load exactly one source file");
        SourceFile sf = files.get(0);

        Path expectedPath = devBase.resolve("veny/lang/test.veny").toAbsolutePath();
        assertEquals(expectedPath.toString(), sf.filename());
        assertEquals(content, sf.source());
    }

    @Test
    void loadsFromFileProtocolClasspath() throws Exception {
        // Create a directory structure and add it to a URLClassLoader
        Path classpathDir = tempDir.resolve("cp-resources");
        Path venyLang = classpathDir.resolve("veny/lang");
        Files.createDirectories(venyLang);
        Path file = venyLang.resolve("cp-test.veny");
        String content = "println(\"classpath file\");";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{classpathDir.toUri().toURL()},
                null
        );
        Thread.currentThread().setContextClassLoader(ucl);

        StdlibLoader loader = new StdlibLoader("veny.lang", Optional.empty());
        List<SourceFile> files = loader.load();

        assertEquals(1, files.size());
        SourceFile sf = files.get(0);

        Path expectedPath = classpathDir.resolve("veny/lang/cp-test.veny").toAbsolutePath();
        assertEquals(expectedPath.toString(), sf.filename());
        assertEquals(content, sf.source());
    }

    @Test
    void loadsFromJarOnClasspath() throws Exception {
        // Build a JAR containing veny/lang/jar-test.veny
        Path jarFile = tempDir.resolve("stdlib.jar");

        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile))) {
            // Add directory entry
            jos.putNextEntry(new JarEntry("veny/"));
            jos.closeEntry();

            jos.putNextEntry(new JarEntry("veny/lang/"));
            jos.closeEntry();

            // Add the .veny file
            String entryName = "veny/lang/jar-test.veny";
            jos.putNextEntry(new JarEntry(entryName));
            String content = "println(\"from jar\");";
            jos.write(content.getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{jarFile.toUri().toURL()},
                null
        );

        Thread.currentThread().setContextClassLoader(ucl);

        StdlibLoader loader = new StdlibLoader("veny.lang", Optional.empty());
        List<SourceFile> files = loader.load();

        assertEquals(1, files.size());
        SourceFile sf = files.get(0);

        assertEquals("veny/lang/jar-test.veny", sf.filename());
        assertEquals("println(\"from jar\");", sf.source());
    }

    @Test
    void skipsNonVenyFilesInDirectory() throws Exception {
        Path classpathDir = tempDir.resolve("cp-resources-2");
        Files.createDirectories(classpathDir.resolve("veny/lang"));
        Files.write(classpathDir.resolve("veny/lang/Ignore.txt"), "ignore".getBytes());
        Files.write(classpathDir.resolve("veny/lang/keep.veny"), "keep".getBytes());

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{classpathDir.toUri().toURL()},
                null
        );
        Thread.currentThread().setContextClassLoader(ucl);

        StdlibLoader loader = new StdlibLoader("veny.lang", Optional.empty());
        List<SourceFile> files = loader.load();

        assertEquals(1, files.size());

        Path expectedPath = classpathDir.resolve("veny/lang/keep.veny").toAbsolutePath();
        assertEquals(expectedPath.toString(), files.get(0).filename());
    }

    @Test
    void emptyClasspathAndNoDevPathReturnsEmptyList() {
        StdlibLoader loader = new StdlibLoader("nonexistent.pkg", Optional.empty());
        List<SourceFile> files = loader.load();
        assertTrue(files.isEmpty(), "Should return empty list when nothing is found");
    }
}