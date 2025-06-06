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
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a source file to be compiled by the Veny compiler.
 * <p>
 * This class loads the contents of a file and verifies that it is valid UTF-8
 * without a Byte Order Mark (BOM). It stores both the file name and the
 * decoded source code string.
 */
public class SourceFile {
    private final String filename;
    private final String source;

    /**
     * Creates a new {@code SourceFile} instance by reading and validating the file at the given path.
     *
     * @param path the path to the source file
     * @return a {@code SourceFile} representing the file at the given path
     * @throws IOException if the file cannot be read or is not valid UTF-8
     */
    public static SourceFile of(Path path) throws IOException {
        return new SourceFile(path);
    }

    /**
     * Constructs a SourceFile by reading and validating the UTF-8 contents
     * of the given path.
     *
     * @param path the path to the source file
     * @throws IOException if the file cannot be read, is not valid UTF-8,
     *                     or contains a UTF-8 BOM or replacement characters
     */
    public SourceFile(Path path) throws IOException {
        this.filename = path.toString() ;
        this.source = readUtf8File(path);
    }

    /**
     * Returns the file name or path of this source file.
     *
     * @return the file name as a string
     */
    public String filename() {
        return filename;
    }

    /**
     * Returns the contents of the source file as a string.
     *
     * @return the source code in UTF-8
     */

    public String source() {
        return source;
    }

    /**
     * Reads the contents of the specified file, ensuring it is valid UTF-8,
     * does not contain a Byte Order Mark (BOM), and has no invalid characters.
     *
     * @param path the path to the file
     * @return the decoded UTF-8 content of the file
     * @throws IOException if the file is not valid UTF-8 or contains disallowed characters
     */
    private String readUtf8File(Path path) throws IOException {
        try {
            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);

            if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
                throw new IOException("UTF-8 BOM is not allowed: " + path);
            }

            if (content.contains("\uFFFD")) {
                throw new IOException("Invalid UTF-8 characters in file: " + path);
            }

            return content;
        } catch (MalformedInputException e) {
            throw new IOException("File is not valid UTF-8: " + path, e);
        }
    }
}
