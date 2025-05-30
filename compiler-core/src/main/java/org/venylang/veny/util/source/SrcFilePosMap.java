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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code SrcFilePosMap} represents a source file managed by a {@code SrcFileSet}.
 * <p>
 * It stores file name, content size, and a list of line offsets to allow efficient
 * conversion between abstract file offsets and human-readable positions (line/column).
 * <p>
 * The file content itself is not stored; only metadata for position tracking is maintained.
 */
public class SrcFilePosMap {

    private final SrcFileSet set;
    private final String name;
    private final int base;
    private final int size;

    // List of offsets where each line starts in the file (0-based offset)
    private List<Integer> lines;

    // Optional adjustment info from position-altering comments like "//line"
    private final List<LineInfo> infos;

    public SrcFilePosMap(SrcFileSet set, String name, int base, int size, int[] lines) {
        this.set = set;
        this.name = name;
        this.base = base;
        this.size = size;
        this.lines = (lines != null)
                ? Arrays.stream(lines).boxed().collect(Collectors.toList())
                : new ArrayList<>();
        this.infos = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public int size() {
        return size;
    }

    public int base() {
        return base;
    }

    public int lineCount() {
        return lines.size();
    }

    public List<Integer> getLines() {
        return lines;
    }

    /**
     * Adds a new line offset to the table if it's valid and increasing.
     *
     * @param offset character offset of line start
     */
    public void addOffset(int offset) {
        int i = lines.size();
        if ((lines.isEmpty() || lines.get(i - 1) < offset) && offset < size) {
            lines.add(offset);
        }
    }

    /**
     * Merges the given line with the following line by removing the corresponding line offset.
     *
     * @param line the 1-based line number to merge
     */
    public void mergeLine(int line) {
        if (line <= 0) throw new IllegalArgumentException("Line must be > 0");
        if (line >= lines.size())
            throw new IllegalStateException("Illegal line number: " + line);
        lines.remove(line); // removes the offset of line+1
    }

    /**
     * Replaces the current line offset table with the given one if valid.
     *
     * @param lines new list of line start offsets
     * @return true if successfully validated and applied
     */
    public boolean setLines(ArrayList<Integer> lines) {
        if (lines == null) return false;
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i) <= lines.get(i - 1) || lines.get(i) >= size) {
                return false;
            }
        }
        this.lines = lines;
        return true;
    }

    /**
     * Populates line offset table from raw content bytes.
     *
     * @param content the file content as a byte array
     */
    public void setLinesForContent(byte[] content) {
        if (content == null) throw new IllegalArgumentException("content must not be null");

        ArrayList<Integer> result = new ArrayList<>();
        result.add(0); // line 1 always starts at offset 0

        for (int i = 0; i < content.length; i++) {
            if (content[i] == '\n' && i + 1 < content.length) {
                result.add(i + 1);
            }
        }

        this.lines = result;
    }

    /**
     * Returns an {@link Offset} object for the provided offset.
     *
     * @param offset file-relative offset
     * @return global Offset value
     */
    public Offset pos(int offset) {
        if (offset > size) throw new IllegalStateException("Illegal file offset");
        return new Offset(base + offset);
    }

    /**
     * Returns the offset into the file for a given {@code Offset}.
     *
     * @param p the Offset value
     * @return file-relative offset
     */
    public int offset(Offset p) {
        if (p == null) throw new IllegalArgumentException("Offset cannot be null");
        if (p.offset() < base || p.offset() > base + size)
            throw new IllegalStateException("Illegal Offset: " + p.offset());
        return p.offset() - base;
    }

    /**
     * Returns the 1-based line number for a given position.
     *
     * @param p position offset
     * @return line number
     */
    public int line(Offset p) {
        return position(p).line();
    }

    public Position position(Offset pos) {
        return positionFor(pos, true);
    }

    public Position positionFor(Offset pos, boolean adjusted) {
        if (pos == Offset.NO_OFFSET) return null;
        if (pos.offset() < base || pos.offset() > base + size)
            throw new IllegalStateException("Offset out of range: " + pos.offset());
        return getPosition(pos, adjusted);
    }

    protected Position getPosition(Offset pos, boolean adjusted) {
        int relOffset = pos.offset() - base;
        return unpack(relOffset, adjusted);
    }

    private Position unpack(int offset, boolean adjusted) {
        String fileName = this.name;
        int[] arr = lines.stream().mapToInt(Integer::intValue).toArray();
        int i = searchInts(arr, offset);
        int line = (i >= 0) ? i + 1 : 1;
        int column = (i >= 0) ? offset - lines.get(i) + 1 : offset + 1;

        if (adjusted && !infos.isEmpty()) {
            int idx = searchLineInfos(offset);
            if (idx >= 0) {
                LineInfo li = infos.get(idx);
                fileName = li.fileName();
                int baseLine = searchInts(arr, li.offset());
                if (baseLine >= 0) {
                    line += li.line() - baseLine - 1;
                }
            }
        }
        return new Position(fileName, line, column);
    }

    private int searchLineInfos(int x) {
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).offset() > x) {
                return i - 1;
            }
        }
        return infos.size() - 1;
    }

    protected int searchInts(int[] a, int x) {
        int i = 0, j = a.length;
        while (i < j) {
            int h = i + (j - i) / 2;
            if (a[h] <= x) {
                i = h + 1;
            } else {
                j = h;
            }
        }
        return i - 1;
    }

    public void addLineInfo(String fileName, int offset, int line) {
        if (infos.isEmpty() || (infos.get(infos.size() - 1).offset() < offset && offset < size)) {
            infos.add(new LineInfo(fileName, offset, line));
        }
    }
}
