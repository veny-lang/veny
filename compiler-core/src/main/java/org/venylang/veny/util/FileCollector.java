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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCollector {

    private static final String VENY_EXTENSION = ".veny";

    private final List<Path> venyFiles;

    private final Path baseDir;

    public static final FileCollector of(Path dir) {
        return new FileCollector(dir);
    }

    private FileCollector(Path dir) {
        baseDir = dir;
        List<Path> collected;
        try (Stream<Path> stream = Files.walk(dir)) {
            collected = stream
                .filter(p -> p.toString().endsWith(VENY_EXTENSION))
                .collect(Collectors.toUnmodifiableList());  // Immutable list
        } catch (IOException e) {
            System.err.println("Error reading directory: " + dir);
            collected = List.of(); // Empty immutable list on error
        }
        this.venyFiles = collected;
    }

    public Stream<Path> stream() {
        return venyFiles.stream();
    }

    public void forEach(Consumer<Path> action) {
        venyFiles.forEach(action);
    }

    public int size() {
        return venyFiles.size();
    }

    public boolean isEmpty() {
        return venyFiles.isEmpty();
    }

    public  Path baseDir() {
        return baseDir;
    }
}
