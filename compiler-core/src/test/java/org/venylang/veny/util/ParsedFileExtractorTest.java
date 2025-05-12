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

        Path tempFile = Files.createTempFile("Person", ".lm");
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

        Path tempFile = Files.createTempFile("NoPackage", ".lm");
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

        Path tempFile = Files.createTempFile("Animal", ".lm");
        Files.writeString(tempFile, content);

        Optional<ParsedFile> result = ParsedFileExtractor.of(tempFile).extract();
        assertTrue(result.isPresent());

        ParsedFile parsed = result.get();
        assertEquals("simple", parsed.packageName());
        assertTrue(parsed.imports().isEmpty());

        Files.deleteIfExists(tempFile);
    }
}
