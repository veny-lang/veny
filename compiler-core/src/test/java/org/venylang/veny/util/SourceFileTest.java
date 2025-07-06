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
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SourceFileTest {

    @TempDir
    Path tempDir;

    @Test
    void testValidUtf8File() throws IOException {
        Path file = tempDir.resolve("valid.veny");
        String content = "print(\"Hello\")\n";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        SourceFile sourceFile = SourceFile.of(file);
        assertEquals("print(\"Hello\")\n", sourceFile.source());
        assertEquals(file.toString(), sourceFile.filename());
    }

    @Test
    void testUtf8BomRejected() throws IOException {
        Path file = tempDir.resolve("bom.veny");
        byte[] bomWithText = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF, 'p', 'r', 'i', 'n', 't' };
        Files.write(file, bomWithText);

        IOException ex = assertThrows(IOException.class, () -> SourceFile.of(file));
        assertTrue(ex.getMessage().contains("UTF-8 BOM"));
    }

    @Test
    void testUtf8ReplacementCharRejected() throws IOException {
        Path file = tempDir.resolve("invalid-char.veny");
        String content = "print(\"Hello�\")"; // contains \uFFFD
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        IOException ex = assertThrows(IOException.class, () -> SourceFile.of(file));
        assertTrue(ex.getMessage().contains("Invalid UTF-8 characters"));
    }

    @Test
    void testValidInputStreamSource() throws IOException {
        String content = "println(\"From jar\")\n";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            SourceFile sourceFile = SourceFile.of("jar://stdlib/Console.veny", in);
            assertEquals(content, sourceFile.source());
            assertEquals("jar://stdlib/Console.veny", sourceFile.filename());
        }
    }

    @Test
    void testInputStreamWithBomRejected() {
        byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF, 'f', 'o', 'o' };
        try (InputStream in = new ByteArrayInputStream(bom)) {
            IOException ex = assertThrows(IOException.class, () -> SourceFile.of("bad-file", in));
            assertTrue(ex.getMessage().contains("UTF-8 BOM"));
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
    }

    @Test
    void testFilenameExposedCorrectly() throws IOException {
        Path file = tempDir.resolve("simple.veny");
        Files.write(file, "x = 1".getBytes(StandardCharsets.UTF_8));

        SourceFile source = SourceFile.of(file);
        assertTrue(source.filename().endsWith("simple.veny"));
        assertEquals("x = 1", source.source());
    }

    @Test
    void testEmptyFileAllowed() throws IOException {
        Path file = tempDir.resolve("empty.veny");
        Files.write(file, new byte[0]);

        SourceFile source = SourceFile.of(file);
        assertEquals("", source.source());
    }

    @Test
    void testInputStreamWithInvalidUtf8Throws() {
        // Invalid UTF-8 sequence: lone 0xC3
        byte[] invalidUtf8 = new byte[] { (byte) 0xC3 };
        try (InputStream in = new ByteArrayInputStream(invalidUtf8)) {
            assertThrows(IOException.class, () -> SourceFile.of("invalid", in));
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
    }
}