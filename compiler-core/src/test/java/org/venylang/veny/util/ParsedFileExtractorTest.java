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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsedFileExtractorTest {

    @Test
    void testValidPackageAndImports() throws IOException {
        String content = """
            package myapp.models

            import utils.strings
            import core.math

            class Person {
                var name: String
            }
            """;

        Path tempFile = Files.createTempFile("Person", ".veny");
        Files.writeString(tempFile, content);

        Optional<ParsedFile> result = ParsedFileExtractor.of(tempFile).extract();
        assertTrue(result.isPresent());

        ParsedFile parsed = result.get();
        assertEquals("myapp.models", parsed.packageName());
        assertEquals(tempFile, parsed.path());
        assertEquals(List.of("utils.strings", "core.math"), parsed.imports());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testMissingPackageReturnsEmpty() throws IOException {
        String content = """
            import utils.strings

            class Person {
                var name: String
            }
            """;

        Path tempFile = Files.createTempFile("NoPackage", ".veny");
        Files.writeString(tempFile, content);

        Optional<ParsedFile> result = ParsedFileExtractor.of(tempFile).extract();
        assertTrue(result.isEmpty());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testNoImportsStillParses() throws IOException {
        String content = """
            package simple

            class Animal {
                var type: String
            }
            """;

        Path tempFile = Files.createTempFile("Animal", ".veny");
        Files.writeString(tempFile, content);

        Optional<ParsedFile> result = ParsedFileExtractor.of(tempFile).extract();
        assertTrue(result.isPresent());

        ParsedFile parsed = result.get();
        assertEquals("simple", parsed.packageName());
        assertTrue(parsed.imports().isEmpty());

        Files.deleteIfExists(tempFile);
    }
}
