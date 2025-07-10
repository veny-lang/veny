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

package org.venylang.veny.imports;

import org.venylang.veny.parser.ast.VenyFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for extracting {@link ImportRecord} instances from a {@link VenyFile}.
 * <p>
 * This class follows the object-oriented approach by binding the extractor to a specific
 * {@code VenyFile} instance. To create an instance, use the static factory method
 * {@link #of(VenyFile)}.
 */
public class ImportExtractor {

    /** The file from which to extract import statements. */
    private final VenyFile venyFile;

    /**
     * Private constructor to enforce use of the static factory method {@link #of(VenyFile)}.
     *
     * @param venyFile the file to extract imports from
     */
    private ImportExtractor(VenyFile venyFile) {
        this.venyFile = venyFile;
    }

    /**
     * Creates a new {@code ImportExtractor} for the specified {@link VenyFile}.
     *
     * @param venyFile the file to extract imports from
     * @return an {@code ImportExtractor} instance bound to the given file
     * @throws NullPointerException if {@code venyFile} is {@code null}
     */
    public static ImportExtractor of(VenyFile venyFile) {
        Objects.requireNonNull(venyFile, "venyFile must not be null");
        return new ImportExtractor(venyFile);
    }

    /**
     * Extracts a list of {@link ImportRecord} instances from the bound {@link VenyFile}.
     * <p>
     * Each import line is expected to be in the format {@code packageName.ClassName}.
     * If a line is malformed (e.g., missing a dot or ending with a dot), an
     * {@link ImportResolutionException} is thrown.
     *
     * @return a list of parsed {@link ImportRecord}s, never {@code null}
     * @throws ImportResolutionException if a malformed import is encountered
     */
    public List<ImportRecord> extract() throws ImportResolutionException {
        List<ImportRecord> imports = new ArrayList<>();

        for (String fullImport : venyFile.imports()) {
            int lastDot = fullImport.lastIndexOf('.');
            if (lastDot == -1 || lastDot == fullImport.length() - 1) {
                throw new ImportResolutionException(
                        "Malformed import: \"%s\" (expected <package>.<Class>)".formatted(fullImport));
            }

            String packageName = fullImport.substring(0, lastDot);
            String className   = fullImport.substring(lastDot + 1);
            imports.add(new ImportRecord(packageName, className));
        }

        return imports;
    }
}
