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
package org.venylang.veny.util.source;

/**
 * Represents a compact encoding of a source position within a {@code SrcFileSet}.
 *
 * <p>The {@code Offset} acts as a global abstract offset, relative to a {@code SrcFileSet},
 * and can be mapped to a {@code Position} (filename, line, column) using a {@code SrcFileSet} or {@code SrcFilePosMap}.
 *
 * <p>An {@code Offset} of 0 is considered invalid and typically represents "no position".
 *
 * @param offset the integer offset relative to the beginning of the file set (starting from 1)
 */
public record Offset(int offset) implements Comparable<Offset> {

    /**
     * Represents an invalid or non-existent offset.
     * This is the default "zero" offset and cannot be mapped to a meaningful position.
     */
    public static final Offset NO_OFFSET = new Offset(0);

    /**
     * Checks whether this offset is valid (non-zero).
     *
     * @return true if the offset is greater than 0; false otherwise
     */
    public boolean isValid() {
        return offset > 0;
    }

    /**
     * Compares this offset with another offset.
     *
     * @param o the other Offset to compare with
     * @return -1 if this offset is less, 1 if greater, or 0 if equal
     */
    @Override
    public int compareTo(Offset o) {
        return Integer.compare(this.offset, o.offset);
    }
}

