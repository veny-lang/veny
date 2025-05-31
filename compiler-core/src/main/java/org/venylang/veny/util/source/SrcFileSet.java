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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A SrcFileSet represents a set of source files, each assigned a unique
 * position range within a shared offset space.
 */
public class SrcFileSet {

    // List of source files in the order they were added
    private final List<SrcFilePosMap> files = new ArrayList<>();

    // Base offset for the next file (starts at 1, similar to Go)
    private int currentBase = 1;

    // Cached reference to the last file accessed
    private SrcFilePosMap last;

    /**
     * Returns the current base offset.
     *
     * @return current base offset
     */
    public int currentBase() {
        return currentBase;
    }

    /**
     * Returns the last file accessed in this set.
     *
     * @return last accessed SrcFilePosMap
     */
    public SrcFilePosMap last() {
        return last;
    }

    /**
     * Adds a new source file to the file set using the current base offset.
     * <p>
     * This is a convenience overload of {@link #addFile(String, int, int)} where the base
     * is implicitly set to the current base of the file set.
     * </p>
     * <p>
     * The size must be non-negative and represents the number of characters in the file,
     * including the final newline if present. The new file will be assigned a base offset,
     * and the internal base will be updated accordingly for the next file.
     * </p>
     *
     * @param fileName the name (or path) of the file being added
     * @param size the size of the file in characters
     * @return the position map for the newly added file
     * @throws IllegalStateException if the size is negative or if the computed base overflows
     */
    public SrcFilePosMap addFile(String fileName, int size) {
        return addFile(fileName, -1, size);
    }

    /**
     * Adds a new SrcFilePosMap to this file set.
     *
     * @param fileName the name of the source file
     * @param base     the base offset for the file; if negative, current base is used
     * @param size     the size of the file in bytes
     * @return the added SrcFilePosMap
     */
    public SrcFilePosMap addFile(String fileName, int base, int size) {
        if (base < 0) {
            base = currentBase;
        }

        if (base < currentBase || size < 0) {
            throw new IllegalStateException("Illegal base or size");
        }

        SrcFilePosMap f = new SrcFilePosMap(this, fileName, base, size, new int[]{0});
        int nextBase = base + size + 1; // +1 for EOF position

        if (nextBase < 0) {
            throw new IllegalStateException("Offset overflow (> 2GB of source code)");
        }

        currentBase = nextBase;
        files.add(f);
        last = f;
        return f;
    }

    /**
     * Iterates over the files in this set and applies the provided function.
     * Iteration stops if the function returns false.
     *
     * @param func a function that takes a SrcFilePosMap and returns a boolean
     */
    public void iterate(Function<SrcFilePosMap, Boolean> func) {
        for (SrcFilePosMap f : files) {
            if (!func.apply(f)) {
                break;
            }
        }
    }

    /**
     * Returns the file containing the given offset, or null if none exists.
     *
     * @param offset the position offset
     * @return the corresponding SrcFilePosMap, or null
     */
    public SrcFilePosMap fileAt(Offset offset) {
        return offset != Offset.NO_OFFSET ? file(offset) : null;
    }

    /**
     * Returns the Position for the given offset with position adjustment.
     *
     * @param offset the offset
     * @return corresponding Position
     */
    public Position position(Offset offset) {
        return positionFor(offset, true);
    }

    /**
     * Returns the Position for the given offset.
     *
     * @param offset   the offset
     * @param adjusted whether to account for adjusted line info
     * @return corresponding Position
     */
    public Position positionFor(Offset offset, boolean adjusted) {
        if (offset == Offset.NO_OFFSET) {
            return Position.NO_POSITION;
        }
        SrcFilePosMap f = file(offset);
        return f != null ? f.getPosition(offset, adjusted) : Position.NO_POSITION;
    }

    // Finds the SrcFilePosMap that contains the given offset
    private SrcFilePosMap file(Offset offset) {
        int value = offset.offset();

        // Check if the cached last file contains the offset
        if (last != null &&
                last.base() <= value &&
                value <= last.base() + last.size()) {
            return last;
        }

        // Perform search
        int i = searchFiles(files, value);
        if (i >= 0) {
            SrcFilePosMap f = files.get(i);
            if (value <= f.base() + f.size()) {
                last = f;
                return f;
            }
        }
        return null;
    }

    /**
     * Performs a binary search to find the index of the file in the list
     * whose base offset is the greatest value less than or equal to the given offset.
     * <p>
     * Returns -1 if no such file exists (i.e., if all file bases are greater than the offset).
     *
     * @param files list of SrcFilePosMap sorted by base offset in ascending order
     * @param offset the offset to search for
     * @return the index of the file with the greatest base <= offset, or -1 if none found
     */
    private int searchFiles(List<SrcFilePosMap> files, int offset) {
        int low = 0;
        int high = files.size() - 1;
        int result = -1; // default if none found

        while (low <= high) {
            int mid = low + (high - low) / 2;
            SrcFilePosMap f = files.get(mid);
            if (f.base() <= offset) {
                result = mid; // candidate file index
                low = mid + 1; // try to find a later file with base <= offset
            } else {
                high = mid - 1;
            }
        }
        return result;
    }
}
