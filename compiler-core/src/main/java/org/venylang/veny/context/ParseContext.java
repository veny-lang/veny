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

package org.venylang.veny.context;

import org.venylang.veny.util.source.SrcFilePosMap;

import java.nio.file.Path;
import java.util.Objects;

/**
 * ParseContext holds the shared state and configuration for the parsing process.
 * It encapsulates information such as source position mapping, error handling,
 * and other flags or services that may be needed throughout parsing.
 * <p>
 * Using a context object simplifies passing common dependencies across
 * parser components without cluttering method signatures.
 */
public final class ParseContext {

    private final String source;
    private final Path filePath;
    private final SrcFilePosMap srcFilePosMap;
    // Add other shared parser state or services here

    private ParseContext(Builder builder) {
        this.srcFilePosMap = Objects.requireNonNull(builder.srcFilePosMap, "SrcFilePosMap must not be null");
        this.source = Objects.requireNonNull(builder.source, "Source must not be null");
        this.filePath = Objects.requireNonNull(builder.filePath, "FilePath must not be null");
        // initialize other fields from builder
    }

    /**
     * Returns the source file position map used to convert offsets to positions.
     */
    public SrcFilePosMap srcFilePosMap() {
        return srcFilePosMap;
    }

    /**
     * Returns the source file path.
     */
    public Path filePath() {
        return filePath;
    }

    /**
     * Returns the source code.
     */
    public String source() {
        return source;
    }

    // Add other getters here

    /**
     * Creates a new builder for ParseContext.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ParseContext} to configure and construct instances.
     */
    public static class Builder {

        private String source;
        private Path filePath;
        private SrcFilePosMap srcFilePosMap;

        // Add builder fields here

        private Builder() {}

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        /**
         * Sets the source file position map.
         * @param srcFilePosMap the source file position map
         * @return this builder for chaining
         */
        public Builder srcFilePosMap(SrcFilePosMap srcFilePosMap) {
            this.srcFilePosMap = srcFilePosMap;
            return this;
        }


        // Add other builder setters here

        /**
         * Builds the {@link ParseContext} instance.
         * @return the constructed ParseContext
         * @throws NullPointerException if required fields are missing
         */
        public ParseContext build() {
            return new ParseContext(this);
        }
    }
}
