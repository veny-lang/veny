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
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a source file to be compiled by the Veny compiler.
 *
 * <p>This class encapsulates the file name and its decoded UTF-8 source contents.
 * It validates that the content is well-formed UTF-8 and does not include a Byte Order Mark (BOM)
 * or replacement characters (�), ensuring the source code is safe to compile.</p>
 */
public class SourceFile {
    private final String filename;
    private final String source;

    /**
     * Creates a {@code SourceFile} from a file on disk.
     * The file is read as UTF-8 and validated for encoding correctness.
     *
     * @param path the path to the source file
     * @return a {@code SourceFile} instance with the decoded source
     * @throws IOException if the file cannot be read or is not valid UTF-8
     */
    public static SourceFile of(Path path) throws IOException {
        return new SourceFile(path.toString(), Files.readAllBytes(path));
    }

    /**
     * Creates a {@code SourceFile} from an input stream.
     * Intended for use when reading source files from packaged resources such as JARs.
     * The stream is fully read, decoded as UTF-8, and validated.
     *
     * @param filename a name or identifier for the source (used for diagnostics)
     * @param stream   the input stream to read the source from
     * @return a {@code SourceFile} instance with the decoded source
     * @throws IOException if the stream cannot be read or is not valid UTF-8
     */
    public static SourceFile of(String filename, InputStream stream) throws IOException {
        return new SourceFile(filename, stream.readAllBytes());
    }

    /**
     * Creates a {@code SourceFile} from an already-decoded in-memory string.
     * This method assumes the source is valid UTF-8 and skips validation.
     * Use this only if you are sure the string was correctly decoded.
     *
     * @param filename a name or identifier for the source
     * @param source   the source code as a string
     * @return a {@code SourceFile} instance
     */
    public static SourceFile of(String filename, String source) throws IOException {
        return new SourceFile(filename, source.getBytes(StandardCharsets.UTF_8));
    }

    private SourceFile(String filename, byte[] bytes) throws IOException {
        this.filename = filename;
        this.source = decodeUtf8Bytes(filename, bytes);
    }

    /**
     * Returns the name or path of the source file.
     *
     * @return the file name or identifier
     */
    public String filename() {
        return filename;
    }

    /**
     * Returns the decoded source code as a string.
     *
     * @return the UTF-8 decoded source code
     */
    public String source() {
        return source;
    }

    /**
     * Decodes the given byte array as UTF-8, checking for invalid input.
     * This ensures no BOM or invalid replacement characters are present.
     *
     * @param filename the logical name of the source (for error reporting)
     * @param bytes    the raw UTF-8 bytes
     * @return the decoded string
     * @throws IOException if the byte array contains invalid UTF-8
     */
    private String decodeUtf8Bytes(String filename, byte[] bytes) throws IOException {
        try {
            String content = new String(bytes, StandardCharsets.UTF_8);

            if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
                throw new IOException("UTF-8 BOM is not allowed: " + filename);
            }

            if (content.contains("\uFFFD")) {
                throw new IOException("Invalid UTF-8 characters in file: " + filename);
            }

            return content;
        } catch (MalformedInputException e) {
            throw new IOException("File is not valid UTF-8: " + filename, e);
        }
    }
}
